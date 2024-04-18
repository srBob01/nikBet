package ru.arsentiev.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.user.*;
import ru.arsentiev.entity.User;
import ru.arsentiev.manager.DaoManager;
import ru.arsentiev.manager.ValidationManager;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.repository.UserDao;
import ru.arsentiev.service.entity.user.ReturnValueInCheckLogin;
import ru.arsentiev.service.entity.user.ReturnValueOnUpdateDescription;
import ru.arsentiev.singleton.password.PasswordHasher;
import ru.arsentiev.validator.MoneyValidator;
import ru.arsentiev.validator.RegistrationUserValidator;
import ru.arsentiev.validator.UpdateUserValidator;
import ru.arsentiev.validator.entity.load.LoadError;
import ru.arsentiev.validator.entity.load.LoadValidationResult;
import ru.arsentiev.validator.entity.login.LoginError;
import ru.arsentiev.validator.entity.money.MoneyError;
import ru.arsentiev.validator.entity.update.UpdatePasswordError;
import ru.arsentiev.validator.entity.update.UpdatedUserFields;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserService {
    private static final UserService INSTANCE = new UserService();
    private final UserMapper userMapper = UserMapper.getInstance();
    private final PasswordHasher passwordHasher = PasswordHasher.getInstance();
    private final UserDao userDao = DaoManager.getUserDao();
    private final RegistrationUserValidator registrationUserValidator = ValidationManager.getRegistrationUserValidator();
    private final UpdateUserValidator updateUserValidator = ValidationManager.getUpdateUserValidator();
    private final MoneyValidator moneyValidator = ValidationManager.getMoneyValidator();

    public static UserService getInstance() {
        return INSTANCE;
    }

    public List<LoadError> insertUser(UserRegistrationDto userRegistrationDto) {
        LoadValidationResult result = registrationUserValidator.isValid(userRegistrationDto);
        if (result.isEmpty()) {
            User user = userMapper.map(userRegistrationDto, passwordHasher::hashPassword);
            userDao.insert(user);
            return List.of();
        } else {
            return result.getLoadErrors();
        }
    }

    public ReturnValueOnUpdateDescription updateDescriptionUser(UserUpdateDescriptionDto userUpdateDescriptionDto,
                                                                UpdatedUserFields updatedUserFields,
                                                                UserConstFieldsDto userConstFieldsDto) {
        LoadValidationResult result = updateUserValidator.isValidDescription(userUpdateDescriptionDto, updatedUserFields);
        if (result.isEmpty()) {
            User user = userMapper.map(userUpdateDescriptionDto);
            userDao.updateDescriptionWithDynamicCreation(user, updatedUserFields);
            UserDto userDto = userMapper.map(userConstFieldsDto, userUpdateDescriptionDto);
            return new ReturnValueOnUpdateDescription(List.of(), userDto);
        } else {
            return new ReturnValueOnUpdateDescription(result.getLoadErrors(), null);
        }
    }

    public ReturnValueInCheckLogin checkLogin(UserViewLoginDto userViewLoginDto) {
        UserControllerLoginDto userControllerLoginDto =
                new UserControllerLoginDto(userViewLoginDto.email());

        Optional<String> userPassword = userDao.selectPasswordByLogin(userControllerLoginDto.email());
        if (userPassword.isPresent()) {
            if (userPassword.get().equals(passwordHasher.hashPassword(userViewLoginDto.password()))) {
                User user = userDao.selectByLogin(userControllerLoginDto.email());
                UserDto userDto = userMapper.map(user);
                return new ReturnValueInCheckLogin(null, Optional.of(userDto));
            } else {
                return new ReturnValueInCheckLogin(LoginError.INCORRECT_PASSWORD, Optional.empty());
            }
        } else {
            return new ReturnValueInCheckLogin(LoginError.USER_NOT_FOUND, Optional.empty());
        }
    }

    public Optional<UpdatePasswordError> updatePassword(UserViewLogoPasDto userViewLogoPasDto) {
        String login = userViewLogoPasDto.email();
        Optional<String> password = userDao.selectPasswordByLogin(login);
        if (password.isEmpty()) {
            throw new RuntimeException();
        } else {
            if (!passwordHasher.hashPassword(userViewLogoPasDto.oldPassword()).equals(password.get())) {
                return Optional.of(UpdatePasswordError.PASSWORDS_DONT_MATCH);
            }
            Optional<UpdatePasswordError> error = updateUserValidator.isValidPassword(userViewLogoPasDto
                    .newPassword());
            if (error.isPresent()) {
                return error;
            }
            UserLogoPasDto userLogoPasDto = userMapper.map(userViewLogoPasDto, passwordHasher::hashPassword);
            userDao.updatePasswordByLogin(userLogoPasDto);
            return Optional.empty();
        }
    }

    public Optional<MoneyError> depositMoney(UserMoneyViewDto userMoneyViewDto) {
        if (!moneyValidator.isValidMoney(userMoneyViewDto.summa())) {
            return Optional.of(MoneyError.INCORRECT_VALUE);
        }
        UserMoneyControllerDto userMoneyControllerDto = userMapper.map(userMoneyViewDto);
        userDao.depositMoneyById(userMoneyControllerDto);
        return Optional.empty();
    }

    public String getAccountBalance(Long idUser) {
        var balance = userDao.selectBalanceById(idUser);
        if (balance.isEmpty()) {
            throw new RuntimeException();
        }
        return balance.get().toString();
    }

    public Optional<MoneyError> withdrawMoney(UserMoneyViewDto userMoneyViewDto) {
        if (!moneyValidator.isValidMoney(userMoneyViewDto.summa())) {
            return Optional.of(MoneyError.INCORRECT_VALUE);
        }
        UserMoneyControllerDto userMoneyControllerDto = userMapper.map(userMoneyViewDto);
        var balance = userDao.selectBalanceById(userMoneyControllerDto.idUser());
        if (balance.isEmpty()) {
            throw new RuntimeException();
        }

        if (balance.get().compareTo(userMoneyControllerDto.summa()) < 0) {
            return Optional.of(MoneyError.INSUFFICIENT_FUNDS);
        }
        userDao.withdrawMoneyById(userMoneyControllerDto);
        return Optional.empty();
    }

    public boolean checkSummaPrediction(UserPredictionSummaDto userPredictionSummaDto) {
        var balance = userDao.selectBalanceById(userPredictionSummaDto.idUser());
        if (balance.isEmpty()) {
            throw new RuntimeException();
        }
        BigDecimal summa = BigDecimal.valueOf(Double.parseDouble(userPredictionSummaDto.summa()));
        return balance.get().compareTo(summa) >= 0;
    }
}
