package eu.brimir.renoup.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.brimir.renoup.Utils.MailSender;
import eu.brimir.renoup.Utils.ParseConstants;
import eu.brimir.ribbit.BuildConfig;
import eu.brimir.ribbit.R;

public class ViewImageActivity extends AppCompatActivity {

    protected String postnr, address;
    private ProgressDialog pDialog;
    protected File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ButterKnife.bind(this);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        address = getIntent().getStringExtra(ParseConstants.KEY_ADDRESS);
        postnr = getIntent().getStringExtra(ParseConstants.KEY_POSTNR);

        Uri imageUri = getIntent().getData();
        Picasso.with(this).load(imageUri.toString()).into(imageView);

        file = new File(imageUri.getPath());

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


    @OnClick(R.id.mapButton)
    public void viewMap(View view) {
        String uri = "geo:0,0?q=" + address + "+"+postnr + "&z=14";

        startActivity(new Intent(
                android.content.Intent.ACTION_VIEW, Uri
                .parse(uri)));


    }


}
