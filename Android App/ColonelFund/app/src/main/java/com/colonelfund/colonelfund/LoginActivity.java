package com.colonelfund.colonelfund;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via userName/password.
 */
public class LoginActivity extends AppCompatActivity {
    private final String URL_FOR_LOGIN = "https://wesll.com/colonelfund/login.php";
    // UI references.
    private EditText txtLoginEmail;
    private EditText txtPassword;
    private View mProgressView;
    private View mLoginFormView;
    private LoginButton loginButton;
    private SignInButton mGoogleButton;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private LoginButton btnFacebookLogin;
    private Button btnLogin, btnRegister;
    AppSingleton appContext;
    // Current User Properties
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String profilePicURL;
    private String facebookID;
    private String googleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appContext = new AppSingleton(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        AppEventsLogger.activateApp(this);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        AppEventsLogger.activateApp(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        //logger.logPurchase(BigDecimal.valueOf(4.32), Currency.getInstance("USD"));
        // Set up the login form.
        txtLoginEmail = (EditText) findViewById(R.id.txtLoginEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        //fb login
        btnFacebookLogin = (LoginButton) findViewById(R.id.btnFacebookLogin);
        //google login
        mGoogleButton = (SignInButton) findViewById(R.id.sign_in_button);
        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        txtLoginEmail = (EditText) findViewById(R.id.txtLoginEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
//        btnRegister = (Button) findViewById(R.id.btnRegister); // registrationEnable
        btnFacebookLogin = (LoginButton) findViewById(R.id.btnFacebookLogin);
        //Button fbLogin = (Button) findViewById(R.id.login_button);

        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        final MemberCollection mcf = new MemberCollection(getApplicationContext());
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loginUser(txtLoginEmail.getText().toString(), txtPassword.getText().toString());
            }
        });

        // TODO: 1/15/2018 Remove before final submission 

        // registrationEnable
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onRegister(view);
//            }
//        });

        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest req = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            Log.d("login", "Facebook object:" + object.toString());
                            if (object.getString("first_name") != null) {
                                firstName = object.getString("first_name");
                            }
                            if (object.getString("last_name") != null) {
                                lastName = object.getString("last_name");
                            }
                            if (object.getString("email") != null) {
                                emailAddress = object.getString("email");
                            }
                            if (object.getString("picture") != null) {
                                profilePicURL = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            }
                            if (object.getString("id") != null) {
                                facebookID = object.getString("id");
                            }
                            Member member = new Member("", firstName, lastName, emailAddress, "", "", "", "", "", "");
                            member.setProfilePicURL(profilePicURL);
                            member.setFacebookID(facebookID);
                            User.setCurrentUser(member);
                            Toast.makeText(LoginActivity.this, User.currentUser.getFormattedFullName() + " signed in successfully", Toast.LENGTH_LONG).show();
                            startActivity(MainIntent);

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, picture");
                req.setParameters(parameters);
                req.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(LoginActivity.this, "Wrong username or password", Toast.LENGTH_LONG).show();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.

        appContext.cancelAll();
    }

    private void loginUser(final String emailAddress, final String password) {
        // Tag used to cancel the request
        String cancel_req_tag = "login";
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_FOR_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Member member = new Member(jObj.getJSONObject("user"));
                        User.setCurrentUser(member);

                        // Launch User activity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), User.currentUser.getFormattedFullName() + " signed in successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("emailAddress", emailAddress);
                params.put("password", password);
                return params;
            }

        };
        // Adding request to request queue
        strReq.setTag(getApplicationContext());
        appContext.addToRequestQueue(strReq, cancel_req_tag);
    }

    /**
     * Launches "Create new Account" page.
     *
     * @param
     */
    // TODO: 1/15/2018 remove before final submission
    // registrationEnable

//    public void onRegister(View view) {
//        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
//        startActivity(i);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount googleAccount = completedTask.getResult(ApiException.class);
            Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
            if (googleAccount.getGivenName() != null) {
                firstName = googleAccount.getGivenName();
            }
            if (googleAccount.getFamilyName() != null) {
                lastName = googleAccount.getFamilyName();
            }
            if (googleAccount.getEmail() != null) {
                emailAddress = googleAccount.getEmail();
            }
            if (googleAccount.getPhotoUrl() != null) {
                profilePicURL = googleAccount.getPhotoUrl().toString();
            }
            if (googleAccount.getId() != null) {
                googleID = googleAccount.getId();
            }
            Member member = new Member("", firstName, lastName, emailAddress, "", "", "", "", "", "");
            member.setProfilePicURL(profilePicURL);
            member.setGoogleID(googleID);
            User.setCurrentUser(member);
            Toast.makeText(LoginActivity.this, User.currentUser.getFormattedFullName() + " signed in successfully", Toast.LENGTH_LONG).show();
            startActivity(MainIntent);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }
}

