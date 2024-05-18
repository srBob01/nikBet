package ru.arsentiev.servicelayer.validator;

import org.junit.jupiter.api.Test;
import ru.arsentiev.dto.user.view.UserValidatorViewDto;
import ru.arsentiev.processing.check.*;
import ru.arsentiev.processing.dateformatter.LocalDateFormatter;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;
import ru.arsentiev.servicelayer.validator.entity.load.LoadError;
import ru.arsentiev.servicelayer.validator.entity.load.LoadValidationResult;
import ru.arsentiev.servicelayer.validator.entity.load.TypeLoadError;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserValidatorTest {
    private final UserValidator userValidator;
    private final UpdatedUserFields updatedAllFields;
    private final UpdatedUserFields updatedNothingFields;

    {
        updatedAllFields = UpdatedUserFields.builder()
                .isNewNickName(true)
                .isNewFirstName(true)
                .isNewLastName(true)
                .isNewPhoneNumber(true)
                .isNewEmail(true)
                .isNewBirthDate(true)
                .build();

        updatedNothingFields = UpdatedUserFields.builder().build();

        EmptyCheck emptyCheck = new EmptyCheck();
        LocalDateFormatter localDateFormatter = new LocalDateFormatter();
        DateCheck dateCheck = new DateCheck(localDateFormatter);
        PasswordCheck passwordCheck = new PasswordCheck();
        PhoneNumberCheck phoneNumberCheck = new PhoneNumberCheck();
        NameCheck nameCheck = new NameCheck();
        userValidator = new UserValidator(dateCheck, passwordCheck, phoneNumberCheck, emptyCheck, nameCheck, localDateFormatter);
    }

    @Test
    void isValidTest_EmptyFields() {
        UserValidatorViewDto user = UserValidatorViewDto.builder()
                .nickname("")
                .firstName("")
                .lastName("")
                .password("")
                .phoneNumber("")
                .email("")
                .birthDate("")
                .build();

        userValidator.setToRegistration(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        LoadValidationResult resultRegistration = userValidator.isValid(user);

        assertThat(resultRegistration).isNotNull();
        assertThat(resultRegistration.getLoadErrors().size()).isEqualTo(7);
        assertThat(resultRegistration.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.EMPTY)).isTrue();

        userValidator.setToUpdate(updatedAllFields, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        LoadValidationResult resultUpdate = userValidator.isValid(user);

        assertThat(resultUpdate).isNotNull();
        assertThat(resultUpdate.getLoadErrors().size()).isEqualTo(6);
        assertThat(resultUpdate.getLoadErrors()).doesNotContain(LoadError.of("password", TypeLoadError.EMPTY));
        assertThat(resultUpdate.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.EMPTY)).isTrue();
    }

    @Test
    public void isValidTest_NoUpdateRequired() {
        UserValidatorViewDto userUpdate = UserValidatorViewDto.builder()
                .nickname("")
                .firstName("")
                .lastName("")
                .password(null)
                .phoneNumber("")
                .email("")
                .birthDate("")
                .build();

        userValidator.setToUpdate(updatedNothingFields, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        LoadValidationResult result = userValidator.isValid(userUpdate);

        assertTrue(result.isEmpty());
    }

    @Test
    public void isValidTest_IncorrectData() {
        UserValidatorViewDto user = UserValidatorViewDto.builder()
                .nickname("nickname")
                .firstName("542")
                .lastName("543")
                .password("11")
                .phoneNumber("+7")
                .email("necit717@gmail.com")
                .birthDate("2002--")
                .build();


        userValidator.setToRegistration(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        LoadValidationResult resultRegistration = userValidator.isValid(user);

        assertThat(resultRegistration).isNotNull();
        assertThat(resultRegistration.getLoadErrors().size()).isEqualTo(5);
        assertThat(resultRegistration.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.INCORRECT)).isTrue();

        userValidator.setToUpdate(updatedAllFields, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        LoadValidationResult resultUpdate = userValidator.isValid(user);

        assertThat(resultUpdate).isNotNull();
        assertThat(resultUpdate.getLoadErrors().size()).isEqualTo(4);
        assertThat(resultUpdate.getLoadErrors()).doesNotContain(LoadError.of("password", TypeLoadError.INCORRECT));
        assertThat(resultUpdate.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.INCORRECT)).isTrue();
    }

    @Test
    public void isValidTest_NonUnique() {
        UserValidatorViewDto user = UserValidatorViewDto.builder()
                .nickname("nickname")
                .firstName("Nikita")
                .lastName("Nikita")
                .password("fewiod3q02fmdJ")
                .phoneNumber("+79969291133")
                .email("necit717@gmail.com")
                .birthDate("2000-01-01")
                .build();


        userValidator.setToRegistration(Collections.singletonList(user.getNickname()), Collections.singletonList(user.getEmail()),
                Collections.singletonList(user.getPhoneNumber()));
        LoadValidationResult resultRegistration = userValidator.isValid(user);

        assertThat(resultRegistration).isNotNull();
        assertThat(resultRegistration.getLoadErrors().size()).isEqualTo(3);
        assertThat(resultRegistration.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.NON_UNIQUE)).isTrue();

        userValidator.setToUpdate(updatedAllFields, Collections.singletonList(user.getNickname()), Collections.singletonList(user.getEmail()),
                Collections.singletonList(user.getPhoneNumber()));
        LoadValidationResult resultUpdate = userValidator.isValid(user);

        assertThat(resultUpdate).isNotNull();
        assertThat(resultUpdate.getLoadErrors().size()).isEqualTo(3);
        assertThat(resultUpdate.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.NON_UNIQUE)).isTrue();
    }

    @Test
    public void isValidTest_YangUser() {
        UserValidatorViewDto user = UserValidatorViewDto.builder()
                .nickname("nickname")
                .firstName("Nikita")
                .lastName("Nikita")
                .password("fewiod3q02fmdJ")
                .phoneNumber("+79969291133")
                .email("necit717@gmail.com")
                .birthDate("2020-01-01")
                .build();


        userValidator.setToRegistration(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        LoadValidationResult resultRegistration = userValidator.isValid(user);

        assertThat(resultRegistration).isNotNull();
        assertThat(resultRegistration.getLoadErrors().size()).isEqualTo(1);
        assertThat(resultRegistration.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.YOUNG_USER)).isTrue();

        userValidator.setToUpdate(updatedAllFields, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        LoadValidationResult resultUpdate = userValidator.isValid(user);

        assertThat(resultUpdate).isNotNull();
        assertThat(resultUpdate.getLoadErrors().size()).isEqualTo(1);
        assertThat(resultUpdate.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.YOUNG_USER)).isTrue();
    }

    @Test
    public void isValidTest_ValidUser() {
        UserValidatorViewDto user = UserValidatorViewDto.builder()
                .nickname("nickname")
                .firstName("Nikita")
                .lastName("Nikita")
                .password("fewiod3q02fmdJ")
                .phoneNumber("+79969291133")
                .email("necit717@gmail.com")
                .birthDate("2000-01-01")
                .build();


        userValidator.setToRegistration(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        LoadValidationResult resultRegistration = userValidator.isValid(user);

        assertThat(resultRegistration).isNotNull();
        assertThat(resultRegistration.getLoadErrors()).isEmpty();

        userValidator.setToUpdate(updatedAllFields, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        LoadValidationResult resultUpdate = userValidator.isValid(user);

        assertThat(resultUpdate).isNotNull();
        assertThat(resultUpdate.getLoadErrors()).isEmpty();
    }

    @Test
    public void validateValidPassword() {
        assertTrue(userValidator.isValidPassword("ValidPassword123"));
    }

    @Test
    public void validateInvalidPassword() {
        assertFalse(userValidator.isValidPassword("short"));
    }

    @Test
    public void validateNullPassword() {
        assertFalse(userValidator.isValidPassword(null));
    }

    @Test
    public void validateEmptyPassword() {
        assertFalse(userValidator.isValidPassword(""));
    }

    @Test
    public void validateWhitespaceOnlyPassword() {
        assertFalse(userValidator.isValidPassword("   "));
    }
}