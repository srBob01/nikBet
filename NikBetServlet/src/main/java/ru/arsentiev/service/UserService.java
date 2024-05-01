package ru.arsentiev.service;

import ru.arsentiev.dto.user.controller.*;
import ru.arsentiev.entity.User;
import ru.arsentiev.exception.ServiceException;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.processing.password.PasswordHashed;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;
import ru.arsentiev.repository.UserRepository;
import ru.arsentiev.service.entity.user.ReturnValueInCheckLogin;
import ru.arsentiev.processing.validator.UpdateUserValidator;
import ru.arsentiev.processing.validator.entity.login.LoginError;
import ru.arsentiev.processing.validator.entity.update.UpdatePasswordError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class UserService {
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

    public UserControllerDto updateDescriptionUser(UserUpdateDescriptionControllerDto userUpdateDescriptionControllerDto,
                                                   UpdatedUserFields updatedUserFields,
                                                   UserConstFieldsControllerDto userConstFieldsControllerDto) {
        User user = userMapper.map(userUpdateDescriptionControllerDto);
        if (userRepository.updateDescriptionWithDynamicCreation(user, updatedUserFields)) {
            return userMapper.map(userConstFieldsControllerDto, userUpdateDescriptionControllerDto);
        } else {
            throw new ServiceException("The user does not exist");
        }
    }

    public ReturnValueInCheckLogin checkLogin(UserLoginControllerDto userLoginControllerDto) {
        User userSelectPassword = userRepository.selectPasswordByLogin(userLoginControllerDto.email());
        UserPasswordAndSaltControllerDto userPasswordAndSaltControllerDto = userMapper.mapUserToPasswordAndSaltController(userSelectPassword);
        if (userPasswordAndSaltControllerDto.password().isPresent() && userPasswordAndSaltControllerDto.salt().isPresent()) {
            if (userPasswordAndSaltControllerDto.password().get()
                    .equals(passwordHashed.hashPassword(userLoginControllerDto.password(),
                            userPasswordAndSaltControllerDto.salt().get()))) {
                User user = userRepository.selectByLogin(userLoginControllerDto.email());
                UserControllerDto userControllerDto = userMapper.map(user);
                return new ReturnValueInCheckLogin(null, Optional.of(userControllerDto));
            } else {
                return new ReturnValueInCheckLogin(LoginError.INCORRECT_PASSWORD, Optional.empty());
            }
        } else {
            return new ReturnValueInCheckLogin(LoginError.USER_NOT_FOUND, Optional.empty());
        }
    }

    public Optional<UpdatePasswordError> updatePassword(UserLogoPasControllerDto userLogoPasControllerDto) {
        User userSelectPassword = userRepository.selectPasswordByLogin(userLogoPasControllerDto.email());
        UserPasswordAndSaltControllerDto userPasswordAndSaltControllerDto = userMapper.mapUserToPasswordAndSaltController(userSelectPassword);
        if (userPasswordAndSaltControllerDto.password().isPresent() && userPasswordAndSaltControllerDto.salt().isPresent()) {
            if (userPasswordAndSaltControllerDto.password().get()
                    .equals(passwordHashed.hashPassword(userLogoPasControllerDto.oldPassword(),
                            userPasswordAndSaltControllerDto.salt().get()))) {
                Optional<UpdatePasswordError> error = updateUserValidator.isValidPassword(userLogoPasControllerDto
                        .newPassword());
                if (error.isPresent()) {
                    return error;
                }
                String salt = passwordHashed.generateSalt();
                User user = userMapper.map(userLogoPasControllerDto, salt, passwordHashed::hashPassword);
                if (userRepository.updatePasswordByLogin(user)) {
                    return Optional.empty();
                } else {
                    throw new ServiceException("The user has not been updated");
                }
            } else {
                return Optional.of(UpdatePasswordError.PASSWORDS_DONT_MATCH);
            }
        } else {
            throw new ServiceException("The user does not exist");
        }
    }

    public boolean depositMoney(UserMoneyControllerDto userMoneyControllerDto) {
        return userRepository.depositMoneyById(userMoneyControllerDto.idUser(), userMoneyControllerDto.summa());
    }

    public BigDecimal getAccountBalance(Long idUser) {
        var balance = userRepository.selectBalanceById(idUser);
        if (balance.isEmpty()) {
            throw new ServiceException("The user does not exist");
        }
        return balance.get();
    }

    public boolean withdrawMoney(UserMoneyControllerDto userMoneyControllerDto) {
        Optional<BigDecimal> balance = userRepository.selectBalanceById(userMoneyControllerDto.idUser());
        if (balance.isEmpty()) {
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
