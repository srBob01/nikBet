package ru.arsentiev.servicelayer.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.arsentiev.dto.user.controller.*;
import ru.arsentiev.entity.User;
import ru.arsentiev.exception.ServiceException;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.processing.password.PasswordHashed;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;
import ru.arsentiev.repository.UserRepository;
import ru.arsentiev.servicelayer.validator.UpdateUserValidator;
import ru.arsentiev.servicelayer.validator.entity.login.LoginError;
import ru.arsentiev.servicelayer.validator.entity.update.UpdatePasswordError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class UserService {
    private static final Logger logger = LogManager.getLogger();
    private final UserMapper userMapper;
    private final PasswordHashed passwordHashed;
    private final UserRepository userRepository;
    private final UpdateUserValidator updateUserValidator;

    public UserService(UserMapper userMapper, PasswordHashed passwordHashed, UserRepository userRepository,
                       UpdateUserValidator updateUserValidator) {
        this.userMapper = userMapper;
        this.passwordHashed = passwordHashed;
        this.userRepository = userRepository;
        this.updateUserValidator = updateUserValidator;
    }

    public void insertUser(UserRegistrationControllerDto userRegistrationControllerDto) {
        String salt = passwordHashed.generateSalt();
        User user = userMapper.map(userRegistrationControllerDto, salt, passwordHashed::hashPassword);
        userRepository.insert(user);
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

    public UserControllerDto updateDescriptionUser(UserUpdateDescriptionControllerDto userUpdateDescriptionControllerDto,
                                                   UpdatedUserFields updatedUserFields,
                                                   UserConstFieldsControllerDto userConstFieldsControllerDto) {
        User user = userMapper.map(userUpdateDescriptionControllerDto);
        if (userRepository.updateDescriptionWithDynamicCreation(user, updatedUserFields)) {
            return userMapper.map(userConstFieldsControllerDto, userUpdateDescriptionControllerDto);
        } else {
            logger.error("Update user" + user.toString() + " failed");
            throw new ServiceException("Update failed");
        }
    }

    public Optional<UpdatePasswordError> updatePasswordUser(UserLogoPasControllerDto userLogoPasControllerDto) {
        User userSelectPassword = userRepository.selectPasswordByLogin(userLogoPasControllerDto.email());
        if (userSelectPassword != null) {
            UserPasswordAndSaltControllerDto dto = userMapper.mapUserToPasswordAndSaltController(userSelectPassword);
            if (dto.password().equals(passwordHashed.hashPassword(userLogoPasControllerDto.oldPassword(), dto.salt()))) {
                if (!updateUserValidator.isValidPassword(userLogoPasControllerDto.newPassword())) {
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
        return userRepository.selectAll().stream()
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
