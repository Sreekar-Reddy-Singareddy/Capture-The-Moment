package singareddy.productionapps.capturethemoment.user.auth;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OtpRetriveTask extends AsyncTask<Void, Void, Void> {
    private static String TAG = "OtpRetriveTask";
    Integer timer = 10;
    TextView otpTimer, otpTimerLabel;
    Button loginButton;
    ProgressBar loader;
    EditText otpInput;

    public OtpRetriveTask(TextView otpTimer, TextView otpTimerLabel, Button loginButton, ProgressBar loader, EditText otpInput) {
        Log.i(TAG, "OtpRetriveTask: Timer label: "+otpTimer);
        this.otpTimer = otpTimer;
        this.otpTimerLabel = otpTimerLabel;
        this.loginButton = loginButton;
        this.loader = loader;
        this.otpInput = otpInput;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.otpTimer.setText(timer.toString()+" Seconds");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            while (timer > 0) {
                Thread.sleep(1000);
                timer--;
                publishProgress();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.i(TAG, "onProgressUpdate: Timer: "+timer);
        this.otpTimer.setText(timer.toString()+" Seconds");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        loginButton.setVisibility(View.VISIBLE);
        loader.setVisibility(View.INVISIBLE);
        otpInput.setVisibility(View.VISIBLE);
        otpTimerLabel.setVisibility(View.INVISIBLE);
        otpTimer.setVisibility(View.INVISIBLE);
        timer = 10;
    }
}
