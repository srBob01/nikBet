package ru.arsentiev.servicelayer.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.arsentiev.dto.user.view.UserRegistrationViewDto;
import ru.arsentiev.processing.check.DateCheck;
import ru.arsentiev.processing.check.NameCheck;
import ru.arsentiev.processing.check.PasswordCheck;
import ru.arsentiev.processing.check.PhoneNumberCheck;
import ru.arsentiev.processing.dateformatter.LocalDateFormatter;
import ru.arsentiev.repository.UserExistsRepository;
import ru.arsentiev.servicelayer.validator.entity.load.LoadValidationResult;
import ru.arsentiev.servicelayer.validator.entity.load.TypeLoadError;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationUserValidatorTest {
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
    private RegistrationUserValidator registrationUserValidator;

    @Test
    void isValidTest_EmptyFields() {
        UserRegistrationViewDto user = UserRegistrationViewDto.builder()
                .nickname("")
                .firstName("")
                .lastName("")
                .password("")
                .phoneNumber("")
                .email("")
                .birthDate("")
                .build();

        LoadValidationResult result = registrationUserValidator.isValid(user);

        assertThat(result).isNotNull();
        assertThat(result.getLoadErrors().size()).isEqualTo(7);
        assertThat(result.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.EMPTY)).isTrue();
    }

    @Test
    void isValidTest_Incorrect() {
        UserRegistrationViewDto user = UserRegistrationViewDto.builder()
                .nickname("validNick")
                .firstName("Invalid1")
                .lastName("Invalid2")
                .password("123")
                .phoneNumber("123456")
                .email("validEmail@mail.com")
                .birthDate("1930-12")
                .build();

        when(nameCheck.isIncorrect(user.getFirstName())).thenReturn(true);
        when(nameCheck.isIncorrect(user.getLastName())).thenReturn(true);
        when(passwordCheck.isIncorrect(user.getPassword())).thenReturn(true);
        when(phoneNumberCheck.isIncorrect(user.getPhoneNumber())).thenReturn(true);
        when(dateCheck.isCorrect(user.getBirthDate())).thenReturn(false);


        LoadValidationResult result = registrationUserValidator.isValid(user);

        assertThat(result).isNotNull();
        assertThat(result.getLoadErrors().size()).isEqualTo(5);
        assertThat(result.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.INCORRECT)).isTrue();
    }

    @Test
    void isValidTest_NonUnique() {
        UserRegistrationViewDto user = UserRegistrationViewDto.builder()
                .nickname("validNick")
                .firstName("Invalid1")
                .lastName("Invalid2")
                .password("123")
                .phoneNumber("123456")
                .email("validEmail@mail.com")
                .birthDate("2000-12-12")
                .build();

        when(userExistsRepository.existsByNickname(user.getNickname())).thenReturn(true);
        when(userExistsRepository.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(true);
        when(userExistsRepository.existsByEmail(user.getEmail())).thenReturn(true);
        when(dateCheck.isCorrect(user.getBirthDate())).thenReturn(true);
        when(localDateFormatter.format(user.getBirthDate())).thenReturn(LocalDate.of(2000, 10, 10));

        LoadValidationResult result = registrationUserValidator.isValid(user);

        assertThat(result).isNotNull();
        assertThat(result.getLoadErrors().size()).isEqualTo(3);
        assertThat(result.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.NON_UNIQUE)).isTrue();
    }

    @Test
    void isValidTest_YangUser() {
        UserRegistrationViewDto user = UserRegistrationViewDto.builder()
                .nickname("validNick")
                .firstName("Invalid1")
                .lastName("Invalid2")
                .password("123")
                .phoneNumber("123456")
                .email("validEmail@mail.com")
                .birthDate("2020-12-12")
                .build();

        when(dateCheck.isCorrect(user.getBirthDate())).thenReturn(true);
        when(localDateFormatter.format(user.getBirthDate())).thenReturn(LocalDate.of(2020, 12, 12));

        LoadValidationResult result = registrationUserValidator.isValid(user);

        assertThat(result).isNotNull();
        assertThat(result.getLoadErrors().size()).isEqualTo(1);
        assertThat(result.getLoadErrors().stream().allMatch(error -> error.getType() == TypeLoadError.YOUNG_USER)).isTrue();
    }

    @Test
    void isValidTest_Valid() {
        UserRegistrationViewDto user = UserRegistrationViewDto.builder()
                .nickname("validNick")
                .firstName("Invalid1")
                .lastName("Invalid2")
                .password("123")
                .phoneNumber("123456")
                .email("validEmail@mail.com")
                .birthDate("2020-12-12")
                .build();

        when(dateCheck.isCorrect(anyString())).thenReturn(true);
        when(localDateFormatter.format(anyString())).thenReturn(LocalDate.now().minusYears(20));

        LoadValidationResult result = registrationUserValidator.isValid(user);

        assertThat(result.getLoadErrors()).isEmpty();
    }
}