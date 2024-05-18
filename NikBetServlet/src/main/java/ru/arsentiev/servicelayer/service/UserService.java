package ru.arsentiev.servicelayer.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.arsentiev.dto.user.controller.*;
import ru.arsentiev.dto.user.view.UserRegistrationViewDto;
import ru.arsentiev.dto.user.view.UserUpdateDescriptionViewDto;
import ru.arsentiev.dto.user.view.UserValidatorViewDto;
import ru.arsentiev.entity.User;
import ru.arsentiev.exception.ServiceException;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.processing.password.PasswordHashed;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;
import ru.arsentiev.repository.UserRepository;
import ru.arsentiev.servicelayer.service.entity.user.ReturnValueFromUpdateDescription;
import ru.arsentiev.servicelayer.validator.UserValidator;
import ru.arsentiev.servicelayer.validator.entity.load.LoadError;
import ru.arsentiev.servicelayer.validator.entity.load.LoadValidationResult;
import ru.arsentiev.servicelayer.validator.entity.login.LoginError;
import ru.arsentiev.servicelayer.validator.entity.update.UpdatePasswordError;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserService {
    private static final Logger logger = LogManager.getLogger();
    private final UserMapper userMapper;
    private final PasswordHashed passwordHashed;
    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserService(UserMapper userMapper, PasswordHashed passwordHashed, UserRepository userRepository, UserValidator userValidator) {
        this.userMapper = userMapper;
        this.passwordHashed = passwordHashed;
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    public List<LoadError> insertUser(UserRegistrationViewDto userRegistrationViewDto) {
        UserValidatorViewDto userValidatorViewDto = userMapper.mapRegistrationToValidator(userRegistrationViewDto);
        List<User> users = userRepository.selectAll();
        List<String> nicknames = users.stream().map(User::getNickname).toList();
        List<String> emails = users.stream().map(User::getEmail).toList();
        List<String> phoneNumbers = users.stream().map(User::getPhoneNumber).toList();
        userValidator.setToRegistration(nicknames, emails, phoneNumbers);
        LoadValidationResult loadValidationResult = userValidator.isValid(userValidatorViewDto);
        if (loadValidationResult.isEmpty()) {
            UserRegistrationControllerDto userRegistrationControllerDto = userMapper.map(userRegistrationViewDto);
            String salt = passwordHashed.generateSalt();
            User user = userMapper.map(userRegistrationControllerDto, salt, passwordHashed::hashPassword);
            userRepository.insert(user);
            return Collections.emptyList();
        } else {
            return loadValidationResult.getLoadErrors();
        }
    }

    public UserControllerDto selectUser(UserLoginControllerDto userLoginControllerDto) {
        User user = userRepository.selectByLogin(userLoginControllerDto.email());
        if (user != null) {
            return userMapper.map(user);
        } else {
            logger.error("User with" + userLoginControllerDto.email() + " not found!");
            throw new ServiceException("User not found!");
        }
    }

    public Optional<LoginError> checkLoginUser(UserLoginControllerDto userLoginControllerDto) {
        User userSelectPassword = userRepository.selectPasswordByLogin(userLoginControllerDto.email());
        if (userSelectPassword != null) {
            UserPasswordAndSaltControllerDto dto = userMapper.mapUserToPasswordAndSaltController(userSelectPassword);
            if (dto.password().equals(passwordHashed.hashPassword(userLoginControllerDto.password(), dto.salt()))) {
                return Optional.empty();
            } else {
                return Optional.of(LoginError.INCORRECT_PASSWORD);
            }
        } else {
            return Optional.of(LoginError.USER_NOT_FOUND);
        }
    }

    public ReturnValueFromUpdateDescription updateDescriptionUser(UserUpdateDescriptionViewDto userUpdateDescriptionViewDto,
                                                                  UpdatedUserFields updatedUserFields,
                                                                  UserConstFieldsControllerDto userConstFieldsControllerDto) {
        UserValidatorViewDto userValidatorViewDto = userMapper.mapUpdateToValidator(userUpdateDescriptionViewDto);
        List<User> users = userRepository.selectAll();
        List<String> nicknames = users.stream().map(User::getNickname).toList();
        List<String> emails = users.stream().map(User::getEmail).toList();
        List<String> phoneNumbers = users.stream().map(User::getPhoneNumber).toList();
        userValidator.setToUpdate(updatedUserFields, nicknames, emails, phoneNumbers);
        LoadValidationResult result = userValidator.isValid(userValidatorViewDto);
        if (result.isEmpty()) {
            UserUpdateDescriptionControllerDto userUpdateDescriptionControllerDto =
                    userMapper.map(userUpdateDescriptionViewDto);
            User user = userMapper.map(userUpdateDescriptionControllerDto);
            if (userRepository.updateDescriptionWithDynamicCreation(user, updatedUserFields)) {
                return ReturnValueFromUpdateDescription.builder()
                        .loadErrors(Collections.emptyList())
                        .userControllerDtoOptional(Optional.of(userMapper.map(userConstFieldsControllerDto, userUpdateDescriptionControllerDto)))
                        .build();
            } else {
                logger.error("Update user" + user.toString() + " failed");
                throw new ServiceException("Update failed");
            }
        } else {
            return ReturnValueFromUpdateDescription.builder()
                    .loadErrors(result.getLoadErrors())
                    .userControllerDtoOptional(Optional.empty())
                    .build();
        }

    }

    public Optional<UpdatePasswordError> updatePasswordUser(UserLogoPasControllerDto userLogoPasControllerDto) {
        User userSelectPassword = userRepository.selectPasswordByLogin(userLogoPasControllerDto.email());
        if (userSelectPassword != null) {
            UserPasswordAndSaltControllerDto dto = userMapper.mapUserToPasswordAndSaltController(userSelectPassword);
            if (dto.password().equals(passwordHashed.hashPassword(userLogoPasControllerDto.oldPassword(), dto.salt()))) {
                if (!userValidator.isValidPassword(userLogoPasControllerDto.newPassword())) {
                    return Optional.of(UpdatePasswordError.INCORRECT_NEW_PASSWORD);
                }
                String salt = passwordHashed.generateSalt();
                User user = userMapper.map(userLogoPasControllerDto, salt, passwordHashed::hashPassword);
                if (userRepository.updatePasswordByLogin(user)) {
                    return Optional.empty();
                } else {
                    logger.error("The user has not been updated");
                    throw new ServiceException("The user has not been updated");
                }
            } else {
                return Optional.of(UpdatePasswordError.PASSWORDS_DONT_MATCH);
            }
        } else {
            logger.error("The user with email " + userLogoPasControllerDto.email() + " does not exist");
            throw new ServiceException("The user does not exist");
        }
    }

    public boolean depositMoney(UserMoneyControllerDto userMoneyControllerDto) {
        return userRepository.depositMoneyById(userMoneyControllerDto.idUser(), userMoneyControllerDto.summa());
    }

    public BigDecimal getAccountBalance(Long idUser) {
        var balance = userRepository.selectBalanceById(idUser);
        if (balance.isEmpty()) {
            logger.error("The user with id" + idUser + " does not exist");
            throw new ServiceException("The user does not exist");
        }
        return balance.get();
    }

    public boolean withdrawMoney(UserMoneyControllerDto userMoneyControllerDto) {
        Optional<BigDecimal> balance = userRepository.selectBalanceById(userMoneyControllerDto.idUser());
        if (balance.isEmpty()) {
            logger.error("The user with id" + userMoneyControllerDto.idUser() + " does not exist");
            throw new ServiceException("The user does not exist");
        }

        if (balance.get().compareTo(userMoneyControllerDto.summa()) < 0) {
            return false;
        }
        return userRepository.withdrawMoneyById(userMoneyControllerDto.idUser(), userMoneyControllerDto.summa());
    }

    public List<UserForAdminControllerDto> selectAllUser() {
        return userRepository.selectAllOnlyUser().stream()
                .map(userMapper::mapUserToControllerForAdmin)
                .toList();
    }

    public boolean deleteUser(Long idUser) {
        return userRepository.delete(idUser);
    }

    public Optional<UserForAdminControllerDto> selectUserByNickname(String nickname) {
        return userRepository.selectByNickname(nickname).map(userMapper::mapUserToControllerForAdmin);
    }
}
