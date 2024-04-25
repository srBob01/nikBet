package ru.arsentiev.processing.validator;

import ru.arsentiev.dto.user.view.UserRegistrationViewDto;
import ru.arsentiev.repository.UserExistsDao;
import ru.arsentiev.processing.check.DateCheck;
import ru.arsentiev.processing.check.NameCheck;
import ru.arsentiev.processing.check.PasswordCheck;
import ru.arsentiev.processing.check.PhoneNumberCheck;
import ru.arsentiev.processing.dateformatter.LocalDateFormatter;
import ru.arsentiev.processing.validator.entity.load.LoadError;
import ru.arsentiev.processing.validator.entity.load.LoadValidationResult;
import ru.arsentiev.processing.validator.entity.load.TypeLoadError;

import java.time.LocalDate;
import java.time.Period;

public class RegistrationUserValidator {
    private final UserExistsDao userExistsDao;
    private final DateCheck dateCheck;
    private final PasswordCheck passwordCheck;
    private final PhoneNumberCheck phoneNumberCheck;
    private final NameCheck nameCheck;
    private final LocalDateFormatter localDateFormatter;

    public RegistrationUserValidator(UserExistsDao userExistsDao, DateCheck dateCheck, PasswordCheck passwordCheck,
                                     PhoneNumberCheck phoneNumberCheck, NameCheck nameCheck, LocalDateFormatter localDateFormatter) {
        this.userExistsDao = userExistsDao;
        this.dateCheck = dateCheck;
        this.passwordCheck = passwordCheck;
        this.phoneNumberCheck = phoneNumberCheck;
        this.nameCheck = nameCheck;
        this.localDateFormatter = localDateFormatter;
    }

    public LoadValidationResult isValid(UserRegistrationViewDto obj) {
        LoadValidationResult result = new LoadValidationResult();

        if (obj.getNickname() == null || obj.getNickname().trim().isEmpty()) {
            result.add(LoadError.of("nickname", TypeLoadError.EMPTY));
        } else if (userExistsDao.existsByNickname(obj.getNickname())) {
            result.add(LoadError.of("nickname", TypeLoadError.NON_UNIQUE));
        }

        if (obj.getFirstName() == null || obj.getFirstName().trim().isEmpty()) {
            result.add(LoadError.of("firstName", TypeLoadError.EMPTY));
        } else if (nameCheck.isIncorrect(obj.getFirstName())) {
            result.add(LoadError.of("firstName", TypeLoadError.INCORRECT));
        }


        if (obj.getLastName() == null || obj.getLastName().trim().isEmpty()) {
            result.add(LoadError.of("lastName", TypeLoadError.EMPTY));
        } else if (nameCheck.isIncorrect(obj.getLastName())) {
            result.add(LoadError.of("lastName", TypeLoadError.INCORRECT));
        }


        if (obj.getPassword() == null || obj.getPassword().trim().isEmpty()) {
            result.add(LoadError.of("password", TypeLoadError.EMPTY));
        } else if (passwordCheck.isIncorrect(obj.getPassword())) {
            result.add(LoadError.of("password", TypeLoadError.INCORRECT));
        }


        if (obj.getPhoneNumber() == null || obj.getPhoneNumber().trim().isEmpty()) {
            result.add(LoadError.of("phoneNumber", TypeLoadError.EMPTY));
        } else if (phoneNumberCheck.isIncorrect(obj.getPhoneNumber())) {
            result.add(LoadError.of("phoneNumber", TypeLoadError.INCORRECT));
        } else if (userExistsDao.existsByPhoneNumber(obj.getPhoneNumber())) {
            result.add(LoadError.of("phoneNumber", TypeLoadError.NON_UNIQUE));
        }


        if (obj.getEmail() == null || obj.getEmail().trim().isEmpty()) {
            result.add(LoadError.of("email", TypeLoadError.EMPTY));
        } else if (userExistsDao.existsByEmail(obj.getEmail())) {
            result.add(LoadError.of("email", TypeLoadError.NON_UNIQUE));
        }

        if (obj.getBirthDate() == null || obj.getBirthDate().trim().isEmpty()) {
            result.add(LoadError.of("birthDate", TypeLoadError.EMPTY));
        } else if (!dateCheck.isCorrect(obj.getBirthDate())) {
            result.add(LoadError.of("birthDate", TypeLoadError.INCORRECT));
        } else {
            Period period = Period.between(localDateFormatter.format(obj.getBirthDate()), LocalDate.now());
            if (period.getYears() < 18) {
                result.add(LoadError.of("birthDate", TypeLoadError.YOUNG_USER));
            }
        }

        return result;
    }
}
