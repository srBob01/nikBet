package ru.arsentiev.service;

import ru.arsentiev.dto.user.controller.*;
import ru.arsentiev.entity.User;
import ru.arsentiev.exception.ServiceException;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.processing.password.PasswordHashed;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;
import ru.arsentiev.repository.UserDao;
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
    private final UserDao userDao;
    private final UpdateUserValidator updateUserValidator;

    public UserService(UserMapper userMapper, PasswordHashed passwordHashed, UserDao userDao,
                       UpdateUserValidator updateUserValidator) {
        this.userMapper = userMapper;
        this.passwordHashed = passwordHashed;
        this.userDao = userDao;
        this.updateUserValidator = updateUserValidator;
    }

    public void insertUser(UserRegistrationControllerDto userRegistrationControllerDto) {
        String salt = passwordHashed.generateSalt();
        User user = userMapper.map(userRegistrationControllerDto, salt, passwordHashed::hashPassword);
        userDao.insert(user);
    }

    public UserControllerDto updateDescriptionUser(UserUpdateDescriptionControllerDto userUpdateDescriptionControllerDto,
                                                   UpdatedUserFields updatedUserFields,
                                                   UserConstFieldsControllerDto userConstFieldsControllerDto) {
        User user = userMapper.map(userUpdateDescriptionControllerDto);
        if (userDao.updateDescriptionWithDynamicCreation(user, updatedUserFields)) {
            return userMapper.map(userConstFieldsControllerDto, userUpdateDescriptionControllerDto);
        } else {
            throw new ServiceException("The user does not exist");
        }
    }

    public ReturnValueInCheckLogin checkLogin(UserLoginControllerDto userLoginControllerDto) {
        UserPasswordAndSaltControllerDto userPasswordAndSaltControllerDto =
                userDao.selectPasswordByLogin(userLoginControllerDto.email());
        if (userPasswordAndSaltControllerDto.password().isPresent() && userPasswordAndSaltControllerDto.salt().isPresent()) {
            if (userPasswordAndSaltControllerDto.password().get()
                    .equals(passwordHashed.hashPassword(userLoginControllerDto.password(),
                            userPasswordAndSaltControllerDto.salt().get()))) {
                User user = userDao.selectByLogin(userLoginControllerDto.email());
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
        UserPasswordAndSaltControllerDto userPasswordAndSaltControllerDto =
                userDao.selectPasswordByLogin(userLogoPasControllerDto.email());
        if (userPasswordAndSaltControllerDto.password().isPresent()
            && userPasswordAndSaltControllerDto.salt().isPresent()) {
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
                if (userDao.updatePasswordByLogin(user)) {
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
        return userDao.depositMoneyById(userMoneyControllerDto);
    }

    public BigDecimal getAccountBalance(Long idUser) {
        var balance = userDao.selectBalanceById(idUser);
        if (balance.isEmpty()) {
            throw new ServiceException("The user does not exist");
        }
        return balance.get();
    }

    public boolean withdrawMoney(UserMoneyControllerDto userMoneyControllerDto) {
        Optional<BigDecimal> balance = userDao.selectBalanceById(userMoneyControllerDto.idUser());
        if (balance.isEmpty()) {
            throw new ServiceException("The user does not exist");
        }

        if (balance.get().compareTo(userMoneyControllerDto.summa()) < 0) {
            return false;
        }
        return userDao.withdrawMoneyById(userMoneyControllerDto);
    }

    public List<UserForAdminControllerDto> selectAllUser() {
        return userDao.selectAll().stream()
                .map(userMapper::mapUserToControllerForAdmin)
                .toList();
    }

    public boolean deleteUser(Long idUser) {
        return userDao.delete(idUser);
    }

    public Optional<UserForAdminControllerDto> selectUserByNickname(String nickname) {
        return userDao.selectByNickname(nickname).map(userMapper::mapUserToControllerForAdmin);
    }
}
