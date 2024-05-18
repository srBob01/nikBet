package ru.arsentiev.servicelayer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.arsentiev.dto.user.controller.*;
import ru.arsentiev.dto.user.view.UserRegistrationViewDto;
import ru.arsentiev.dto.user.view.UserValidatorViewDto;
import ru.arsentiev.entity.User;
import ru.arsentiev.entity.UserRole;
import ru.arsentiev.exception.ServiceException;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.processing.dateformatter.LocalDateFormatter;
import ru.arsentiev.processing.password.PasswordHashed;
import ru.arsentiev.repository.UserRepository;
import ru.arsentiev.servicelayer.validator.UserValidator;
import ru.arsentiev.servicelayer.validator.entity.load.LoadError;
import ru.arsentiev.servicelayer.validator.entity.load.LoadValidationResult;
import ru.arsentiev.servicelayer.validator.entity.load.TypeLoadError;
import ru.arsentiev.servicelayer.validator.entity.login.LoginError;
import ru.arsentiev.servicelayer.validator.entity.update.UpdatePasswordError;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private final User user;
    private final UserPasswordAndSaltControllerDto userPasswordAndSaltControllerDto;
    private final UserLogoPasControllerDto userLogoPasControllerDto;
    private final UserLoginControllerDto userLoginControllerDto;
    private final UserControllerDto userControllerDto;
    private final UserMoneyControllerDto userMoneyControllerDto;
    private final UserMapper userMapper;
    private PasswordHashed passwordHashed;
    private UserRepository userRepository;
    private UserValidator userValidator;
    private UserService userService;

    {
        LocalDateFormatter localDateFormatter = new LocalDateFormatter();
        userMapper = new UserMapper(localDateFormatter);

        user = User.builder()
                .nickname("user1").firstName("John")
                .lastName("Doe").password("password1")
                .salt("salt1").phoneNumber("+79969291133").email("user1@example.com")
                .birthDate(LocalDate.of(1990, 5, 15))
                .accountBalance(new BigDecimal("0")).role(UserRole.USER)
                .build();

        String newPassword = "newPassword";

        userPasswordAndSaltControllerDto = UserPasswordAndSaltControllerDto.builder()
                .password(user.getPassword())
                .salt(user.getSalt())
                .build();

        userLogoPasControllerDto = UserLogoPasControllerDto.builder()
                .email(user.getEmail())
                .oldPassword(user.getPassword())
                .newPassword(newPassword)
                .build();

        userLoginControllerDto = UserLoginControllerDto.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        userControllerDto = UserControllerDto.builder()
                .nickname("user1").firstName("John")
                .lastName("Doe").phoneNumber("+79969291133").email("user1@example.com")
                .birthDate(LocalDate.of(1990, 5, 15)).role(UserRole.USER)
                .build();

        userMoneyControllerDto = UserMoneyControllerDto.builder()
                .idUser(user.getIdUser())
                .summa(BigDecimal.valueOf(10))
                .build();
    }

    @BeforeEach
    void createUserService() {
        passwordHashed = mock(PasswordHashed.class);
        userRepository = mock(UserRepository.class);
        userValidator = mock(UserValidator.class);
        userService = new UserService(userMapper, passwordHashed, userRepository, userValidator);
    }


    @Test
    void insertUserTest_Valid() {
        UserRegistrationViewDto userRegistrationViewDto = UserRegistrationViewDto.builder()
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate().toString())
                .patronymic(user.getPatronymic())
                .password(user.getPassword())
                .email(user.getEmail()).build();

        UserValidatorViewDto userValidatorViewDto = UserValidatorViewDto.builder()
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate().toString())
                .patronymic(user.getPatronymic())
                .password(user.getPassword())
                .email(user.getEmail()).build();

        when(userRepository.selectAll()).thenReturn(Collections.emptyList());
        when(userValidator.isValid(userValidatorViewDto)).thenReturn(new LoadValidationResult());
        when(passwordHashed.generateSalt()).thenReturn(user.getSalt());

        List<LoadError> result = userService.insertUser(userRegistrationViewDto);
        assertThat(result).isEmpty();
    }

    @Test
    void insertUserTest_Invalid() {
        UserRegistrationViewDto userRegistrationViewDto = UserRegistrationViewDto.builder()
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate().toString())
                .patronymic(user.getPatronymic())
                .password(user.getPassword())
                .email(user.getEmail()).build();

        UserValidatorViewDto userValidatorViewDto = UserValidatorViewDto.builder()
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate().toString())
                .patronymic(user.getPatronymic())
                .password(user.getPassword())
                .email(user.getEmail()).build();

        when(userRepository.selectAll()).thenReturn(Collections.singletonList(user));
        LoadValidationResult loadValidationResult = new LoadValidationResult();
        loadValidationResult.add(LoadError.of("k", TypeLoadError.INCORRECT));
        when(userValidator.isValid(userValidatorViewDto)).thenReturn(loadValidationResult);
        when(passwordHashed.generateSalt()).thenReturn(user.getSalt());

        List<LoadError> result = userService.insertUser(userRegistrationViewDto);
        assertThat(result).isNotEmpty();
    }

    @Test
    void updatePassword_UserDoesNotExists() {
        when(userRepository.selectPasswordByLogin(anyString())).thenReturn(null);

        assertThatThrownBy(() -> userService.updatePasswordUser(userLogoPasControllerDto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void updatePassword_PasswordsDoNotMatch() {

        when(userRepository.selectPasswordByLogin(user.getEmail())).thenReturn(user);
        when(passwordHashed.hashPassword(userLogoPasControllerDto.oldPassword(), userPasswordAndSaltControllerDto.salt())).thenReturn("differentHashedPassword");

        Optional<UpdatePasswordError> result = userService.updatePasswordUser(userLogoPasControllerDto);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(UpdatePasswordError.PASSWORDS_DONT_MATCH);
    }

    @Test
    void updatePassword_NewPasswordsInvalid() {

        when(userRepository.selectPasswordByLogin(user.getEmail())).thenReturn(user);
        when(passwordHashed.hashPassword(userLogoPasControllerDto.oldPassword(), userPasswordAndSaltControllerDto.salt())).thenReturn(user.getPassword());
        when(userValidator.isValidPassword(userLogoPasControllerDto.newPassword())).thenReturn(false);

        Optional<UpdatePasswordError> result = userService.updatePasswordUser(userLogoPasControllerDto);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(UpdatePasswordError.INCORRECT_NEW_PASSWORD);
    }

    @Test
    void updatePassword_InvalidUpdate() {

        when(userRepository.selectPasswordByLogin(user.getEmail())).thenReturn(user);
        when(passwordHashed.hashPassword(userLogoPasControllerDto.oldPassword(), userPasswordAndSaltControllerDto.salt())).thenReturn(user.getPassword());
        when(userValidator.isValidPassword(userLogoPasControllerDto.newPassword())).thenReturn(true);
        when(passwordHashed.generateSalt()).thenReturn(user.getSalt());
        when(userRepository.updatePasswordByLogin(user)).thenReturn(false);

        assertThatThrownBy(() -> userService.updatePasswordUser(userLogoPasControllerDto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void updatePassword_ValidUpdate() {

        when(userRepository.selectPasswordByLogin(user.getEmail())).thenReturn(user);
        when(passwordHashed.hashPassword(userLogoPasControllerDto.oldPassword(), userPasswordAndSaltControllerDto.salt())).thenReturn(user.getPassword());
        when(userValidator.isValidPassword(userLogoPasControllerDto.newPassword())).thenReturn(true);
        when(passwordHashed.generateSalt()).thenReturn(user.getSalt());
        when(userRepository.updatePasswordByLogin(any(User.class))).thenReturn(true);

        Optional<UpdatePasswordError> result = userService.updatePasswordUser(userLogoPasControllerDto);

        assertThat(result).isEmpty();
    }

    @Test
    void checkLoginUser_UserNotFound() {
        when(userRepository.selectPasswordByLogin(userLoginControllerDto.email())).thenReturn(null);

        Optional<LoginError> error = userService.checkLoginUser(userLoginControllerDto);

        assertThat(error).isPresent();
        assertThat(error.get()).isEqualTo(LoginError.USER_NOT_FOUND);
    }

    @Test
    void checkLoginUser_IncorrectPassword() {
        when(userRepository.selectPasswordByLogin(userLoginControllerDto.email())).thenReturn(user);
        when(passwordHashed.hashPassword(userLoginControllerDto.password(), userPasswordAndSaltControllerDto.salt())).thenReturn("otherPassword");

        Optional<LoginError> error = userService.checkLoginUser(userLoginControllerDto);

        assertThat(error).isPresent();
        assertThat(error.get()).isEqualTo(LoginError.INCORRECT_PASSWORD);
    }

    @Test
    void checkLoginUser_CorrectPassword() {
        when(userRepository.selectPasswordByLogin(userLoginControllerDto.email())).thenReturn(user);
        when(passwordHashed.hashPassword(userLoginControllerDto.password(), userPasswordAndSaltControllerDto.salt())).thenReturn(user.getPassword());

        Optional<LoginError> error = userService.checkLoginUser(userLoginControllerDto);

        assertThat(error).isEmpty();
    }

    @Test
    void selectUser_Valid() {
        when(userRepository.selectByLogin(userLoginControllerDto.email())).thenReturn(user);

        UserControllerDto dto = userService.selectUser(userLoginControllerDto);

        assertThat(dto).isEqualTo(userControllerDto);
    }

    @Test
    void selectUser_Empty() {
        when(userRepository.selectByLogin(userLoginControllerDto.email())).thenReturn(null);

        assertThatThrownBy(() -> userService.selectUser(userLoginControllerDto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void depositMoney_Valid() {
        when(userRepository.depositMoneyById(userMoneyControllerDto.idUser(), userMoneyControllerDto.summa())).thenReturn(true);

        assertThat(userService.depositMoney(userMoneyControllerDto)).isTrue();
    }

    @Test
    void depositMoney_Invalid() {
        when(userRepository.depositMoneyById(userMoneyControllerDto.idUser(), userMoneyControllerDto.summa())).thenReturn(false);

        assertThat(userService.depositMoney(userMoneyControllerDto)).isFalse();
    }

    @Test
    void getAccountBalance_Invalid() {
        long idUser = user.getIdUser();
        when(userRepository.selectBalanceById(idUser)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getAccountBalance(idUser)).isInstanceOf(ServiceException.class);
    }

    @Test
    void getAccountBalance_Valid() {
        long idUser = user.getIdUser();
        BigDecimal balance = BigDecimal.valueOf(10);
        when(userRepository.selectBalanceById(idUser)).thenReturn(Optional.of(balance));

        BigDecimal valueFromService = userService.getAccountBalance(idUser);

        assertThat(valueFromService).isEqualTo(balance);
    }

    @Test
    void withdrawMoney_Invalid() {
        when(userRepository.selectBalanceById(userMoneyControllerDto.idUser())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.withdrawMoney(userMoneyControllerDto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void withdrawMoney_NotEnoughMoney() {
        BigDecimal smallSumma = BigDecimal.valueOf(0);
        when(userRepository.selectBalanceById(userMoneyControllerDto.idUser())).thenReturn(Optional.of(smallSumma));

        assertThat(userService.withdrawMoney(userMoneyControllerDto)).isFalse();
    }

    @Test
    void withdrawMoney_EnoughMoney() {
        BigDecimal bigSumma = BigDecimal.valueOf(10000);
        when(userRepository.selectBalanceById(userMoneyControllerDto.idUser())).thenReturn(Optional.of(bigSumma));
        when(userRepository.withdrawMoneyById(userMoneyControllerDto.idUser(), userMoneyControllerDto.summa())).thenReturn(true);

        assertThat(userService.withdrawMoney(userMoneyControllerDto)).isTrue();
    }

    @Test
    void deleteUser_Valid() {
        long idUser = user.getIdUser();
        when(userRepository.delete(idUser)).thenReturn(true);

        assertThat(userService.deleteUser(idUser)).isTrue();
    }

    @Test
    void deleteUser_Invalid() {
        long idUser = user.getIdUser();
        when(userRepository.delete(idUser)).thenReturn(false);

        assertThat(userService.deleteUser(idUser)).isFalse();
    }

    @Test
    void selectUserByNickname_Valid() {
        String nickname = user.getNickname();
        UserForAdminControllerDto userForAdminControllerDto = UserForAdminControllerDto.builder()
                .idUser(user.getIdUser())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .patronymic(user.getPatronymic())
                .accountBalance(user.getAccountBalance())
                .email(user.getEmail())
                .build();

        when(userRepository.selectByNickname(nickname)).thenReturn(Optional.of(user));

        Optional<UserForAdminControllerDto> result = userService.selectUserByNickname(nickname);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userForAdminControllerDto);
    }

    @Test
    void selectUserByNickname_NotFound() {
        String nickname = "nonExistentNickname";

        when(userRepository.selectByNickname(nickname)).thenReturn(Optional.empty());

        Optional<UserForAdminControllerDto> result = userService.selectUserByNickname(nickname);

        assertThat(result).isEmpty();
    }

    @Test
    void selectAllUser_Valid() {
        List<User> users = Arrays.asList(
                User.builder().idUser(1)
                        .nickname(user.getNickname())
                        .phoneNumber(user.getPhoneNumber())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .birthDate(user.getBirthDate())
                        .patronymic(user.getPatronymic())
                        .accountBalance(user.getAccountBalance())
                        .email(user.getEmail()).build(),
                User.builder().idUser(2)
                        .nickname(user.getNickname())
                        .phoneNumber(user.getPhoneNumber())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .birthDate(user.getBirthDate())
                        .patronymic(user.getPatronymic())
                        .accountBalance(user.getAccountBalance())
                        .email(user.getEmail()).build()
        );
        List<UserForAdminControllerDto> listExpectedDto = users.stream()
                .map(user -> UserForAdminControllerDto.builder().idUser(user.getIdUser())
                        .nickname(user.getNickname())
                        .phoneNumber(user.getPhoneNumber())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .birthDate(user.getBirthDate())
                        .patronymic(user.getPatronymic())
                        .accountBalance(user.getAccountBalance())
                        .email(user.getEmail()).build())
                .collect(Collectors.toList());

        when(userRepository.selectAllOnlyUser()).thenReturn(users);

        List<UserForAdminControllerDto> result = userService.selectAllUser();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(listExpectedDto);
    }

    @Test
    void selectAllUser_Empty() {
        when(userRepository.selectAll()).thenReturn(Collections.emptyList());

        List<UserForAdminControllerDto> result = userService.selectAllUser();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}