package ru.arsentiev.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
               && getAccountBalance().compareTo(user.getAccountBalance()) == 0
               && getRole() == user.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdUser(), getNickname(), getFirstName(), getLastName(), getPatronymic(),
                getPassword(), getSalt(), getPhoneNumber(), getEmail(), getBirthDate(), getAccountBalance(), getRole());
    }

    @Override
    public String toString() {
        return "User{" +
               "idUser=" + idUser +
               ", nickname='" + nickname + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", patronymic='" + patronymic + '\'' +
               ", password='" + password + '\'' +
               ", salt='" + salt + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", email='" + email + '\'' +
               ", birthDate=" + birthDate +
               ", accountBalance=" + accountBalance +
               ", role=" + role +
               '}';
    }
}
