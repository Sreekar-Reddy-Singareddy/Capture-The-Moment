package singareddy.productionapps.capturethemoment.user.auth;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OtpRetriveTask extends AsyncTask<Void, Integer, Void> {
    private static String TAG = "OtpRetriveTask";
    Integer timer;
    TextView resendLabel;
    TextView autoTimer, autoOTPLabel;
    Button loginButton;
    ProgressBar loader;
    EditText otpInput;
    View otpInputIcon;

    public OtpRetriveTask(TextView autoTimer, TextView autoOTPLabel, Button loginButton, ProgressBar loader, EditText otpInput) {
        Log.i(TAG, "OtpRetriveTask: Timer label: "+ autoTimer);
        this.autoTimer = autoTimer;
        this.autoOTPLabel = autoOTPLabel;
        this.loginButton = loginButton;
        this.loader = loader;
        this.otpInput = otpInput;
    }

    public OtpRetriveTask(TextView resendOtpLabel, TextView autoOTPTimer, TextView autoOTPLabel, Button loginButton, ProgressBar progressLoader, EditText otpInput, View otpInputIcon, int time) {
        this.resendLabel = resendOtpLabel;
        this.autoTimer = autoOTPTimer;
        this.autoOTPLabel = autoOTPLabel;
        this.loginButton = loginButton;
        this.loader = progressLoader;
        this.otpInput = otpInput;
        this.otpInputIcon = otpInputIcon;
        this.timer = time;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.autoTimer.setText(timer.toString()+"s");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Integer currentTime = timer;
            while (currentTime > 0) {
                Thread.sleep(1000);
                currentTime--;
                publishProgress(currentTime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Integer currentTime = values[0];
        Log.i(TAG, "onProgressUpdate: Timer: "+currentTime);
        /**
         * There are two types of time
         * 1. Auto OTP timer
         * 2. Resend OTP timer
         */
        if (timer == 60) {
            // Resend OTP timer
        }
        else if (timer == 10) {
            // Auto OTP timer
            autoTimer.setText(currentTime.toString()+"s");
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (timer == 10) {
            autoOTPLabel.setVisibility(View.GONE);
            autoTimer.setVisibility(View.GONE);
            loader.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            otpInput.setVisibility(View.VISIBLE);
            otpInputIcon.setVisibility(View.VISIBLE);
            resendLabel.setVisibility(View.VISIBLE);
        }
    }
}
