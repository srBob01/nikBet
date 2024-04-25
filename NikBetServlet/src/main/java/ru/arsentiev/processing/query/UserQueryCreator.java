package ru.arsentiev.processing.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.entity.User;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UserQueryCreator {
    public Optional<String> createUserUpdateQuery(User user, UpdatedUserFields fields) {
        String start = "UPDATE users SET ";
        StringBuilder sql = new StringBuilder(start);

        if (fields.isNewNickName()) {
            sql.append("nickname = '").append(user.getNickname()).append("', ");
        }
        if (fields.isNewEmail()) {
            sql.append("email = '").append(user.getEmail()).append("', ");
        }
        if (fields.isNewFirstName()) {
            sql.append("firstName = '").append(user.getFirstName()).append("', ");
        }
        if (fields.isNewLastName()) {
            sql.append("lastName = '").append(user.getLastName()).append("', ");
        }
        if (fields.isNewPatronymic()) {
            sql.append("patronymic = '").append(user.getPatronymic()).append("', ");
        }
        if (fields.isNewPhoneNumber()) {
            sql.append("phoneNumber = '").append(user.getPhoneNumber()).append("', ");
        }
        if (fields.isNewBirthDate()) {
            sql.append("birthDate = TO_DATE('").append(user.getBirthDate()).append("', 'YYYY-MM-DD'), ");
        }

        if (sql.toString().endsWith(", ")) {
            sql.setLength(sql.length() - 2);
        } else {
            return Optional.empty();
        }

        sql.append("WHERE idUser =").append(user.getIdUser()).append(" ;");

        return Optional.of(sql.toString());
    }
}
