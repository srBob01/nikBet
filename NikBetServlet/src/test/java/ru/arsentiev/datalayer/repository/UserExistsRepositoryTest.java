package ru.arsentiev.datalayer.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.arsentiev.datalayer.TestConnectionGetter;
import ru.arsentiev.entity.User;
import ru.arsentiev.entity.UserRole;
import ru.arsentiev.processing.query.UserQueryCreator;
import ru.arsentiev.repository.UserExistsRepository;
import ru.arsentiev.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserExistsRepositoryTest {
    private final TestConnectionGetter connectionGetter = TestConnectionGetter.getInstance();
    private final UserExistsRepository userExistsRepository = new UserExistsRepository(connectionGetter);
    private final UserQueryCreator userQueryCreator = new UserQueryCreator();
    private final UserRepository userRepository = new UserRepository(connectionGetter, userQueryCreator);


    private static User defaultUser() {
        return User.builder()
                .nickname("user1").firstName("John")
                .lastName("Doe").patronymic("Smith").password("password1")
                .salt("salt1").phoneNumber("+79969291133").email("user1@example.com")
                .birthDate(LocalDate.of(1990, 5, 15))
                .accountBalance(new BigDecimal("0")).role(UserRole.USER)
                .build();
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


    @Test
    void existsByValidSomethingTest() {
        User user = defaultUser();
        userRepository.insert(user);

        assertThat(userExistsRepository.existsByEmail(user.getEmail())).isTrue();
        assertThat(userExistsRepository.existsByNickname(user.getNickname())).isTrue();
        assertThat(userExistsRepository.existsByPhoneNumber(user.getPhoneNumber())).isTrue();
    }

    @Test
    void existsByInvalidSomethingTest() {
        assertThat(userExistsRepository.existsByEmail("wrongEmail")).isFalse();
        assertThat(userExistsRepository.existsByNickname("wrongNickname")).isFalse();
        assertThat(userExistsRepository.existsByPhoneNumber("wrongPhoneNumber")).isFalse();
        assertThat(userExistsRepository.existsByEmail(null)).isFalse();
        assertThat(userExistsRepository.existsByNickname(null)).isFalse();
        assertThat(userExistsRepository.existsByPhoneNumber(null)).isFalse();
    }
}