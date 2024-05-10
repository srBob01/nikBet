package ru.arsentiev.servicelayer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.arsentiev.dto.user.controller.*;
import ru.arsentiev.entity.User;
import ru.arsentiev.entity.UserRole;
import ru.arsentiev.exception.ServiceException;
import ru.arsentiev.mapper.UserMapper;
import ru.arsentiev.processing.password.PasswordHashed;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;
import ru.arsentiev.repository.UserRepository;
import ru.arsentiev.servicelayer.service.UserService;
import ru.arsentiev.servicelayer.validator.UpdateUserValidator;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserMapper userMapper;
    @Mock
    PasswordHashed passwordHashed;
    @Mock
    UserRepository userRepository;
    @Mock
    UpdateUserValidator updateUserValidator;
    @InjectMocks
    UserService userService;

    private static User user;
    private static UserPasswordAndSaltControllerDto userPasswordAndSaltControllerDto;
    private static UserLogoPasControllerDto userLogoPasControllerDto;
    private static UserLoginControllerDto userLoginControllerDto;
    private static UserControllerDto userControllerDto;
    private static UserMoneyControllerDto userMoneyControllerDto;

    @BeforeAll
    public static void fillUser() {
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

    @Test
    void insertUserTest() {
        UserRegistrationControllerDto dto = UserRegistrationControllerDto.builder().build();

        when(passwordHashed.generateSalt()).thenReturn(user.getSalt());
        when(userMapper.map(eq(dto), eq(user.getSalt()), any())).thenReturn(user);

        userService.insertUser(dto);

        verify(passwordHashed, times(1)).generateSalt();
        verify(userMapper, times(1)).map(eq(dto), eq(user.getSalt()), any());
    }

    @Test
    void updateDescription_ValidUserTest() {
        var userUpdateDescriptionControllerDto = UserUpdateDescriptionControllerDto.builder().build();
        var updatedUserFields = UpdatedUserFields.builder().build();
        var userConstFieldsControllerDto = UserConstFieldsControllerDto.builder().build();
        var userControllerDto = UserControllerDto.builder()
                .nickname(user.getNickname()).firstName(user.getFirstName())
                .lastName(user.getLastName()).phoneNumber(user.getPhoneNumber()).email(user.getEmail())
                .birthDate(user.getBirthDate()).role(user.getRole())
                .build();

        when(userMapper.map(userUpdateDescriptionControllerDto)).thenReturn(user);
        when(userRepository.updateDescriptionWithDynamicCreation(user, updatedUserFields)).thenReturn(true);
        when(userMapper.map(userConstFieldsControllerDto, userUpdateDescriptionControllerDto)).thenReturn(userControllerDto);

        UserControllerDto result = userService.updateDescriptionUser(userUpdateDescriptionControllerDto, updatedUserFields, userConstFieldsControllerDto);

        assertNotNull(result);
        assertThat(userControllerDto).isEqualTo(result);
    }

    @Test
    void updateDescription_InvalidUserTest() {
        var userUpdateDescriptionControllerDto = UserUpdateDescriptionControllerDto.builder().build();
        var updatedUserFields = UpdatedUserFields.builder().build();
        var userConstFieldsControllerDto = UserConstFieldsControllerDto.builder().build();

        when(userMapper.map(userUpdateDescriptionControllerDto)).thenReturn(user);
        when(userRepository.updateDescriptionWithDynamicCreation(user, updatedUserFields)).thenReturn(false);

        assertThatThrownBy(() -> userService.updateDescriptionUser(userUpdateDescriptionControllerDto,
                updatedUserFields, userConstFieldsControllerDto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void updatePassword_UserDoesNotExists() {
        when(userRepository.selectPasswordByLogin(anyString())).thenReturn(null);

        assertThatThrownBy(() -> userService.updatePasswordUser(userLogoPasControllerDto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void updatePassword_PasswordsDoNotMatch() {

        when(userRepository.selectPasswordByLogin(user.getEmail())).thenReturn(user);
        when(userMapper.mapUserToPasswordAndSaltController(user)).thenReturn(userPasswordAndSaltControllerDto);
        when(passwordHashed.hashPassword(userLogoPasControllerDto.oldPassword(), userPasswordAndSaltControllerDto.salt())).thenReturn("differentHashedPassword");

        Optional<UpdatePasswordError> result = userService.updatePasswordUser(userLogoPasControllerDto);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(UpdatePasswordError.PASSWORDS_DONT_MATCH);
    }

    @Test
    void updatePassword_NewPasswordsInvalid() {

        when(userRepository.selectPasswordByLogin(user.getEmail())).thenReturn(user);
        when(userMapper.mapUserToPasswordAndSaltController(user)).thenReturn(userPasswordAndSaltControllerDto);
        when(passwordHashed.hashPassword(userLogoPasControllerDto.oldPassword(), userPasswordAndSaltControllerDto.salt())).thenReturn(user.getPassword());
        when(updateUserValidator.isValidPassword(userLogoPasControllerDto.newPassword())).thenReturn(false);

        Optional<UpdatePasswordError> result = userService.updatePasswordUser(userLogoPasControllerDto);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(UpdatePasswordError.INCORRECT_NEW_PASSWORD);
    }

    @Test
    void updatePassword_InvalidUpdate() {

        when(userRepository.selectPasswordByLogin(user.getEmail())).thenReturn(user);
        when(userMapper.mapUserToPasswordAndSaltController(user)).thenReturn(userPasswordAndSaltControllerDto);
        when(passwordHashed.hashPassword(userLogoPasControllerDto.oldPassword(), userPasswordAndSaltControllerDto.salt())).thenReturn(user.getPassword());
        when(updateUserValidator.isValidPassword(userLogoPasControllerDto.newPassword())).thenReturn(true);
        when(passwordHashed.generateSalt()).thenReturn(user.getSalt());
        when(userMapper.map(eq(userLogoPasControllerDto), eq(user.getSalt()), any())).thenReturn(user);
        when(userRepository.updatePasswordByLogin(user)).thenReturn(false);

        assertThatThrownBy(() -> userService.updatePasswordUser(userLogoPasControllerDto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void updatePassword_ValidUpdate() {

        when(userRepository.selectPasswordByLogin(user.getEmail())).thenReturn(user);
        when(userMapper.mapUserToPasswordAndSaltController(user)).thenReturn(userPasswordAndSaltControllerDto);
        when(passwordHashed.hashPassword(userLogoPasControllerDto.oldPassword(), userPasswordAndSaltControllerDto.salt())).thenReturn(user.getPassword());
        when(updateUserValidator.isValidPassword(userLogoPasControllerDto.newPassword())).thenReturn(true);
        when(passwordHashed.generateSalt()).thenReturn(user.getSalt());
        when(userMapper.map(eq(userLogoPasControllerDto), eq(user.getSalt()), any())).thenReturn(user);
        when(userRepository.updatePasswordByLogin(user)).thenReturn(true);

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
        when(userMapper.mapUserToPasswordAndSaltController(user)).thenReturn(userPasswordAndSaltControllerDto);
        when(passwordHashed.hashPassword(userLoginControllerDto.password(), userPasswordAndSaltControllerDto.salt())).thenReturn("otherPassword");

        Optional<LoginError> error = userService.checkLoginUser(userLoginControllerDto);

        assertThat(error).isPresent();
        assertThat(error.get()).isEqualTo(LoginError.INCORRECT_PASSWORD);
    }

    @Test
    void checkLoginUser_CorrectPassword() {
        when(userRepository.selectPasswordByLogin(userLoginControllerDto.email())).thenReturn(user);
        when(userMapper.mapUserToPasswordAndSaltController(user)).thenReturn(userPasswordAndSaltControllerDto);
        when(passwordHashed.hashPassword(userLoginControllerDto.password(), userPasswordAndSaltControllerDto.salt())).thenReturn(user.getPassword());

        Optional<LoginError> error = userService.checkLoginUser(userLoginControllerDto);

        assertThat(error).isEmpty();
    }

    @Test
    void selectUser_Valid() {
        when(userRepository.selectByLogin(userLoginControllerDto.email())).thenReturn(user);
        when(userMapper.map(user)).thenReturn(userControllerDto);

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
                .build();

        when(userRepository.selectByNickname(nickname)).thenReturn(Optional.of(user));
        when(userMapper.mapUserToControllerForAdmin(user)).thenReturn(userForAdminControllerDto);

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
                User.builder().idUser(1).nickname("nick1").build(),
                User.builder().idUser(2).nickname("nick2").build()
        );
        List<UserForAdminControllerDto> listExpectedDto = users.stream()
                .map(user -> UserForAdminControllerDto.builder().idUser(user.getIdUser()).nickname(user.getNickname()).build())
                .collect(Collectors.toList());

        when(userRepository.selectAll()).thenReturn(users);
        when(userMapper.mapUserToControllerForAdmin(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserForAdminControllerDto.builder().idUser(user.getIdUser()).nickname(user.getNickname()).build();
        });

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