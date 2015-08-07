package eu.brimir.renoup.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.brimir.renoup.Utils.ParseConstants;
import eu.brimir.ribbit.R;

/**
 * Created by brode on 2015-08-05.
 */
public class ShowPopUp extends Activity implements View.OnClickListener {
    Button ok;
    Button cancel;
    protected String message, title;
    boolean click = true;
    @Bind(R.id.messageTextView)
    TextView mMessage;
    @Bind(R.id.titleTextView)
    TextView mTitle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Meddelande!");
        setContentView(R.layout.popupdialog);
        ButterKnife.bind(this);



        message = getIntent().getExtras().getString(ParseConstants.KEY_MESSAGE);
        title = getIntent().getExtras().getString(ParseConstants.KEY_SENDER_NAME);
        mMessage.setText(message);
        mTitle.setText(getString(R.string.message_from) + title);

        ok = (Button)findViewById(R.id.cancelDialogButton);
        ok.setOnClickListener(this);

    }
    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        finish();
    }
}