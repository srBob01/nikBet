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
import ru.arsentiev.singleton.password.PasswordHashed;
import ru.arsentiev.singleton.query.entity.UpdatedUserFields;
import ru.arsentiev.validator.UpdateUserValidator;
import ru.arsentiev.validator.entity.login.LoginError;
import ru.arsentiev.validator.entity.update.UpdatePasswordError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserService {
    private static final UserService INSTANCE = new UserService();
    private final UserMapper userMapper = UserMapper.getInstance();
    private final PasswordHashed passwordHashed = PasswordHashed.getInstance();
    private final UserDao userDao = DaoManager.getUserDao();
    private final UpdateUserValidator updateUserValidator = ValidationManager.getUpdateUserValidator();

    public static UserService getInstance() {
        return INSTANCE;
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
        userDao.updateDescriptionWithDynamicCreation(user, updatedUserFields);
        return userMapper.map(userConstFieldsControllerDto, userUpdateDescriptionControllerDto);
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
                userDao.updatePasswordByLogin(user);
                return Optional.empty();
            } else {
                return Optional.of(UpdatePasswordError.PASSWORDS_DONT_MATCH);
            }
        } else {
            throw new RuntimeException();
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
