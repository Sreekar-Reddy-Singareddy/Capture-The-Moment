package singareddy.productionapps.capturethemoment.user;

public interface AuthenticationListener {

    public interface EmailLogin {
        // Email Login
        public void onEmailUserLoginSuccess();
        public void onEmailUserLoginFailure(String failureCode);
    }

    public interface EmailSignup {
        // Email Registration
        public void onEmailUserRegisterSuccess(String email);
        public void onEmailUserRegisterFailure(String email, String failureCode);
    }

    public interface Mobile {
        // Mobile Registration & Login
        public void onMobileAuthenticationSuccess(String mobile);
        public void onMobileAuthenticationFailure(String mobile, String failureCode);
        public void onMobileFirstTimeLogin(String mobile);
    }

    public interface Logout {
        // Logout related listeners
        public void onUserLoggedOut();
    }

}
