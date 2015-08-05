package eu.brimir.renoup.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.brimir.renoup.Utils.ParseConstants;
import eu.brimir.ribbit.R;

public class SettingsActivity extends AppCompatActivity {

    protected Spinner spinner;
    protected ArrayAdapter<CharSequence> adapter;
    protected String pickedTur;
    @Bind(R.id.saveSettingsButton)
    Button mSaveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);


        SharedPreferences prefs = this.getSharedPreferences(ParseConstants.PREF_FILE_NAME, 0);
        String savedValue = prefs.getString(ParseConstants.KEY_ROUTE, null);










        spinner = (Spinner) findViewById(R.id.felkodSpinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.turer, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if(savedValue == null){
            spinner.setSelection(0);
        }else if (savedValue.equals("101")){
            spinner.setSelection(1);
        }else if (savedValue.equals("102")){
            spinner.setSelection(2);
        }else if (savedValue.equals("103")){
            spinner.setSelection(3);
        }else if (savedValue.equals("104")){
            spinner.setSelection(4);
        }else if (savedValue.equals("105")){
            spinner.setSelection(5);
        }else if (savedValue.equals("106")){
            spinner.setSelection(6);
        }else if (savedValue.equals("107")){
            spinner.setSelection(7);
        }else if (savedValue.equals("108")){
            spinner.setSelection(8);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                pickedTur = parent.getItemAtPosition(position).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



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


    @OnClick(R.id.saveSettingsButton)
    public void saveSettings(View view){

        if(pickedTur.equals("Välj tur")){
            Toast.makeText(this, R.string.must_pick_route_toast, Toast.LENGTH_LONG).show();
        }else{
            SharedPreferences pref = this.getSharedPreferences(ParseConstants.PREF_FILE_NAME, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(ParseConstants.KEY_ROUTE,pickedTur);

            editor.apply();
            finish();
        }


    }
}
