package ru.arsentiev.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class User {
    private Long idUser;
    private String nickname;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String password;
    private String phoneNumber;
    private String email;
    private LocalDate birthDate;
    private BigDecimal accountBalance;
    private UserRole role;
}
