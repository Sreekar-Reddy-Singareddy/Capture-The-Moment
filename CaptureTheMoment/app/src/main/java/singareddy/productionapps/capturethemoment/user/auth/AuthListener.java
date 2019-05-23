package singareddy.productionapps.capturethemoment.user.auth;

public interface AuthListener {

    public interface EmailLogin {
        // Email Login
        public void onEmailUserLoginSuccess();
        public void onEmailUserLoginFailure(String failureCode);
        public void onPasswordResetMailSent(String email);
    }

    public interface EmailSignup {
        // Email Registration
        public void onEmailUserRegisterSuccess(String email);
        public void onEmailUserRegisterFailure(String email, String failureCode);
    }

    public interface Mobile {
        // Mobile Registration & Login
        public void onMobileAuthenticationSuccess();
        public void onMobileAuthenticationFailure(String failureCode);
        default public void onOtpSent(){}
        default public void onOtpRetrievalFailed(){}
    }

    public interface Logout {
        // Logout related listeners
        public void onUserLoggedOut();
    }

}
