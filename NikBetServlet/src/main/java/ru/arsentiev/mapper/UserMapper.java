package ru.arsentiev.mapper;

import ru.arsentiev.dto.user.controller.*;
import ru.arsentiev.dto.user.view.*;
import ru.arsentiev.entity.User;
import ru.arsentiev.processing.dateformatter.LocalDateFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;

public class UserMapper {
    private final LocalDateFormatter localDateFormatter;

    public UserMapper(LocalDateFormatter localDateFormatter) {
        this.localDateFormatter = localDateFormatter;
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

    public UserPasswordAndSaltControllerDto mapUserToPasswordAndSaltController(User user) {
        return UserPasswordAndSaltControllerDto.builder()
                .password(user.getPassword())
                .salt(user.getSalt())
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
                .idUser(obj.idUser())
                .nickname(obj.nickname())
                .firstName(obj.firstName())
                .lastName(obj.lastName())
                .patronymic(obj.patronymic())
                .email(obj.email())
                .phoneNumber(obj.phoneNumber())
                .birthDate(obj.birthDate())
                .build();
    }

    public User map(UserRegistrationControllerDto obj, String salt, BiFunction<String, String, String> function) {
        return User.builder()
                .nickname(obj.nickname())
                .firstName(obj.firstName())
                .lastName(obj.lastName())
                .patronymic(obj.patronymic())
                .email(obj.email())
                .phoneNumber(obj.phoneNumber())
                .password(function.apply(obj.password(), salt))
                .salt(salt)
                .birthDate(obj.birthDate())
                .build();
    }

    public UserControllerDto map(UserConstFieldsControllerDto userConstFieldsControllerDto,
                                 UserUpdateDescriptionControllerDto userUpdateDescriptionControllerDto) {
        return UserControllerDto.builder()
                .idUser(userConstFieldsControllerDto.idUser())
                .nickname(userUpdateDescriptionControllerDto.nickname())
                .firstName(userUpdateDescriptionControllerDto.firstName())
                .lastName(userUpdateDescriptionControllerDto.lastName())
                .patronymic(userUpdateDescriptionControllerDto.patronymic())
                .email(userUpdateDescriptionControllerDto.email())
                .phoneNumber(userUpdateDescriptionControllerDto.phoneNumber())
                .role(userConstFieldsControllerDto.role())
                .birthDate(userUpdateDescriptionControllerDto.birthDate())
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
                .birthDate(localDateFormatter.format(userRegistrationViewDto.getBirthDate()))
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
                .idUser(Long.parseLong(userUpdateDescriptionViewDto.getIdUser()))
                .phoneNumber(userUpdateDescriptionViewDto.getPhoneNumber())
                .patronymic(userUpdateDescriptionViewDto.getPatronymic())
                .firstName(userUpdateDescriptionViewDto.getFirstName())
                .lastName(userUpdateDescriptionViewDto.getLastName())
                .nickname(userUpdateDescriptionViewDto.getNickname())
                .birthDate(localDateFormatter.format(userUpdateDescriptionViewDto.getBirthDate()))
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
                .birthDate(userControllerDto.birthDate().format(localDateFormatter.FORMATTER))
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
                .idUser(String.valueOf(user.idUser()))
                .nickname(user.nickname())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .phoneNumber(user.phoneNumber())
                .accountBalance(user.accountBalance().setScale(2, RoundingMode.HALF_UP).toString())
                .birthDate(user.birthDate().format(localDateFormatter.FORMATTER))
                .email(user.email())
                .patronymic(user.patronymic())
                .build();
    }

    public UserValidatorViewDto mapRegistrationToValidator(UserRegistrationViewDto dto) {
        return UserValidatorViewDto.builder()
                .nickname(dto.getNickname())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .patronymic(dto.getPatronymic())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .birthDate(dto.getBirthDate())
                .build();
    }

    public UserValidatorViewDto mapUpdateToValidator(UserUpdateDescriptionViewDto dto) {
        return UserValidatorViewDto.builder()
                .nickname(dto.getNickname())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .patronymic(dto.getPatronymic())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .password(null)
                .birthDate(dto.getBirthDate())
                .build();
    }
}
