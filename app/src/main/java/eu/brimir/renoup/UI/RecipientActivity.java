package eu.brimir.renoup.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.brimir.renoup.Utils.FileHelper;
import eu.brimir.renoup.Utils.MailSender;
import eu.brimir.renoup.Utils.ParseConstants;
import eu.brimir.ribbit.BuildConfig;
import eu.brimir.ribbit.R;

/**
 * Created by brode on 2015-08-01.
 */
public class RecipientActivity extends AppCompatActivity implements LocationListener {


    public static final String TAG = RecipientActivity.class.getSimpleName();


    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected Uri mMediaUri;
    protected MenuItem mSendMenuItem;
    protected String mFileType;
    protected GridView mGridView;
    protected String filePath;
    protected String pickedFelkod;
    protected String pickedAddress;
    protected String getAddress;
    protected String getPostnr;
    private ProgressDialog pDialog;
    protected String routeNumber;
    protected Spinner spinner;
    protected double latitude, longitude;
    protected ArrayAdapter<CharSequence> adapter;
    private LocationManager locationManager;
    private Location location;

    private String provider;
    private List<Address> addresses;
    @Bind(R.id.sendButton)
    Button mSendButton;
    @Bind(R.id.cancelButton)
    Button mCancelButton;
    @Bind(R.id.addressField)
    EditText mAddressField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient);
        ButterKnife.bind(this);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);

        onLocationChanged(location);


        SharedPreferences prefs = this.getSharedPreferences(ParseConstants.PREF_FILE_NAME, 0);

        String savedValue = prefs.getString(ParseConstants.KEY_ROUTE, null);
        routeNumber = savedValue;
        spinner = (Spinner) findViewById(R.id.felkodSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.felkoder, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                pickedFelkod = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);


    }


    public void saveReducedImage(byte[] fileBytes) {

        File mediaFile;
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "RenoUp");
        String path = mediaStorageDir.getPath() + File.separator;

        mediaFile = new File(path + "upload.jpg");
        filePath = mediaFile.toString();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mediaFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipient, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    protected ParseObject createMessage(String felkod, String address, String postnr) {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getString(ParseConstants.KEY_FIRSTNAME));
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        message.put(ParseConstants.KEY_ADDRESS, address);
        message.put(ParseConstants.KEY_FELKOD, felkod);
        message.put(ParseConstants.KEY_ROUTE, routeNumber);
        message.put(ParseConstants.KEY_POSTNR, postnr);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

        if (fileBytes == null) {
            return null;
        } else {
            if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);

            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);


            return message;
        }

    }


    protected void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientActivity.this);
                    builder.setMessage(getString(R.string.error_sending_message))
                            .setTitle(getString(R.string.error_selecting_file_title))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });


    }


    public class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        MailSender m = new MailSender();


        public SendEmailAsyncTask(String fileName, String address, String felkod, String week, String time) {
            super();
            pDialog = new ProgressDialog(RecipientActivity.this);
            pDialog.setMessage(getString(R.string.sending));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            if (BuildConfig.DEBUG) {
                Log.v(SendEmailAsyncTask.class.getName(),
                        "SendEmailAsyncTask()");
            }
            m.setTo(ParseConstants.MAIL_RECIVIER);


            m.setSubject(felkod + " - " + address + " - Tur: " + routeNumber + " - vecka " + week);


            m.setBody("Skickades klockan: " + time);
            try {

                m.addAttachment(fileName);
            } catch (Exception e) {

                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {

        }


        @Override
        protected Boolean doInBackground(Void... params) {

            if (BuildConfig.DEBUG)
                Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
            try {
                m.send();
                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                return false;
            } catch (MessagingException e) {
                Log.e(SendEmailAsyncTask.class.getName(), m.getTo(null)
                        + "failed");
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            }

        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            Toast.makeText(RecipientActivity.this, R.string.sent, Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    @OnClick(R.id.gpsButton)
    public void getAddressButton(View view){
        getAddress(latitude,longitude);
    }

    @OnClick(R.id.cancelButton)
    public void closeSendActivity(View view) {
        finish();
    }


    @OnClick(R.id.sendButton)
    public void sendMessage(View view) {




        DateFormat dateFormat = new SimpleDateFormat("w");
        DateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String week = dateFormat.format(date);
        String time = dateFormat2.format(date);


        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
        fileBytes = FileHelper.reduceImageForUpload(fileBytes);

        pickedAddress = mAddressField.getText().toString();
        pickedAddress = pickedAddress.trim();
        pickedFelkod = pickedFelkod.trim();
        ParseObject message = createMessage(pickedFelkod, pickedAddress, getPostnr);
        if (message == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.error_selecting_file))
                    .setTitle(getString(R.string.error_selecting_file_title))
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            send(message);
            saveReducedImage(fileBytes);


            new SendEmailAsyncTask(filePath, pickedAddress, pickedFelkod, week, time).execute();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

            locationManager.requestLocationUpdates(provider, 400, 1, RecipientActivity.this);




    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(RecipientActivity.this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null) {

            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private void getAddress(double latitude, double longitude) {

        GetAddressAsynctask addressAsynctask = new GetAddressAsynctask();
        addressAsynctask.execute(latitude, longitude);


    }



    private class GetAddressAsynctask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... location) {

            double latitude = (Double) location[0];
            double longitude = (Double) location[1];
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());

            addresses = null;
            try {
                addresses = gcd.getFromLocation(latitude,
                        longitude, 1);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            if (addresses != null && addresses.size() > 0) {

                if ((addresses.get(0).getAddressLine(0) != null)) {

                    getAddress = addresses.get(0).getAddressLine(0);
                    getPostnr = addresses.get(0).getAddressLine(1);


                }


            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mAddressField.setText(getAddress);


        }

    }


}



