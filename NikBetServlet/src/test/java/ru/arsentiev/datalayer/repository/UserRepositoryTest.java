package ru.arsentiev.datalayer.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.arsentiev.datalayer.TestConnectionGetter;
import ru.arsentiev.entity.User;
import ru.arsentiev.entity.UserRole;
import ru.arsentiev.exception.RepositoryException;
import ru.arsentiev.processing.query.UserQueryCreator;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;
import ru.arsentiev.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRepositoryTest {
    private final TestConnectionGetter connectionGetter = TestConnectionGetter.getInstance();
    private final UserQueryCreator userQueryCreator = new UserQueryCreator();
    private final UserRepository userRepository = new UserRepository(connectionGetter, userQueryCreator);
    private final UpdatedUserFields updatedUserFieldsAll;
    private final UpdatedUserFields updatedUserFieldsNothing;
    private final UpdatedUserFields updatedUserFieldsOnlyNickname;

    {

        updatedUserFieldsAll = UpdatedUserFields.builder()
                .isNewBirthDate(true)
                .isNewLastName(true)
                .isNewEmail(true)
                .isNewPatronymic(true)
                .isNewFirstName(true)
                .isNewNickName(true)
                .isNewPhoneNumber(true)
                .build();

        updatedUserFieldsNothing = UpdatedUserFields.builder()
                .isNewBirthDate(false)
                .isNewLastName(false)
                .isNewEmail(true)
                .isNewPatronymic(false)
                .isNewFirstName(false)
                .isNewNickName(false)
                .isNewPhoneNumber(false)
                .build();

        updatedUserFieldsOnlyNickname = UpdatedUserFields.builder()
                .isNewBirthDate(false)
                .isNewLastName(false)
                .isNewEmail(false)
                .isNewPatronymic(false)
                .isNewFirstName(false)
                .isNewNickName(false)
                .isNewPhoneNumber(false)
                .build();

    }

    private static Stream<List<User>> generateValidUserList() {
        User user1 = User.builder()
                .nickname("user1").firstName("John")
                .lastName("Doe").patronymic("Smith").password("password1")
                .salt("salt1").phoneNumber("+79969291133").email("user1@example.com")
                .birthDate(LocalDate.of(1990, 5, 15))
                .accountBalance(new BigDecimal("0")).role(UserRole.USER)
                .build();

        User user2 = User.builder()
                .nickname("user2").firstName("Alice")
                .lastName("Johnson").patronymic("Marie").password("password2")
                .salt("salt2").phoneNumber("+79969291144").email("user2@example.com")
                .birthDate(LocalDate.of(1985, 9, 20))
                .accountBalance(new BigDecimal("0")).role(UserRole.USER)
                .build();
        return Stream.of(List.of(user1, user2), List.of(user1));
    }

    private static Stream<User> generateValidUser() {
        User user1 = User.builder()
                .nickname("user1").firstName("John")
                .lastName("Doe").password("password1")
                .salt("salt1").phoneNumber("+79969291133").email("user1@example.com")
                .birthDate(LocalDate.of(1990, 5, 15))
                .accountBalance(new BigDecimal("0")).role(UserRole.USER)
                .build();

        User user2 = User.builder()
                .nickname("user2").firstName("Alice")
                .lastName("Johnson").patronymic("Marie").password("password2")
                .salt("salt2").phoneNumber("+98765432101").email("user2@example.com")
                .birthDate(LocalDate.of(1985, 9, 20))
                .accountBalance(new BigDecimal("0")).role(UserRole.USER)
                .build();
        return Stream.of(user1, user2);
    }

    private static Stream<User> generateInvalidUser() {
        User user1 = User.builder()
                .nickname("user1")
                .build();

        User user2 = User.builder()
                .lastName("Johnson").patronymic("Marie").role(UserRole.USER)
                .build();

        User user3 = User.builder()
                .firstName("Bob")
                .lastName("Brown")
                .password("password3")
                .salt("salt3")
                .phoneNumber("+79969291144")
                .email("user3@example.com")
                .birthDate(LocalDate.of(1995, 12, 10))
                .accountBalance(new BigDecimal("0"))
                .role(UserRole.ADMIN)
                .build();

        return Stream.of(user1, user2, user3);
    }

    private User defaultUser() {
        return User.builder()
                .nickname("user1").firstName("John")
                .lastName("Doe").patronymic("Smith").password("password1")
                .salt("salt1").phoneNumber("+79969291133").email("user1@example.com")
                .birthDate(LocalDate.of(1990, 5, 15))
                .accountBalance(new BigDecimal("0")).role(UserRole.USER)
                .build();
    }

    private void settingToDefault(User user) {
        user.setPassword(null);
        user.setSalt(null);
    }

    @SneakyThrows
    @BeforeEach
    public void clear() {
        try (Connection connection = connectionGetter.get()) {
            //language=PostgreSQL
            String CLEAR_TABLE = "TRUNCATE nikbet_test.public.users RESTART IDENTITY CASCADE";
            connection.prepareStatement(CLEAR_TABLE).executeUpdate();
        }
    }

    @ParameterizedTest
    @MethodSource("generateValidUser")
    void insertValidUserTest(User user) {
        assertThat(userRepository.insert(user)).isTrue();
        assertThat(user.getIdUser()).isNotZero();

        settingToDefault(user);

        Optional<User> userOptional = userRepository.selectById(user.getIdUser());
        assertThat(userOptional.isPresent()).isTrue();
        User userInsert = userOptional.get();
        assertThat(userInsert).isEqualTo(user);
    }

    @ParameterizedTest
    @MethodSource("generateInvalidUser")
    void insertInvalidUserTest(User user) {
        assertThatThrownBy(() -> userRepository.insert(user)).isInstanceOf(RepositoryException.class);
    }

    @ParameterizedTest
    @MethodSource("generateValidUserList")
    void selectAllUserTest(List<User> users) {
        for (var user : users) {
            userRepository.insert(user);
        }
        List<User> usersFromRepository = userRepository.selectAll();
        assertThat(usersFromRepository).isEqualTo(users);
    }

    @Test
    void selectUserByValidSomethingTest() {
        User user = defaultUser();

        userRepository.insert(user);
        settingToDefault(user);

        Optional<User> userOptionalId = userRepository.selectById(user.getIdUser());
        assertThat(userOptionalId.isPresent()).isTrue();
        User userFromRepositoryId = userOptionalId.get();
        assertThat(userFromRepositoryId).isEqualTo(user);

        Optional<User> userOptionalNickname = userRepository.selectByNickname(user.getNickname());
        assertThat(userOptionalNickname.isPresent()).isTrue();
        User userFromRepositoryNickname = userOptionalNickname.get();
        assertThat(userFromRepositoryNickname).isEqualTo(user);

        User userFromRepositoryLogin = userRepository.selectByLogin(user.getEmail());
        assertThat(userFromRepositoryLogin).isEqualTo(user);
    }

    @Test
    void selectUserByInvalidSomethingTest() {
        long wrongId = -1L;
        String wrongNickname = "wrongNickname";
        String wrongEmail = "wrongEmail";

        assertThat(userRepository.selectById(wrongId)).isEmpty();
        assertThat(userRepository.selectByNickname(wrongNickname)).isEmpty();
        assertThat(userRepository.selectByLogin(wrongEmail)).isNull();
    }

    @Test
    void selectPasswordByValidLoginTest() {
        User user = defaultUser();

        userRepository.insert(user);

        User userSelectPassword = userRepository.selectPasswordByLogin(user.getEmail());
        assertThat(userSelectPassword.getPassword()).isEqualTo(user.getPassword());
        assertThat(userSelectPassword.getSalt()).isEqualTo(user.getSalt());
    }

    @Test
    void selectPasswordByInvalidLoginTest() {
        String wrongLogin = "wrongLogin";

        User userSelectPassword = userRepository.selectPasswordByLogin(wrongLogin);
        assertThat(userSelectPassword.getPassword()).isNull();
        assertThat(userSelectPassword.getSalt()).isNull();
    }

    @Test
    void selectAndUpdateBalanceByValidIdTest() {
        User user = defaultUser();

        userRepository.insert(user);

        BigDecimal summaDeposit = BigDecimal.valueOf(30.4);
        user.setAccountBalance(summaDeposit);
        assertThat(userRepository.depositMoneyById(user.getIdUser(), summaDeposit)).isTrue();

        Optional<BigDecimal> balanceAfterDeposit = userRepository.selectBalanceById(user.getIdUser());
        assertThat(balanceAfterDeposit.isPresent()).isTrue();
        assertThat(balanceAfterDeposit.get()).isEqualTo(user.getAccountBalance());

        BigDecimal summaWithdraw = BigDecimal.valueOf(10);
        BigDecimal summaBalance = summaDeposit.subtract(summaWithdraw);
        user.setAccountBalance(summaBalance);
        assertThat(userRepository.withdrawMoneyById(user.getIdUser(), summaWithdraw)).isTrue();

        Optional<BigDecimal> balanceAfterWithdraw = userRepository.selectBalanceById(user.getIdUser());
        assertThat(balanceAfterWithdraw.isPresent()).isTrue();
        assertThat(balanceAfterWithdraw.get()).isEqualTo(user.getAccountBalance());
    }

    @Test
    void selectAndUpdateBalanceByInvalidIdTest() {
        long wrongIdUser = -1L;
        BigDecimal summa = BigDecimal.valueOf(30.4);
        assertThat(userRepository.depositMoneyById(wrongIdUser, summa)).isFalse();
        assertThat(userRepository.withdrawMoneyById(wrongIdUser, summa)).isFalse();

        Optional<BigDecimal> balance = userRepository.selectBalanceById(wrongIdUser);
        assertThat(balance.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("generateValidUserList")
    void deleteValidUserTest(List<User> users) {
        for (var user : users) {
            userRepository.insert(user);
        }
        long idUser = users.get(0).getIdUser();

        assertThat(userRepository.delete(idUser)).isTrue();

        assertThat(userRepository.selectById(idUser)).isEmpty();
    }

    @Test
    void deleteInvalidUserTest() {
        long wrongIdUser = -1L;

        assertThat(userRepository.delete(wrongIdUser)).isFalse();
    }

    @Test
    void updateUserTest() {
        User user = defaultUser();
        userRepository.insert(user);

        user.setNickname("newNickname");
        assertThat(userRepository.update(user)).isTrue();

        Optional<User> optionalUser = userRepository.selectById(user.getIdUser());
        assertThat(optionalUser.isPresent()).isTrue();
        User newUser = optionalUser.get();

        settingToDefault(user);
        assertThat(newUser).isEqualTo(user);
    }

    @Test
    void updateUserWithFieldTest() {
        User user = defaultUser();
        userRepository.insert(user);

        assertThat(userRepository.updateDescriptionWithDynamicCreation(user, updatedUserFieldsNothing)).isTrue();

        user.setNickname("newNickname");
        assertThat(userRepository.updateDescriptionWithDynamicCreation(user, updatedUserFieldsOnlyNickname)).isTrue();


        user.setBirthDate(LocalDate.of(2000, 2, 3));
        user.setLastName("newLastName");
        user.setEmail("newEmail");
        user.setPassword("newPatronymic");
        user.setFirstName("newFirstName");
        user.setNickname("newNickname");
        user.setPhoneNumber("+79969291133");
        assertThat(userRepository.updateDescriptionWithDynamicCreation(user, updatedUserFieldsAll)).isTrue();
    }

    @Test
    void updatePasswordByValidLoginTest() {
        User user = defaultUser();
        userRepository.insert(user);
        user.setPassword("newPassword");
        user.setSalt("newSalt");

        assertThat(userRepository.updatePasswordByLogin(user)).isTrue();

        User newUser = userRepository.selectPasswordByLogin(user.getEmail());
        assertThat(newUser).isNotNull();
        assertThat(newUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(newUser.getSalt()).isEqualTo(user.getSalt());
    }
}