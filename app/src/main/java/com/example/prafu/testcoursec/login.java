package com.example.prafu.testcoursec;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

//import android.support.design.widget.TextInputLayout;

//import static com.example.prafu.testcoursec.R.id.show_hide_password;
//import static com.example.prafu.testcoursec.R.string.signUp;

public class login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN=0;
    private FirebaseAuth mAuth;
    Uri personPhoto;
    private final Context mContext = this;
    String personPhoneURL;
    private FirebaseAuth.AuthStateListener mListener;
String personId;
    private static CheckBox show_hide_pwd;
    private static Animation shakeAnimation;
    private static LinearLayout loginLayout;
    private static RelativeLayout relativeloginLayout;
    private CallbackManager callbackManager;
    EditText login_emailid, login_password;
    Button loginBtn;
    ProgressDialog progressDialog;
    ImageButton fb_login, google_circle;
    TextView createAccount;
    TextView forgot_password;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mListener != null) {
            mAuth.removeAuthStateListener(mListener);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        forgot_password = (TextView) findViewById(R.id.forgot_password);
        login_emailid = (EditText) findViewById(R.id.login_emailid);
        login_password = (EditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        createAccount = (TextView) findViewById(R.id.createAccount);
        show_hide_pwd = (CheckBox) findViewById(R.id.show_hide_pwd);
        shakeAnimation = AnimationUtils.loadAnimation(login.this, R.anim.shake);
        relativeloginLayout = (RelativeLayout) findViewById(R.id.relative_login_layout);
        fb_login=(ImageButton)findViewById(R.id.fb_login);
        google_circle=(ImageButton)findViewById(R.id.google_login);
        mAuth = FirebaseAuth.getInstance();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.prafu.testcoursec",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        //FaceBook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        setListeners();
        XmlResourceParser xrp = getResources().getXml(R.drawable.textviewselector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            forgot_password.setTextColor(csl);
            show_hide_pwd.setTextColor(csl);
            createAccount.setTextColor(csl);
        } catch (Exception e) {
        }
        mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = login_emailid.getText().toString();
                String password = login_password.getText().toString();
                if (email.equals("") || email.length() == 0
                        || password.equals("") || password.length() == 0) {
                    relativeloginLayout.startAnimation(shakeAnimation);
                    Toast.makeText(login.this, "Enter Both Credentials", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    hideKeyboard();
                    login();
                }
            }
        });
fb_login.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View v) {
        onFacebookLogInClicked();
    }
});
        createAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);

            }
        });
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, ResetPassword.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        });
        google_circle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signInwithGoogle();
            }
        });

    }
    protected void signInwithGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()

                .build();


         mGoogleApiClient = new GoogleApiClient.Builder(login.this)
                .enableAutoManage(this,new GoogleApiClient.OnConnectionFailedListener(){

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                //TO USE
                final String personName = acct.getDisplayName();
                final String personEmail = acct.getEmail();
                personId = acct.getId();
                personPhoto = acct.getPhotoUrl();
                personPhoneURL = acct.getPhotoUrl().toString();
                //                User user = new User();
                //                user.setUsername(personName);
                //                user.setEmail(personEmail);
//                user.setPersonId(personId);
//                user.setPersonProfileUrl(personPhoneURL);
//                user.setSignedInWithGoogle(true);

                // updateFirebaseData(user,personEmail);
                final FirebaseUser user = mAuth.getCurrentUser();

                final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userRef.child("Name").setValue(personName.toString());
                        userRef.child("rootlevel").setValue("20");
                        userRef.child("profilepic").setValue(personPhoneURL);
                        userRef.child("e-mail").setValue(personEmail.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//                String userData = gson.toJson(user);
//                      EPreferenceManager.getSingleton().setUserdata(getActivity(),userData);
                firebaseAuthWithGoogle(acct);
            } else {
                //callbackManager.onActivityResult(requestCode, resultCode, data);

                Toast.makeText(login.this,"There was a trouble signing in-Please try again",Toast.LENGTH_SHORT).show();;
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(login.this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(login.this, "Authentication pass.",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(login.this, MainActivity.class);

                            startActivity(intent);
                            finish();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
    private void setListeners() {
        // loginButton.setOnClickListener(this);
        //forgot_password.setOnClickListener(login.this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_pwd
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_pwd.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            login_password.setInputType(InputType.TYPE_CLASS_TEXT);
                            login_password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_pwd.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            login_password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            login_password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }
    //FaceBook
    public void onFacebookLogInClicked( ){
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        this,
                        Arrays.asList("public_profile", "user_friends", "email")
                );
    }

    //FaceBook
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        progressDialog = new ProgressDialog(login.this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(login.this, "Authentication pass.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        // [START_EXCLUDE]
                        progressDialog.dismiss();
                        // [END_EXCLUDE]
                    }
                });
    }

    public void login() {
        Log.d(TAG, "Login");
        progressDialog = new ProgressDialog(login.this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginBtn.setEnabled(false);


        final String email = login_emailid.getText().toString();
        final String password = login_password.getText().toString();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(final AuthResult authResult) {
                        if (authResult.getUser().isEmailVerified()) {
                            onLoginSuccess();
                        } else {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(login.this)
                                    .setTitle("Not verified")
                                    .setMessage("Please verify your email address.\n Send verification email again?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            authResult.getUser().sendEmailVerification();
                                            mAuth.signOut();
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mAuth.signOut();
                                }
                            })
                                    .show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        onLoginFailed();
                        new AlertDialog.Builder(login.this)
                                .setTitle("Login failed")
                                .setMessage("There was an error while trying to log-in : \n" + e.getMessage())
                                .setNegativeButton("Okay", null)
                                .show();
                    }
                });

    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        progressDialog.dismiss();
        loginBtn.setEnabled(true);
        if (mAuth.getCurrentUser().getPhotoUrl() == null)
            startActivity(new Intent(login.this, CompleteProfile.class));
        else
            startActivity(new Intent(login.this, MainActivity.class));
        finish();
    }

    public void onLoginFailed() {
        progressDialog.dismiss();
        loginBtn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = login_emailid.getText().toString();
        String password = login_password.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            login_emailid.setError("enter a valid email address");
            valid = false;
        } else {
            login_emailid.setError(null);

        }
            if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
                //login_password.setError("between 4 and 10 alphanumeric characters");
                login_password.setError("between 4 and 10 alphanumeric characters");
                valid = false;
            } else {
                login_password.setError(null);
            }

            return valid;
        }

    }
