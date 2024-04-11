package ru.arsentiev.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.jsp.user.UserRegSerConDto;
import ru.arsentiev.mapper.user.UserRegMapper;
import ru.arsentiev.singleton.password.SimplePasswordHashed;
import ru.arsentiev.repository.UserDao;
import ru.arsentiev.repository.manager.DaoManager;
import ru.arsentiev.service.entity.ReturnValueInInsertUser;
import ru.arsentiev.validator.RegistrationUserValidator;
import ru.arsentiev.validator.manager.ValidationManager;
import ru.arsentiev.validator.entity.ValidationResult;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserService {
    private static final UserService INSTANCE = new UserService();
    private static final UserRegMapper userRegMapper = UserRegMapper.getInstance();
    private static final SimplePasswordHashed simpleFunction = SimplePasswordHashed.getInstance();
    private static final UserDao userDao = DaoManager.getUserDao();
    private static final RegistrationUserValidator registrationUserValidator = ValidationManager.getRegistrationUserValidator();

    public static UserService getInstance() {
        return INSTANCE;
    }

    public ReturnValueInInsertUser insertUser(UserRegSerConDto userRegSerConDto) {
        ValidationResult result = registrationUserValidator.isValid(userRegSerConDto);
        if (result.isEmpty()) {
            var user = userRegMapper.map(userRegSerConDto, simpleFunction::hashPassword);
            userDao.insert(user);
            return new ReturnValueInInsertUser(null, user.getIdUser());
        } else {
            return new ReturnValueInInsertUser(result.getMyErrors(), -1L);
        }
    }
}
