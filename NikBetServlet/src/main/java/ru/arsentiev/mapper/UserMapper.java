package ru.arsentiev.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.user.*;
import ru.arsentiev.entity.User;
import ru.arsentiev.utils.LocalDateFormatter;

import java.math.BigDecimal;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    private static final UserMapper INSTANCE = new UserMapper();

    public static UserMapper getInstance() {
        return INSTANCE;
    }

    public UserDto map(User obj) {
        return UserDto.builder()
                .idUser(obj.getIdUser())
                .nickname(obj.getNickname())
                .firstName(obj.getFirstName())
                .lastName(obj.getLastName())
                .patronymic(obj.getPatronymic())
                .email(obj.getEmail())
                .phoneNumber(obj.getPhoneNumber())
                .role(obj.getRole())
                .birthDate(obj.getBirthDate())
                .build();
    }

    public UserLogoPasDto map(UserViewLogoPasDto obj, Function<String, String> hashFunction) {
        return new UserLogoPasDto(obj.email(), hashFunction.apply(obj.newPassword()));
    }

    public User map(UserUpdateDescriptionDto obj) {
        return User.builder()
                .idUser(Long.valueOf(obj.getIdUser()))
                .nickname(obj.getNickname())
                .firstName(obj.getFirstName())
                .lastName(obj.getLastName())
                .patronymic(obj.getPatronymic())
                .email(obj.getEmail())
                .phoneNumber(obj.getPhoneNumber())
                .birthDate(LocalDateFormatter.format(obj.getBirthDate()))
                .build();
    }

    public User map(UserRegistrationDto obj, Function<String, String> hashFunction) {
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

    public UserDto map(UserConstFieldsDto userConstFieldsDto, UserUpdateDescriptionDto userUpdateDescriptionDto) {
        return UserDto.builder()
                .idUser(userConstFieldsDto.idUser())
                .nickname(userUpdateDescriptionDto.getNickname())
                .firstName(userUpdateDescriptionDto.getFirstName())
                .lastName(userUpdateDescriptionDto.getLastName())
                .patronymic(userUpdateDescriptionDto.getPatronymic())
                .email(userUpdateDescriptionDto.getEmail())
                .phoneNumber(userUpdateDescriptionDto.getPhoneNumber())
                .role(userConstFieldsDto.role())
                .birthDate(LocalDateFormatter.format(userUpdateDescriptionDto.getBirthDate()))
                .build();
    }

    public UserMoneyControllerDto map(UserMoneyViewDto userMoneyViewDto) {
        return new UserMoneyControllerDto(userMoneyViewDto.idUser(), BigDecimal.valueOf(
                Double.parseDouble(userMoneyViewDto.summa())));
    }
}
