package ru.arsentiev.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
public class User {
    private long idUser;
    private String nickname;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String password;
    private String salt;
    private String phoneNumber;
    private String email;
    private LocalDate birthDate;
    private BigDecimal accountBalance;
    private UserRole role;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof User user)) {
            return false;
        }
        return getIdUser() == user.getIdUser()
               && Objects.equals(getNickname(), user.getNickname())
               && Objects.equals(getFirstName(), user.getFirstName())
               && Objects.equals(getLastName(), user.getLastName())
               && Objects.equals(getPatronymic(), user.getPatronymic())
               && Objects.equals(getPassword(), user.getPassword())
               && Objects.equals(getSalt(), user.getSalt())
               && Objects.equals(getPhoneNumber(), user.getPhoneNumber())
               && Objects.equals(getEmail(), user.getEmail())
               && Objects.equals(getBirthDate(), user.getBirthDate())
               && Objects.equals(getAccountBalance(), user.getAccountBalance())
               && getRole() == user.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdUser(), getNickname(), getFirstName(), getLastName(), getPatronymic(),
                getPassword(), getSalt(), getPhoneNumber(), getEmail(), getBirthDate(), getAccountBalance(), getRole());
    }
}
