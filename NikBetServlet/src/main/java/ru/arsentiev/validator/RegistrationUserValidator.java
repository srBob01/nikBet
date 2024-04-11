package ru.arsentiev.validator;

import ru.arsentiev.singleton.check.NameCheck;
import ru.arsentiev.dto.jsp.user.UserRegSerConDto;
import ru.arsentiev.repository.UserExistsDao;
import ru.arsentiev.utils.LocalDateFormatter;
import ru.arsentiev.singleton.check.DateCheck;
import ru.arsentiev.singleton.check.PasswordCheck;
import ru.arsentiev.singleton.check.PhoneNumberCheck;
import ru.arsentiev.validator.entity.MyError;
import ru.arsentiev.validator.entity.TypeError;
import ru.arsentiev.validator.entity.ValidationResult;

import java.time.LocalDate;
import java.time.Period;

public class RegistrationUserValidator implements Validator<UserRegSerConDto> {
    private final UserExistsDao userExistsDao;
    private final DateCheck dateCheck;
    private final PasswordCheck passwordCheck;
    private final PhoneNumberCheck phoneNumberCheck;
    private final NameCheck nameCheck;

    public RegistrationUserValidator(UserExistsDao userExistsDao, DateCheck dateCheck, PasswordCheck passwordCheck,
                                     PhoneNumberCheck phoneNumberCheck, NameCheck nameCheck) {
        this.userExistsDao = userExistsDao;
        this.dateCheck = dateCheck;
        this.passwordCheck = passwordCheck;
        this.phoneNumberCheck = phoneNumberCheck;
        this.nameCheck = nameCheck;
    }

    @Override
    public ValidationResult isValid(UserRegSerConDto obj) {
        ValidationResult result = new ValidationResult();

        if (obj.getNickname() == null || obj.getNickname().trim().isEmpty()) {
            result.add(MyError.of("nickname", TypeError.EMPTY));
        } else if (userExistsDao.existsByNickname(obj.getNickname())) {
            result.add(MyError.of("nickname", TypeError.NON_UNIQUE));
        }

        if (obj.getFirstName() == null || obj.getFirstName().trim().isEmpty()) {
            result.add(MyError.of("firstName", TypeError.EMPTY));
        } else if (!nameCheck.check(obj.getFirstName())) {
            result.add(MyError.of("firstName", TypeError.INCORRECT));
        }


        if (obj.getLastName() == null || obj.getLastName().trim().isEmpty()) {
            result.add(MyError.of("lastName", TypeError.EMPTY));
        } else if (!nameCheck.check(obj.getLastName())) {
            result.add(MyError.of("lastName", TypeError.INCORRECT));
        }


        if (obj.getPassword() == null || obj.getPassword().trim().isEmpty()) {
            result.add(MyError.of("password", TypeError.EMPTY));
        } else if (!passwordCheck.check(obj.getPassword())) {
            result.add(MyError.of("password", TypeError.INCORRECT));
        }


        if (obj.getPhoneNumber() == null || obj.getPhoneNumber().trim().isEmpty()) {
            result.add(MyError.of("phoneNumber", TypeError.EMPTY));
        } else if (!phoneNumberCheck.check(obj.getPhoneNumber())) {
            result.add(MyError.of("phoneNumber", TypeError.INCORRECT));
        } else if (userExistsDao.existsByPhoneNumber(obj.getPhoneNumber())) {
            result.add(MyError.of("phoneNumber", TypeError.NON_UNIQUE));
        }


        if (obj.getEmail() == null || obj.getEmail().trim().isEmpty()) {
            result.add(MyError.of("email", TypeError.EMPTY));
        } else if (userExistsDao.existsByEmail(obj.getEmail())) {
            result.add(MyError.of("email", TypeError.NON_UNIQUE));
        }

        if (obj.getBirthDate() == null || obj.getBirthDate().trim().isEmpty()) {
            result.add(MyError.of("birthDate", TypeError.EMPTY));
        } else if (!dateCheck.check(obj.getBirthDate())) {
            result.add(MyError.of("birthDate", TypeError.INCORRECT));
        } else {
            Period period = Period.between(LocalDateFormatter.format(obj.getBirthDate()), LocalDate.now());
            if (period.getYears() < 18) {
                result.add(MyError.of("birthDate", TypeError.YOUNG_USER));
            }
        }

        return result;
    }
}
