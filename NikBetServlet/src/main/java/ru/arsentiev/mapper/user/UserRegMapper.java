package ru.arsentiev.mapper.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.jsp.user.UserRegSerConDto;
import ru.arsentiev.entity.User;
import ru.arsentiev.mapper.Mapper;
import ru.arsentiev.mapper.MapperWithHash;
import ru.arsentiev.utils.LocalDateFormatter;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRegMapper implements MapperWithHash<UserRegSerConDto, User> {
    private static final UserRegMapper INSTANCE = new UserRegMapper();

    public static UserRegMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public User map(UserRegSerConDto obj, Function<String, String> hashFunction) {
        return User.builder()
                .nickname(obj.getNickname())
                .firstName(obj.getFirstName())
                .lastName(obj.getLastName())
                .patronymic(obj.getPatronymic())
                .email(obj.getEmail())
                .phoneNumber(obj.getPhoneNumber())
                .password(hashFunction.apply(obj.getPassword()))
                .birthDate(LocalDateFormatter.format(obj.getBirthDate()))
                .build();
    }
}
