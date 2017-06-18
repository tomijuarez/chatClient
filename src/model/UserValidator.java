package model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tomi on 25/05/17.
 * Este código fue tomado de: https://www.mkyong.com/regular-expressions/how-to-validate-username-with-regular-expression/
 */
public class UserValidator {

    private Pattern pattern;
    private Matcher matcher;

    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";

    public UserValidator(){
        pattern = Pattern.compile(USERNAME_PATTERN);
    }

    /**
     * Validate username with regular expression
     * @param username username for validation
     * @return true valid username, false invalid username
     */
    public boolean validate(final String username){

        matcher = pattern.matcher(username);
        return matcher.matches();

    }
}