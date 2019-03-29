package bach.project.utils;

public class MvcUtils {
    public static boolean checkPassword(String password) {
        boolean letters = false;
        boolean capitalL = false;
        boolean number = false;
        for (int j = 0; j < password.length(); j++) {
            int i = password.charAt(j);
            if (i >= 'a' && i <= 'z')
                letters = true;
            else if (i >= 'A' && i <= 'Z')
                capitalL = true;
            else if (i >= '0' && i <= '9')
                number = true;
        }

        return letters && capitalL && number && password.length() >= 9;
    }
}
