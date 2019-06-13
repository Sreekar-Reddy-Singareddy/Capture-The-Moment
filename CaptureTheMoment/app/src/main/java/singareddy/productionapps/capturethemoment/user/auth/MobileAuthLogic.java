package singareddy.productionapps.capturethemoment.user.auth;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import singareddy.productionapps.capturethemoment.HomeActivity;
import singareddy.productionapps.capturethemoment.R;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MobileAuthLogic {

    AuthViewModel authViewModel;
    Context context;

    Button loginButton;
    EditText mobileNumber, otpInput;
    ProgressBar progressLoader;
    TextView autoOTPLabel, autoOTPTimer;
    TextView resendOtpLabel;
    View otpInputIcon;

    public MobileAuthLogic(Context context, View parentLayout, AuthViewModel authViewModel) {
        this.authViewModel = authViewModel;
        this.context = context;

        mobileNumber = parentLayout.findViewById(R.id.activity_mobile_login_mobile);
        otpInput = parentLayout.findViewById(R.id.activity_mobile_login_otp);
        otpInputIcon = parentLayout.findViewById(R.id.activity_mobile_login_otp_icon);
        autoOTPLabel = parentLayout.findViewById(R.id.activity_mobile_login_tv_otp_label);
        autoOTPTimer = parentLayout.findViewById(R.id.activity_mobile_login_tv_time);
        resendOtpLabel = parentLayout.findViewById(R.id.activity_mobile_login_tv_resend_label);
        resendOtpLabel.setOnClickListener(this::resendOTP);
        progressLoader = parentLayout.findViewById(R.id.activity_mobile_login_pb_loading);
        loginButton = parentLayout.findViewById(R.id.login_bt_continue);
        loginButton.setOnClickListener(this::login);

        setViewsVisibility(VISIBLE,GONE,VISIBLE,INVISIBLE,INVISIBLE,INVISIBLE,INVISIBLE,INVISIBLE);
    }

    private void login(View view) {

        if (otpInput.getVisibility() == VISIBLE) {
            setViewsVisibility(VISIBLE,VISIBLE,INVISIBLE,VISIBLE,INVISIBLE,INVISIBLE,INVISIBLE,INVISIBLE);
        }
        authViewModel.authorizePhoneCredentials(
                mobileNumber.getText().toString().trim(),
                otpInput.getText().toString().trim());
    }

    private void resendOTP(View view) {
        authViewModel.resendOTP(mobileNumber.getText().toString().trim());
    }

    public void autoRetrieveOtp() {
        OtpRetriveTask otpRetriveTask = new OtpRetriveTask(resendOtpLabel, autoOTPTimer, autoOTPLabel, loginButton, progressLoader, otpInput, otpInputIcon, 10);
        otpRetriveTask.execute();
        setViewsVisibility(VISIBLE,GONE,INVISIBLE,VISIBLE,INVISIBLE,VISIBLE,VISIBLE,VISIBLE);
    }

    private void setViewsVisibility(int mobile, int otp, int login, int loader, int resend, int resendTimer, int autoOTP, int autoTimer) {
        mobileNumber.setVisibility(mobile);
        otpInput.setVisibility(otp);
        otpInputIcon.setVisibility(otp);
        loginButton.setVisibility(login);
        progressLoader.setVisibility(loader);
        resendOtpLabel.setVisibility(resend);
        autoOTPLabel.setVisibility(autoOTP);
        autoOTPTimer.setVisibility(autoTimer);
    }

    public void loginFailed(String failureCode) {
        setViewsVisibility(VISIBLE, VISIBLE, VISIBLE, GONE, VISIBLE,VISIBLE,INVISIBLE,INVISIBLE);
//        if (failureCode.equals("EMPTY_FIELDS")) {
//            Toast.makeText(this, "Mobile number is invalid", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        else if (failureCode.equals(AppUtilities.FailureCodes.NO_OTP)) {
//            Toast.makeText(this, "OTP Required", Toast.LENGTH_SHORT).show();
//        }
//        Toast.makeText(this, "Login Failure", Toast.LENGTH_SHORT).show();
        otpInput.setText("");
    }

    public void loginSuccess(){
        Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show();
        // On successful login, erase all the data.
        authViewModel.eraseLocalData();
        Intent homeIntent = new Intent(context, HomeActivity.class);
        context.startActivity(homeIntent);
        // Once data is erased, download this user's data
        authViewModel.setupInitialData();
    }
}
