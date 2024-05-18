package ru.arsentiev.servicelayer.validator;

import ru.arsentiev.dto.user.view.UserValidatorViewDto;
import ru.arsentiev.processing.check.*;
import ru.arsentiev.processing.dateformatter.LocalDateFormatter;
import ru.arsentiev.processing.query.entity.RegistrationUserFields;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;
import ru.arsentiev.servicelayer.validator.entity.load.LoadError;
import ru.arsentiev.servicelayer.validator.entity.load.LoadValidationResult;
import ru.arsentiev.servicelayer.validator.entity.load.TypeLoadError;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserValidator {
    private final EmptyCheck emptyCheck;
    private final DateCheck dateCheck;
    private final PasswordCheck passwordCheck;
    private final PhoneNumberCheck phoneNumberCheck;
    private final NameCheck nameCheck;
    private final LocalDateFormatter localDateFormatter;
    private List<String> listNicknames;
    private List<String> listEmails;
    private List<String> listPhoneNumber;
    private final RegistrationUserFields registrationUserFields;

    public UserValidator(DateCheck dateCheck, PasswordCheck passwordCheck, PhoneNumberCheck phoneNumberCheck, EmptyCheck emptyCheck,
                         NameCheck nameCheck, LocalDateFormatter localDateFormatter) {
        this.emptyCheck = emptyCheck;
        this.dateCheck = dateCheck;
        this.passwordCheck = passwordCheck;
        this.phoneNumberCheck = phoneNumberCheck;
        this.nameCheck = nameCheck;
        this.localDateFormatter = localDateFormatter;
        this.listNicknames = Collections.emptyList();
        this.listEmails = Collections.emptyList();
        this.listPhoneNumber = Collections.emptyList();
        registrationUserFields = RegistrationUserFields.builder().build();
    }

    private void setLists(List<String> listNicknames, List<String> listEmails, List<String> listPhoneNumber) {
        this.listNicknames = listNicknames;
        this.listEmails = listEmails;
        this.listPhoneNumber = listPhoneNumber;
    }

    public void setToUpdate(UpdatedUserFields updatedUserFields, List<String> listNicknames, List<String> listEmails, List<String> listPhoneNumber) {
        this.registrationUserFields.setUpdatedUserFields(updatedUserFields);
        this.registrationUserFields.setNewPassword(false);
        setLists(listNicknames, listEmails, listPhoneNumber);
    }

    public void setToRegistration(List<String> listNicknames, List<String> listEmails, List<String> listPhoneNumber) {
        this.registrationUserFields.setUpdatedUserFields(UpdatedUserFields.builder()
                .isNewBirthDate(true)
                .isNewLastName(true)
                .isNewEmail(true)
                .isNewPatronymic(true)
                .isNewFirstName(true)
                .isNewNickName(true)
                .isNewPhoneNumber(true)
                .build());
        this.registrationUserFields.setNewPassword(true);
        setLists(listNicknames, listEmails, listPhoneNumber);
    }

    public LoadValidationResult isValid(UserValidatorViewDto obj) {
        LoadValidationResult result = new LoadValidationResult();

        Optional<LoadError> checkNickname = checkField(registrationUserFields.getUpdatedUserFields().isNewNickName(), obj.getNickname(),
                "nickname", emptyCheck, listNicknames);
        checkNickname.ifPresent(result::add);

        Optional<LoadError> checkFirstName = checkField(registrationUserFields.getUpdatedUserFields().isNewFirstName(), obj.getFirstName(),
                "firstName", nameCheck, Collections.emptyList());
        checkFirstName.ifPresent(result::add);

        Optional<LoadError> checkLastName = checkField(registrationUserFields.getUpdatedUserFields().isNewLastName(), obj.getLastName(),
                "lastName", nameCheck, Collections.emptyList());
        checkLastName.ifPresent(result::add);

        Optional<LoadError> checkPassword = checkField(registrationUserFields.isNewPassword(), obj.getPassword(),
                "password", passwordCheck, Collections.emptyList());
        checkPassword.ifPresent(result::add);

        Optional<LoadError> checkPhoneNumber = checkField(registrationUserFields.getUpdatedUserFields().isNewPhoneNumber(), obj.getPhoneNumber(),
                "phoneNumber", phoneNumberCheck, listPhoneNumber);
        checkPhoneNumber.ifPresent(result::add);

        Optional<LoadError> checkEmail = checkField(registrationUserFields.getUpdatedUserFields().isNewEmail(), obj.getEmail(),
                "email", emptyCheck, listEmails);
        checkEmail.ifPresent(result::add);

        Optional<LoadError> checkBirthDate = checkField(registrationUserFields.getUpdatedUserFields().isNewBirthDate(), obj.getBirthDate(),
                "birthDate", dateCheck, Collections.emptyList());
        if (checkBirthDate.isPresent()) {
            result.add(checkBirthDate.get());
        } else {
            checkYangUser(obj.getBirthDate(), registrationUserFields.getUpdatedUserFields().isNewBirthDate()).ifPresent(result::add);
        }

        return result;
    }

    private Optional<LoadError> checkField(boolean isNew, String field, String name, Check check, List<String> list) {
        if (isNew) {
            if (field == null || field.trim().isEmpty()) {
                return Optional.of(LoadError.of(name, TypeLoadError.EMPTY));
            } else if (check.isInvalid(field)) {
                return Optional.of((LoadError.of(name, TypeLoadError.INCORRECT)));
            } else if (list.contains(field)) {
                return Optional.of(LoadError.of(name, TypeLoadError.NON_UNIQUE));
            }
        }
        return Optional.empty();
    }

    private Optional<LoadError> checkYangUser(String str, boolean isNew) {
        if (isNew) {
            Period period = Period.between(localDateFormatter.format(str), LocalDate.now());
            if (period.getYears() < 18) {
                return Optional.of(LoadError.of("birthDate", TypeLoadError.YOUNG_USER));
            }
        }
        return Optional.empty();
    }

    public boolean isValidPassword(String password) {
        return checkField(true, password,
                "password", passwordCheck, Collections.emptyList()).isEmpty();
    }
}
