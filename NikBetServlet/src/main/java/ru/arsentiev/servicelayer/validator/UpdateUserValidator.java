package ru.arsentiev.servicelayer.validator;

import ru.arsentiev.dto.user.view.UserUpdateDescriptionViewDto;
import ru.arsentiev.processing.check.DateCheck;
import ru.arsentiev.processing.check.NameCheck;
import ru.arsentiev.processing.check.PasswordCheck;
import ru.arsentiev.processing.check.PhoneNumberCheck;
import ru.arsentiev.processing.dateformatter.LocalDateFormatter;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;
import ru.arsentiev.repository.UserExistsRepository;
import ru.arsentiev.servicelayer.validator.entity.load.LoadError;
import ru.arsentiev.servicelayer.validator.entity.load.LoadValidationResult;
import ru.arsentiev.servicelayer.validator.entity.load.TypeLoadError;

import java.time.LocalDate;
import java.time.Period;

public class UpdateUserValidator {
    private final UserExistsRepository userExistsRepository;
    private final DateCheck dateCheck;
    private final PhoneNumberCheck phoneNumberCheck;
    private final NameCheck nameCheck;
    private final PasswordCheck passwordCheck;
    private final LocalDateFormatter localDateFormatter;

    public UpdateUserValidator(UserExistsRepository userExistsRepository, DateCheck dateCheck, PhoneNumberCheck phoneNumberCheck, NameCheck nameCheck, PasswordCheck passwordCheck, LocalDateFormatter localDateFormatter) {
        this.userExistsRepository = userExistsRepository;
        this.dateCheck = dateCheck;
        this.phoneNumberCheck = phoneNumberCheck;
        this.nameCheck = nameCheck;
        this.passwordCheck = passwordCheck;
        this.localDateFormatter = localDateFormatter;
    }

    public LoadValidationResult isValidDescription(UserUpdateDescriptionViewDto obj, UpdatedUserFields updatedUserFields) {
        LoadValidationResult result = new LoadValidationResult();

        if (updatedUserFields.isNewNickName()) {
            if (obj.getNickname().trim().isEmpty()) {
                result.add(LoadError.of("nickname", TypeLoadError.EMPTY));
            } else if (userExistsRepository.existsByNickname(obj.getNickname())) {
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
            } else if (userExistsRepository.existsByPhoneNumber(obj.getPhoneNumber())) {
                result.add(LoadError.of("phoneNumber", TypeLoadError.NON_UNIQUE));
            }
        }

        if (updatedUserFields.isNewEmail()) {
            if (obj.getEmail().trim().isEmpty()) {
                result.add(LoadError.of("email", TypeLoadError.EMPTY));
            } else if (userExistsRepository.existsByEmail(obj.getEmail())) {
                result.add(LoadError.of("email", TypeLoadError.NON_UNIQUE));
            }
        }
        if (updatedUserFields.isNewBirthDate()) {
            if (obj.getBirthDate().trim().isEmpty()) {
                result.add(LoadError.of("birthDate", TypeLoadError.EMPTY));
            } else if (!dateCheck.isCorrect(obj.getBirthDate())) {
                result.add(LoadError.of("birthDate", TypeLoadError.INCORRECT));
            } else {
                Period period = Period.between(localDateFormatter.format(obj.getBirthDate()), LocalDate.now());
                if (period.getYears() < 18) {
                    result.add(LoadError.of("birthDate", TypeLoadError.YOUNG_USER));
                }
            }
        }

        return result;
    }

    public boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty() && !passwordCheck.isIncorrect(password);
    }
}
