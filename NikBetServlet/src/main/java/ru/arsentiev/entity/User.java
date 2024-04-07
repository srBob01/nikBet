package ru.arsentiev.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class User {
    private long idUser;
    private String nickname;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String password;
    private String phoneNumber;
    private String email;
    private LocalDate birthDate;
    private BigDecimal accountBalance;
    private final UserRole role;
    private List<Prediction> predictionList;

    @Builder(builderMethodName = "userBuilder")
    public User(String nickname, String firstName, String lastName, String patronymic,
                String password, String phoneNumber, String email, LocalDate birthDate,
                BigDecimal accountBalance, UserRole role, List<Prediction> predictionList) {
        this.idUser = 0;
        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDate = birthDate;
        this.accountBalance = accountBalance;
        this.role = role == null ? UserRole.USER : role;
        this.predictionList = predictionList;
    }
}
