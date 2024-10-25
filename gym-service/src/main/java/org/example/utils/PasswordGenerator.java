package org.example.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Component
public class PasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_+=<>?";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Set<String> existingPasswords = new HashSet<>();
    private static final Integer PASSWORD_LENGTH = 10;

    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        while (true) {
            for (int i = 0; i < PASSWORD_LENGTH; i++) {
                password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
            }

            String newPassword = password.toString();
            if (!existingPasswords.contains(newPassword)) {
                existingPasswords.add(newPassword);
                return newPassword;
            }

            password.setLength(0);
        }
    }
}
