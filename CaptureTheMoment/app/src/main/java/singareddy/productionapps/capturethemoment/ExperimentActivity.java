package singareddy.productionapps.capturethemoment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import singareddy.productionapps.capturethemoment.book.getbooks.GetBooksViewModel;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.user.auth.AuthListener;
import singareddy.productionapps.capturethemoment.models.User;

public class ExperimentActivity extends AppCompatActivity implements AuthListener.Mobile, AuthListener.EmailSignup, AuthListener.EmailLogin {
    private static String TAG = "ExperimentActivity";

    Button performAction;
    EditText input;
    GetBooksViewModel getBooksViewModel;

    // Firebase objects
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    static String USERS_NODE = "users";
    static String BOOKS_NODE = "books";
    String mobileCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        performAction = findViewById(R.id.action);
        input = findViewById(R.id.input); input.setText("5");

        SharedPreferences userProfile = getApplication().getSharedPreferences("USER_PROFILE", Context.MODE_PRIVATE);
        Log.i(TAG, "onCreate: Name: "+userProfile.getString("emailId","N/A"));
    }

    public void performAction(View view) {
        // Each code value performs a different task
        Log.i(TAG, "performAction: Code: " + input.getText().toString());
        int code = Integer.parseInt(input.getText().toString());

        // Email authentication - In both register and login, must check if the user exists or not
        if (code == 0) {
            // Logout of the application

        }
        else if (code == 1) {
            // Register user via email and password
            User user = new User("Gopi Krishna", 9441079575l, 51, "Male", "gopikrishna@vicat.com");
//            authenticationViewModel.registerUserWithEmailCredentials(user, "Gopi@123", "");
        }
        else if (code == 2) {
            // Login user via email and password
//            authenticationViewModel.loginUserWithEmailCredentials("sreekarreddy430@gmail.com", "Sree@123");
        }
        // Mobile authentication
        else if (code == 3) {
            // Authenticate user by the mobile number
//            authenticationViewModel.authorizePhoneCredentials("9629781945");
        }
        else if (code == 4) {

        }
        else if (code == 5) {
            bookExperiment();
        }
    }

    private void bookExperiment() {
        firebaseDatabase.getReference().child("books")
                .orderByChild("owner").equalTo("owner_one")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.i(TAG, "onDataChange: "+dataSnapshot.getValue().toString());
                        HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            ObjectMapper mapper = new ObjectMapper();
                            Book b = mapper.convertValue(entry.getValue(), Book.class);
                            if (b.getName().toLowerCase().trim().equals("first book")) {
                                Toast.makeText(ExperimentActivity.this, "Book exists!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG, "onCancelled: Message: "+databaseError.getMessage());
                        Log.i(TAG, "onCancelled: Details: "+databaseError.getDetails());
                    }
                });
    }

    // Authentication Listener Interface Methods
    @Override
    public void onEmailUserRegisterSuccess(String email) {
        Log.i(TAG, "onEmailUserRegisterSuccess: "+email);
        Toast.makeText(this, "Registration Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmailUserRegisterFailure(String email, String failureCode) {
        Log.i(TAG, "onEmailUserRegisterFailure: "+email);
        Toast.makeText(this, "Registration Failure", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmailUserLoginFailure(String failureCode) {
        Log.i(TAG, "onEmailUserLoginFailure: *");
        Toast.makeText(this, "Login Failure", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmailUserLoginSuccess() {
        Log.i(TAG, "onEmailUserLoginSuccess: *");
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
        // User logged in
        // Meaning, the book data has to be loaded
        getBooksViewModel = ViewModelProviders.of(this).get(GetBooksViewModel.class);
    }

    @Override
    public void onMobileAuthenticationSuccess() {
        Log.i(TAG, "onMobileAuthenticationSuccess: *");
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMobileAuthenticationFailure(String failureCode) {
        Log.i(TAG, "onMobileAuthenticationFailure: *");
        Toast.makeText(this, "Login Failure", Toast.LENGTH_SHORT).show();
    }
}
