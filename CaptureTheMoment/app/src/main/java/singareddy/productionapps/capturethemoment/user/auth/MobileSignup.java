package singareddy.productionapps.capturethemoment.user.auth;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import singareddy.productionapps.capturethemoment.HomeActivity;
import singareddy.productionapps.capturethemoment.R;

public class MobileSignup extends Fragment implements AuthListener.Mobile, View.OnClickListener {
    private static String TAG = "MobileSignup";

    View fragmentView;
    View loginButton;
    EditText mobileNumber, otpCode;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.activity_mobile_login, container, false);
        initialiseUI();
        initialiseViewModel();
        return fragmentView;
    }

    private void initialiseUI() {
        mobileNumber = fragmentView.findViewById(R.id.activity_mobile_login_mobile);
        otpCode = fragmentView.findViewById(R.id.activity_mobile_login_otp);
        otpCode.setVisibility(View.GONE);
        loginButton = fragmentView.findViewById(R.id.email_signup_bt_continue);
        loginButton.setOnClickListener(this);
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(getActivity());
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        authViewModel.setMobileAuthListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == loginButton) {
            loginButton.setEnabled(false);
            authViewModel.authorizePhoneCredentials(
                    mobileNumber.getText().toString().trim(),
                    otpCode.getText().toString().trim());
        }
    }

    @Override
    public void onMobileAuthenticationSuccess() {
        Toast.makeText(getContext(), "Login Success", Toast.LENGTH_SHORT).show();
        // On successful login, erase all the data.
        authViewModel.eraseLocalData();
        Intent homeIntent = new Intent(getContext(), HomeActivity.class);
        startActivity(homeIntent);
        // Once data is erased, download this user's data
        authViewModel.setupInitialData();
        getActivity().finish();
    }

    @Override
    public void onMobileAuthenticationFailure(String failureCode) {
        loginButton.setEnabled(true);
        if (failureCode.equals("EMPTY_FIELDS")) {
            Toast.makeText(getContext(), "Mobile number is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), "Login Failure", Toast.LENGTH_SHORT).show();
        resetAllViews();
    }

    @Override
    public void onOtpSent() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_otp_timer, null, false);
        TextView timerView = dialogView.findViewById(R.id.dialog_otp_timer_tv_time);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        new AsyncTask<Void, Integer, Void>() {
            Integer timer = 10;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                timerView.setText(timer.toString());
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    while (timer > 0) {
                        Thread.sleep(1000);
                        timer--;
                        publishProgress(timer);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                timerView.setText(values[0].toString());
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getContext(), "Auto retrieval failed. Enter manual OTP", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }.execute();
    }

    @Override
    public void onOtpRetrievalFailed() {
        // Ask for manual OTP here
        otpCode.setVisibility(View.VISIBLE);
        loginButton.setEnabled(true);
    }

    private void resetAllViews () {
        mobileNumber.setText("");
    }
}
