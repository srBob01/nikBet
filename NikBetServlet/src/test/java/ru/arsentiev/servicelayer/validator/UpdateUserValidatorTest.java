package ru.arsentiev.servicelayer.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.arsentiev.dto.user.view.UserUpdateDescriptionViewDto;
import ru.arsentiev.processing.check.DateCheck;
import ru.arsentiev.processing.check.NameCheck;
import ru.arsentiev.processing.check.PasswordCheck;
import ru.arsentiev.processing.check.PhoneNumberCheck;
import ru.arsentiev.processing.dateformatter.LocalDateFormatter;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;
import ru.arsentiev.repository.UserExistsRepository;
import ru.arsentiev.servicelayer.validator.entity.load.LoadValidationResult;
import ru.arsentiev.servicelayer.validator.entity.load.TypeLoadError;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserValidatorTest {
    @Mock
    private UserExistsRepository userExistsRepository;
    @Mock
    private DateCheck dateCheck;
    @Mock
    private PasswordCheck passwordCheck;
    @Mock
    private PhoneNumberCheck phoneNumberCheck;
    @Mock
    private NameCheck nameCheck;
    @Mock
    private LocalDateFormatter localDateFormatter;
    @InjectMocks
    private UpdateUserValidator validator;

    @Test
    public void validateNoUpdateRequired() {
        UserUpdateDescriptionViewDto user = UserUpdateDescriptionViewDto.builder()
                .nickname("JohnDoe")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .email("johndoe@example.com")
                .birthDate("1990-01-01")
                .build();

        UpdatedUserFields updatedFields = UpdatedUserFields.builder().build();

        LoadValidationResult result = validator.isValidDescription(user, updatedFields);

        assertTrue(result.isEmpty());
    }

    @Test
    public void validateEmptyFieldsWhenUpdateRequired() {
        UserUpdateDescriptionViewDto user = UserUpdateDescriptionViewDto.builder()
                .nickname("")
                .firstName("")
                .lastName("")
                .phoneNumber("")
                .email("")
                .birthDate("")
                .build();

        UpdatedUserFields updatedFields = UpdatedUserFields.builder()
                .isNewNickName(true)
                .isNewFirstName(true)
                .isNewLastName(true)
                .isNewPhoneNumber(true)
                .isNewEmail(true)
                .isNewBirthDate(true)
                .build();

        LoadValidationResult result = validator.isValidDescription(user, updatedFields);

        assertEquals(6, result.getLoadErrors().size());
        assertTrue(result.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.EMPTY));
    }

    @Test
    public void validateIncorrectData() {
        when(nameCheck.isIncorrect(anyString())).thenReturn(true);
        when(phoneNumberCheck.isIncorrect(anyString())).thenReturn(true);
        when(dateCheck.isCorrect(anyString())).thenReturn(false);

        UserUpdateDescriptionViewDto user = UserUpdateDescriptionViewDto.builder()
                .nickname("JohnDoe")
                .firstName("Incorrect!")
                .lastName("Incorrect@")
                .phoneNumber("incorrectPhone")
                .birthDate("incorrectDate")
                .build();
        UpdatedUserFields updatedFields = UpdatedUserFields.builder()
                .isNewFirstName(true)
                .isNewLastName(true)
                .isNewPhoneNumber(true)
                .isNewBirthDate(true)
                .build();

        LoadValidationResult result = validator.isValidDescription(user, updatedFields);

        assertEquals(4, result.getLoadErrors().size());
        assertTrue(result.getLoadErrors().stream().anyMatch(error -> error.getType() == TypeLoadError.INCORRECT));
    }

    @Test
    public void validateNonUniqueFields() {
        when(userExistsRepository.existsByNickname(anyString())).thenReturn(true);
        when(userExistsRepository.existsByPhoneNumber(anyString())).thenReturn(true);
        when(userExistsRepository.existsByEmail(anyString())).thenReturn(true);

        UserUpdateDescriptionViewDto user = UserUpdateDescriptionViewDto.builder()
                .nickname("ExistingNickname")
                .phoneNumber("ExistingPhone")
                .email("existing@example.com")
                .build();
        UpdatedUserFields updatedFields = UpdatedUserFields.builder()
                .isNewNickName(true)
                .isNewPhoneNumber(true)
                .isNewEmail(true)
                .build();

        LoadValidationResult result = validator.isValidDescription(user, updatedFields);

        assertEquals(3, result.getLoadErrors().size());
        assertTrue(result.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.NON_UNIQUE));
    }

    @Test
    public void validateCorrectData() {
        when(dateCheck.isCorrect("1990-01-01")).thenReturn(true);
        when(localDateFormatter.format("1990-01-01")).thenReturn(LocalDate.of(1990, 1, 1));

        UserUpdateDescriptionViewDto user = UserUpdateDescriptionViewDto.builder()
                .nickname("JohnDoe")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .email("johndoe@example.com")
                .birthDate("1990-01-01")
                .build();
        UpdatedUserFields updatedFields = UpdatedUserFields.builder()
                .isNewNickName(true)
                .isNewFirstName(true)
                .isNewLastName(true)
                .isNewPhoneNumber(true)
                .isNewEmail(true)
                .isNewBirthDate(true)
                .build();

        LoadValidationResult result = validator.isValidDescription(user, updatedFields);

        assertTrue(result.isEmpty());
    }

    @Test
    public void validateUnderageUser() {
        when(dateCheck.isCorrect("2008-01-01")).thenReturn(true);
        when(localDateFormatter.format("2008-01-01")).thenReturn(LocalDate.of(2008, 1, 1));

        UserUpdateDescriptionViewDto user = UserUpdateDescriptionViewDto.builder()
                .birthDate("2008-01-01")
                .build();
        UpdatedUserFields updatedFields = UpdatedUserFields.builder()
                .isNewBirthDate(true)
                .build();

        LoadValidationResult result = validator.isValidDescription(user, updatedFields);

        assertEquals(1, result.getLoadErrors().size());
        assertEquals(TypeLoadError.YOUNG_USER, result.getLoadErrors().get(0).getType());
    }

    @Test
    public void validateValidPassword() {
        when(passwordCheck.isIncorrect("ValidPassword123")).thenReturn(false);

        assertTrue(validator.isValidPassword("ValidPassword123"));
    }

    @Test
    public void validateInvalidPassword() {
        when(passwordCheck.isIncorrect("short")).thenReturn(true);

        assertFalse(validator.isValidPassword("short"));
    }

    @Test
    public void validateNullPassword() {
        assertFalse(validator.isValidPassword(null));
    }

    @Test
    public void validateEmptyPassword() {
        assertFalse(validator.isValidPassword(""));
    }

    @Test
    public void validateWhitespaceOnlyPassword() {
        assertFalse(validator.isValidPassword("   "));
    }

}