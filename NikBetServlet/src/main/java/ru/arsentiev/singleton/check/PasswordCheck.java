package ru.arsentiev.singleton.check;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordCheck {

    private static final PasswordCheck INSTANCE = new PasswordCheck();

    public static PasswordCheck getInstance() {
        return INSTANCE;
    }

    private final Pattern PASSWORD_PATTEN = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)\\S[a-zA-Z\\d]{8,20}");

    public boolean check(String string) {
        return PASSWORD_PATTEN.matcher(string).matches();
    }
}
