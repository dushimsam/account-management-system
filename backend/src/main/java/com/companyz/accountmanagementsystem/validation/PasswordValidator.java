package com.companyz.accountmanagementsystem.validation;


import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class PasswordValidator{
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";

    public String isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return "Password must be at least 6 characters long";
        }
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            return "Password must contain at least one lowercase letter";
        }
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            return "Password must contain at least one uppercase letter";
        }
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            return "Password must contain at least one digit";
        }
        if (!Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find()) {
            return "Password must contain at least one special character";
        }
        return null; // Password is valid
    }

}
