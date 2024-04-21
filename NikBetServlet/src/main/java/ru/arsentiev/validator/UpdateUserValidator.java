package ru.arsentiev.validator;

import ru.arsentiev.dto.user.view.UserUpdateDescriptionViewDto;
import ru.arsentiev.repository.UserExistsDao;
import ru.arsentiev.singleton.check.DateCheck;
import ru.arsentiev.singleton.check.NameCheck;
import ru.arsentiev.singleton.check.PasswordCheck;
import ru.arsentiev.singleton.check.PhoneNumberCheck;
import ru.arsentiev.singleton.query.entity.UpdatedUserFields;
import ru.arsentiev.utils.LocalDateFormatter;
import ru.arsentiev.validator.entity.load.LoadError;
import ru.arsentiev.validator.entity.load.LoadValidationResult;
import ru.arsentiev.validator.entity.load.TypeLoadError;
import ru.arsentiev.validator.entity.update.UpdatePasswordError;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

public class UpdateUserValidator {
    private final UserExistsDao userExistsDao;
    private final DateCheck dateCheck;
    private final PhoneNumberCheck phoneNumberCheck;
    private final NameCheck nameCheck;
    private final PasswordCheck passwordCheck;

    public UpdateUserValidator(UserExistsDao userExistsDao, DateCheck dateCheck, PhoneNumberCheck phoneNumberCheck, NameCheck nameCheck, PasswordCheck passwordCheck) {
        this.userExistsDao = userExistsDao;
        this.dateCheck = dateCheck;
        this.phoneNumberCheck = phoneNumberCheck;
        this.nameCheck = nameCheck;
        this.passwordCheck = passwordCheck;
    }

    public LoadValidationResult isValidDescription(UserUpdateDescriptionViewDto obj, UpdatedUserFields updatedUserFields) {
        LoadValidationResult result = new LoadValidationResult();

        if (updatedUserFields.isNewNickName()) {
            if (obj.getNickname().trim().isEmpty()) {
                result.add(LoadError.of("nickname", TypeLoadError.EMPTY));
            } else if (userExistsDao.existsByNickname(obj.getNickname())) {
                result.add(LoadError.of("nickname", TypeLoadError.NON_UNIQUE));
            }
        }

        if (updatedUserFields.isNewFirstName()) {
            if (obj.getFirstName().trim().isEmpty()) {
                result.add(LoadError.of("firstName", TypeLoadError.EMPTY));
            } else if (nameCheck.isIncorrect(obj.getFirstName())) {
                result.add(LoadError.of("firstName", TypeLoadError.INCORRECT));
            }
        }

        if (updatedUserFields.isNewLastName()) {
            if (obj.getLastName().trim().isEmpty()) {
                result.add(LoadError.of("lastName", TypeLoadError.EMPTY));
            } else if (nameCheck.isIncorrect(obj.getLastName())) {
                result.add(LoadError.of("lastName", TypeLoadError.INCORRECT));
            }
        }

        if (updatedUserFields.isNewPhoneNumber()) {
            if (obj.getPhoneNumber().trim().isEmpty()) {
                result.add(LoadError.of("phoneNumber", TypeLoadError.EMPTY));
            } else if (phoneNumberCheck.isIncorrect(obj.getPhoneNumber())) {
                result.add(LoadError.of("phoneNumber", TypeLoadError.INCORRECT));
            } else if (userExistsDao.existsByPhoneNumber(obj.getPhoneNumber())) {
                result.add(LoadError.of("phoneNumber", TypeLoadError.NON_UNIQUE));
            }
        }

        if (updatedUserFields.isNewEmail()) {
            if (obj.getEmail().trim().isEmpty()) {
                result.add(LoadError.of("email", TypeLoadError.EMPTY));
            } else if (userExistsDao.existsByEmail(obj.getEmail())) {
                result.add(LoadError.of("email", TypeLoadError.NON_UNIQUE));
            }
        }
        if (updatedUserFields.isNewBirthDate()) {
            if (obj.getBirthDate().trim().isEmpty()) {
                result.add(LoadError.of("birthDate", TypeLoadError.EMPTY));
            } else if (!dateCheck.isCorrect(obj.getBirthDate())) {
                result.add(LoadError.of("birthDate", TypeLoadError.INCORRECT));
            } else {
                Period period = Period.between(LocalDateFormatter.format(obj.getBirthDate()), LocalDate.now());
                if (period.getYears() < 18) {
                    result.add(LoadError.of("birthDate", TypeLoadError.YOUNG_USER));
                }
            }
        }

        return result;
    }

    public Optional<UpdatePasswordError> isValidPassword(String password) {
        if (password == null || password.trim().isEmpty() || passwordCheck.isIncorrect(password)) {
            return Optional.of(UpdatePasswordError.INCORRECT_NEW_PASSWORD);
        }
        return Optional.empty();
    }
}
