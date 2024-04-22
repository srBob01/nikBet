package ru.arsentiev.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.user.controller.*;
import ru.arsentiev.dto.user.view.*;
import ru.arsentiev.entity.User;
import ru.arsentiev.utils.LocalDateFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    private static final UserMapper INSTANCE = new UserMapper();

    public static UserMapper getInstance() {
        return INSTANCE;
    }

    public UserControllerDto map(User obj) {
        return UserControllerDto.builder()
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

    public User map(UserLogoPasControllerDto obj, String salt, BiFunction<String, String, String> function) {
        return User.builder()
                .email(obj.email())
                .salt(salt)
                .password(function.apply(obj.newPassword(), salt))
                .build();
    }

    public User map(UserUpdateDescriptionControllerDto obj) {
        return User.builder()
                .idUser(Long.valueOf(obj.getIdUser()))
                .nickname(obj.getNickname())
                .firstName(obj.getFirstName())
                .lastName(obj.getLastName())
                .patronymic(obj.getPatronymic())
                .email(obj.getEmail())
                .phoneNumber(obj.getPhoneNumber())
                .birthDate(obj.getBirthDate())
                .build();
    }

    public User map(UserRegistrationControllerDto obj, String salt, BiFunction<String, String, String> function) {
        return User.builder()
                .nickname(obj.getNickname())
                .firstName(obj.getFirstName())
                .lastName(obj.getLastName())
                .patronymic(obj.getPatronymic())
                .email(obj.getEmail())
                .phoneNumber(obj.getPhoneNumber())
                .password(function.apply(obj.getPassword(), salt))
                .salt(salt)
                .birthDate(obj.getBirthDate())
                .build();
    }

    public UserControllerDto map(UserConstFieldsControllerDto userConstFieldsControllerDto, UserUpdateDescriptionControllerDto userUpdateDescriptionControllerDto) {
        return UserControllerDto.builder()
                .idUser(userConstFieldsControllerDto.idUser())
                .nickname(userUpdateDescriptionControllerDto.getNickname())
                .firstName(userUpdateDescriptionControllerDto.getFirstName())
                .lastName(userUpdateDescriptionControllerDto.getLastName())
                .patronymic(userUpdateDescriptionControllerDto.getPatronymic())
                .email(userUpdateDescriptionControllerDto.getEmail())
                .phoneNumber(userUpdateDescriptionControllerDto.getPhoneNumber())
                .role(userConstFieldsControllerDto.role())
                .birthDate(userUpdateDescriptionControllerDto.getBirthDate())
                .build();
    }

    public UserLoginControllerDto map(UserLoginViewDto userLoginViewDto) {
        return UserLoginControllerDto.builder()
                .email(userLoginViewDto.email())
                .password(userLoginViewDto.password())
                .build();
    }

    public UserRegistrationControllerDto map(UserRegistrationViewDto userRegistrationViewDto) {
        return UserRegistrationControllerDto.builder()
                .email(userRegistrationViewDto.getEmail())
                .password(userRegistrationViewDto.getPassword())
                .phoneNumber(userRegistrationViewDto.getPhoneNumber())
                .patronymic(userRegistrationViewDto.getPatronymic())
                .firstName(userRegistrationViewDto.getFirstName())
                .lastName(userRegistrationViewDto.getLastName())
                .nickname(userRegistrationViewDto.getNickname())
                .birthDate(LocalDateFormatter.format(userRegistrationViewDto.getBirthDate()))
                .build();
    }

    public UserMoneyControllerDto map(UserMoneyViewDto userMoneyViewDto) {
        return UserMoneyControllerDto.builder()
                .idUser(userMoneyViewDto.idUser())
                .summa(BigDecimal.valueOf(Double.parseDouble(userMoneyViewDto.summa())))
                .build();
    }

    public UserUpdateDescriptionControllerDto map(UserUpdateDescriptionViewDto userUpdateDescriptionViewDto) {
        return UserUpdateDescriptionControllerDto.builder()
                .email(userUpdateDescriptionViewDto.getEmail())
                .idUser(userUpdateDescriptionViewDto.getIdUser())
                .phoneNumber(userUpdateDescriptionViewDto.getPhoneNumber())
                .patronymic(userUpdateDescriptionViewDto.getPatronymic())
                .firstName(userUpdateDescriptionViewDto.getFirstName())
                .lastName(userUpdateDescriptionViewDto.getLastName())
                .nickname(userUpdateDescriptionViewDto.getNickname())
                .birthDate(LocalDateFormatter.format(userUpdateDescriptionViewDto.getBirthDate()))
                .build();
    }

    public UserLogoPasControllerDto map(UserLogoPasViewDto userLogoPasViewDto) {
        return UserLogoPasControllerDto.builder()
                .email(userLogoPasViewDto.email())
                .newPassword(userLogoPasViewDto.newPassword())
                .oldPassword(userLogoPasViewDto.oldPassword())
                .build();
    }

    public UserViewDto map(UserControllerDto userControllerDto) {
        return UserViewDto.builder()
                .email(userControllerDto.email())
                .idUser(userControllerDto.idUser())
                .phoneNumber(userControllerDto.phoneNumber())
                .patronymic(userControllerDto.patronymic())
                .firstName(userControllerDto.firstName())
                .lastName(userControllerDto.lastName())
                .nickname(userControllerDto.nickname())
                .birthDate(userControllerDto.birthDate().format(LocalDateFormatter.FORMATTER))
                .role(userControllerDto.role().name())
                .build();
    }

    public UserForAdminControllerDto mapUserToControllerForAdmin(User user) {
        return UserForAdminControllerDto.builder()
                .idUser(user.getIdUser())
                .nickname(user.getNickname())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .accountBalance(user.getAccountBalance())
                .birthDate(user.getBirthDate())
                .email(user.getEmail())
                .patronymic(user.getPatronymic())
                .build();
    }

    public UserForAdminViewDto mapUserControllerToViewForAdmin(UserForAdminControllerDto user) {
        return UserForAdminViewDto.builder()
                .idUser(user.idUser().toString())
                .nickname(user.nickname())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .phoneNumber(user.phoneNumber())
                .accountBalance(user.accountBalance().setScale(2, RoundingMode.HALF_UP).toString())
                .birthDate(user.birthDate().format(LocalDateFormatter.FORMATTER))
                .email(user.email())
                .patronymic(user.patronymic())
                .build();
    }
}
