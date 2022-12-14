package com.colonelfund.colonelfund;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity for donating to an event.
 */
public class DonateToEventActivity extends BraintreeActivity {
    private GoogleApiClient mGoogleApiClient; // need to implement
    public EditText eventDonationTextField;
    public Button eventDonateButton;
    public TextView eventPaymentDescriptionLabel;
    public Button eventSelectPaymentButton;
    public ImageView eventPaymentIconView;

    /**
     * Sets event information.
     *
     * @param savedInstanceState for activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //In activities extending the BraintreeActivity class, setContentView needs to be the first line, in order for corresponding labels, images, and buttons to be properly manipulated by BraintreeActivity.
        //Respective labels, images, and buttons must then be initialized using findViewById
        //Finally, BraintreeActivityInitializer needs to be called
        //All of this needs to be done **BEFORE** super.onCreate is called to ensure null references aren't passed
        setContentView(R.layout.activity_donate_to_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        final Event selectedEvent =  (Event) intent.getSerializableExtra("SelectedEvent");
        MemberCollection mc = new MemberCollection(this);
        Member associatedMember = mc.get(selectedEvent.getAssociatedMember());

        eventDonationTextField = findViewById(R.id.eventDonationAmount);
        eventDonateButton = findViewById(R.id.eventDonateButton);
        eventPaymentDescriptionLabel = findViewById(R.id.eventPaymentMethodDescription);
        eventSelectPaymentButton = findViewById(R.id.eventSelectPaymentMethodButton);
        eventPaymentIconView = findViewById(R.id.eventPaymentMethodImage);
        BraintreeActivityInitializer(eventDonationTextField, eventDonateButton, eventPaymentDescriptionLabel, eventSelectPaymentButton, eventPaymentIconView, selectedEvent);
        super.onCreate(savedInstanceState);

        TextView text = findViewById(R.id.textView3);
        text.setText(selectedEvent.getTitle());
        text = findViewById(R.id.textView11);
        text.setText(selectedEvent.getEventDate());
        text = findViewById(R.id.textView9);
        text.setText(associatedMember.getFormattedFullName());
        text = findViewById(R.id.textView5);
        text.setText("$" + String.valueOf(selectedEvent.getFundGoal()));
        text = findViewById(R.id.textView7);
        text.setText("$" + String.valueOf(selectedEvent.getCurrentFunds()));
    }

    /**
     * For back button at top left of screen, pass back intent params
     * https://developer.android.com/training/basics/intents/result.html
     * @param requestCode for request.
     * @param resultCode for request.
     * @param data for request.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * inflates menu in top right.
     *
     * @param menu menu for top right.
     * @return boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Detected options menu action selected.
     *
     * @param item that was selected.
     * @return boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_item) {
            AccessToken token = AccessToken.getCurrentAccessToken();
            FirebaseAuth.getInstance().signOut();
            //check for google connection (remove once implemented globally)
            if (mGoogleApiClient != null) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
            if(token != null) {
                LoginManager.getInstance().logOut();
            }
            User.logout();
            Intent loginIntent = new Intent(DonateToEventActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Detected select payment pressed action.
     *
     * @param v a view.
     */
    public void eventSelectPaymentMethodButtonPressed(View v) {
        super.selectPaymentButtonPressed();
    }

    /**
     * Detected event donate action.
     *
     * @param v a view.
     */
    public void eventDonateButtonPressed(View v) {
        super.donateButtonPressed();
    }
}
