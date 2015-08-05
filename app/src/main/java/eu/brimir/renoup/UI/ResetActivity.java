package eu.brimir.renoup.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import eu.brimir.ribbit.R;

public class ResetActivity extends AppCompatActivity {
    private EditText mEmail;
    private Button mSend;
    private Button mCancel;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mEmail = (EditText) findViewById(R.id.forgotPasswordEditText);
        mSend = (Button) findViewById(R.id.resetPasswordButton);
        mCancel = (Button) findViewById(R.id.cancelSignUpButton);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(ResetActivity.this);
                pDialog.setMessage(getString(R.string.waiting_dialog));
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
                String email = mEmail.getText().toString();

                ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                          pDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(ResetActivity.this);
                            builder.setTitle(R.string.done)
                                    .setMessage(R.string.resetpassword_dialog)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            // Something went wrong. Look at the ParseException to see what's up.
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
