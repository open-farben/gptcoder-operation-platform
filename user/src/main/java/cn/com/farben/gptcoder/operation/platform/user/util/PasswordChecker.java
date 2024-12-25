package cn.com.farben.gptcoder.operation.platform.user.util;

public class PasswordChecker {
    public static int isValidPassword(String password, String username) {
        // 检查长度
        if (password.length() < 8) {
            return 0;
        }
        // 检查是否包含至少两种字符类型
        boolean hasLowercase = false;
        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        int valid=0;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isLowerCase(c)) {
                hasLowercase = true;
                valid+=1;
            } else if (Character.isUpperCase(c)) {
                hasUppercase = true;
                valid+=1;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
                valid+=1;
            } else if (isSpecialChar(c)) {
                hasSpecialChar = true;
                valid+=1;
            }
        }
        if (password.equals(username)) {
            return 0;
        }
//        if (!(hasLowercase && hasUppercase && hasDigit && hasSpecialChar)) {
//            return false;
//        }
        return valid;
    }

    private static boolean isSpecialChar(char c) {
        String specialChars = "~!@#$%^&*()-_=+\\|[{}];:'\",<.>/?\\s";
        return specialChars.contains(String.valueOf(c));
    }

}