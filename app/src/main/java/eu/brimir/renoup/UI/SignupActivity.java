package eu.brimir.renoup.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import eu.brimir.ribbit.R;

public class SignupActivity extends AppCompatActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mFirstName;
    protected EditText mLastName;
    protected EditText mEmail;
    protected Button mSignUpButton;
    protected Button mCloseButton;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_signup);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mUsername = (EditText) findViewById(R.id.usernameField);
        mFirstName = (EditText) findViewById(R.id.firstNameField);
        mLastName = (EditText) findViewById(R.id.lastNameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mEmail = (EditText) findViewById(R.id.emailField);
        mSignUpButton = (Button) findViewById(R.id.signupButton);
        mCloseButton = (Button) findViewById(R.id.cancelSignUpButton);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(SignupActivity.this);
                pDialog.setMessage(getString(R.string.waiting_dialog));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String firstName = mFirstName.getText().toString();
                String lastName = mLastName.getText().toString();
                String email = mEmail.getText().toString();
                username = username.trim();
                password = password.trim();
                firstName = firstName.trim();
                lastName = lastName.trim();
                email = email.trim();
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    pDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();


                } else {

                    final ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.put("firstName", firstName);
                    newUser.put("lastName", lastName);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false);
                            if (e == null) {
                                pDialog.dismiss();
                                try {
                                    if (newUser.fetch().getBoolean("accepted")) {
                                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(SignupActivity.this, "Du är ännu inte godkänd för att använda appen vänta på att Patrik ska aktivera dig", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                pDialog.dismiss();
                                setProgressBarIndeterminateVisibility(true);
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();


                            }
                        }
                    });


                }

            }
        });

    }


}
