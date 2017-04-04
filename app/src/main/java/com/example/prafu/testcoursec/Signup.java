package com.example.prafu.testcoursec;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class Signup extends AppCompatActivity {


        private static final String TAG = "SignupActivity";


        private EditText fullName;
        private EditText userEmailId;
        private EditText passwd;
        private EditText confirmPassword;
        private Button signUpBtn;
    private static LinearLayout signupLayout;
    private static Animation shakeAnimation;
        private TextView already_user;
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthListener;
        private ProgressDialog progressDialog;
        private SharedPreferences sharedPreferences;
        private static CheckBox terms_conditions;

        @Override
        protected void onStart() {
            super.onStart();
            mAuth.addAuthStateListener(mAuthListener);
        }

        @Override
        protected void onStop() {
            super.onStop();
            if (mAuthListener != null) {
                mAuth.removeAuthStateListener(mAuthListener);
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);
            shakeAnimation = AnimationUtils.loadAnimation(Signup.this, R.anim.shake);
            fullName = (EditText) findViewById(R.id.fullName);
            signupLayout=(LinearLayout)findViewById(R.id.signuplayout);
            userEmailId = (EditText) findViewById(R.id.userEmailId);
            passwd = (EditText) findViewById(R.id.password);
            signUpBtn = (Button) findViewById(R.id.signUpBtn);
            already_user = (TextView) findViewById(R.id.already_user);
            confirmPassword = (EditText) findViewById(R.id.confirmPassword);
            terms_conditions = (CheckBox) findViewById(R.id.terms_conditions);

            sharedPreferences = getSharedPreferences("appStatus", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("appRanOnce", true);
            editor.commit();



            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
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

            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard();
                    signup();
                }
            });

            already_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Finish the registration screen and return to the Login activity
                    startActivity(new Intent(Signup.this, login.class));
                    finish();
                }
            });

            XmlResourceParser xrp = getResources().getXml(R.drawable.textviewselector);
            try {
                ColorStateList csl = ColorStateList.createFromXml(getResources(),
                        xrp);

                already_user.setTextColor(csl);
                terms_conditions.setTextColor(csl);
            } catch (Exception e) {
            }
        }


        public void signup() {
            Log.d(TAG, "Signup");

            if (!validate()) {
                signUpBtn.setEnabled(true);
                signupLayout.startAnimation(shakeAnimation);
                Toast.makeText(this, "Recheck your entries", Toast.LENGTH_SHORT).show();
                return;
            }

            signUpBtn.setEnabled(false);

            progressDialog = new ProgressDialog(Signup.this);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();

            String name = fullName.getText().toString();
            final String email = userEmailId.getText().toString();
            final String password = passwd.getText().toString();


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            authResult.getUser().sendEmailVerification();
                            onSignupSuccess();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signUpBtn.setEnabled(true);
                    progressDialog.dismiss();
                    new AlertDialog.Builder(Signup.this)
                            .setTitle("Error")
                            .setMessage("Sign up failed. Error : \n" + e.getMessage())
                            .setPositiveButton("Okay", null)
                            .show();
                }
            });

        }


        public void onSignupSuccess() {
            signUpBtn.setEnabled(true);
            progressDialog.dismiss();
            final FirebaseUser user = mAuth.getCurrentUser();
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName.getText().toString())
                    .build();
            user.updateProfile(profileUpdate);
            final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userRef.child("Name").setValue(fullName.getText().toString());
                    userRef.child("rootlevel").setValue("20");
                    userRef.child("e-mail").setValue(userEmailId.getText().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            new AlertDialog.Builder(Signup.this)
                    .setTitle("Sign up successful")
                    .setMessage("Dear user, in order to use the services, you need to verify your email. A verification email has been sent.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAuth.signOut();
                            startActivity(new Intent(Signup.this, login.class));
                            finish();
                        }
                    })
                    .show();

        }

        private void hideKeyboard() {
            View view = getCurrentFocus();
            if (view != null) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        public boolean validate() {
            boolean valid = true;

            String name = fullName.getText().toString();
            String email = userEmailId.getText().toString();
            String password = passwd.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            if (name.isEmpty() || name.length() < 3) {
                fullName.setError("at least 3 characters");
                valid = false;
            } else {
                fullName.setError(null);
            }

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                userEmailId.setError("enter a valid email address");
                valid = false;
            } else {
                userEmailId.setError(null);
            }

            if (password.isEmpty() || password.length() <= 6 || password.length() > 10) {
                passwd.setError("between 6 and 10 alphanumeric characters");
                valid = false;
            } else {
                passwd.setError(null);
            }

            if (!confirmPass.equals(password)) {
                confirmPassword.setError("Both passwords do not match");
                valid = false;
            } else {
                confirmPassword.setError(null);
            }

            return valid;
        }

        @Override
        public void onBackPressed() {

            new AlertDialog.Builder(Signup.this)
                    .setMessage("Do you want to exit the app?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            moveTaskToBack(true);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }
