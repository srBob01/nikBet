package ru.arsentiev.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.user.controller.*;
import ru.arsentiev.entity.User;
import ru.arsentiev.manager.DaoManager;
import ru.arsentiev.manager.ValidationManager;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.repository.UserDao;
import ru.arsentiev.service.entity.user.ReturnValueInCheckLogin;
import ru.arsentiev.singleton.password.PasswordHasher;
import ru.arsentiev.singleton.query.entity.UpdatedUserFields;
import ru.arsentiev.validator.UpdateUserValidator;
import ru.arsentiev.validator.entity.login.LoginError;
import ru.arsentiev.validator.entity.update.UpdatePasswordError;

import java.math.BigDecimal;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserService {
    private static final UserService INSTANCE = new UserService();
    private final UserMapper userMapper = UserMapper.getInstance();
    private final PasswordHasher passwordHasher = PasswordHasher.getInstance();
    private final UserDao userDao = DaoManager.getUserDao();
    private final UpdateUserValidator updateUserValidator = ValidationManager.getUpdateUserValidator();

    public static UserService getInstance() {
        return INSTANCE;
    }

    public void insertUser(UserRegistrationControllerDto userRegistrationControllerDto) {
        User user = userMapper.map(userRegistrationControllerDto, passwordHasher::hashPassword);
        userDao.insert(user);
    }

    public UserControllerDto updateDescriptionUser(UserUpdateDescriptionControllerDto userUpdateDescriptionControllerDto,
                                                   UpdatedUserFields updatedUserFields,
                                                   UserConstFieldsControllerDto userConstFieldsControllerDto) {
        User user = userMapper.map(userUpdateDescriptionControllerDto);
        userDao.updateDescriptionWithDynamicCreation(user, updatedUserFields);
        return userMapper.map(userConstFieldsControllerDto, userUpdateDescriptionControllerDto);
    }

    public ReturnValueInCheckLogin checkLogin(UserLoginControllerDto userLoginControllerDto) {
        Optional<String> userPassword = userDao.selectPasswordByLogin(userLoginControllerDto.email());
        if (userPassword.isPresent()) {
            if (userPassword.get().equals(passwordHasher.hashPassword(userLoginControllerDto.password()))) {
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
        String login = userLogoPasControllerDto.email();
        Optional<String> password = userDao.selectPasswordByLogin(login);
        if (password.isEmpty()) {
            throw new RuntimeException();
        } else {
            if (!passwordHasher.hashPassword(userLogoPasControllerDto.oldPassword()).equals(password.get())) {
                return Optional.of(UpdatePasswordError.PASSWORDS_DONT_MATCH);
            }
            Optional<UpdatePasswordError> error = updateUserValidator.isValidPassword(userLogoPasControllerDto
                    .newPassword());
            if (error.isPresent()) {
                return error;
            }
            User user = userMapper.map(userLogoPasControllerDto, passwordHasher::hashPassword);
            userDao.updatePasswordByLogin(user);
            return Optional.empty();
        }
    }

    public void depositMoney(UserMoneyControllerDto userMoneyControllerDto) {
        userDao.depositMoneyById(userMoneyControllerDto);
    }

    public BigDecimal getAccountBalance(Long idUser) {
        var balance = userDao.selectBalanceById(idUser);
        if (balance.isEmpty()) {
            throw new RuntimeException();
        }
        return balance.get();
    }

    public boolean withdrawMoney(UserMoneyControllerDto userMoneyControllerDto) {
        Optional<BigDecimal> balance = userDao.selectBalanceById(userMoneyControllerDto.idUser());
        if (balance.isEmpty()) {
            throw new RuntimeException();
        }

        if (balance.get().compareTo(userMoneyControllerDto.summa()) < 0) {
            return false;
        }
        userDao.withdrawMoneyById(userMoneyControllerDto);
        return true;
    }
}
