package singareddy.productionapps.capturethemoment;

public class AppUtilities {

    public static boolean UPDATE_PROFILE_DIALOG_SHOWN = false;

    public static class FailureCodes {
        public static final String EMPTY_EMAIL = "EMPTY_EMAIL";
        public static final String EMPTY_PASSWORD = "EMPTY_PASSWORD";
        public static final String PASSWORD_MISMATCH = "PASSWORD_MISMATCH";
        public static final String PROFILE_NAME_EMPTY = "PROFILE_NAME_EMPTY";
        public static final String PROFILE_AGE_INVALID = "PROFILE_AGE_INVALID";
        public static final String PROFILE_EMAIL_EMPTY = "PROFILE_EMAIL_EMPTY";
        public static final String PROFILE_MOBILE_EMPTY = "PROFILE_MOBILE_EMPTY";
        public static final String PROFILE_EMAIL_INVALID = "PROFILE_EMAIL_INVALID";
        public static final String PROFILE_MOBILE_INVALID = "PROFILE_MOBILE_INVALID";
        public static final String EMAIL_EXISTS = "The email address is already in use by another account.";
    }
}
