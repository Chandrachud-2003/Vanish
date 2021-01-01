package com.chandrachud.vanish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.chandrachud.vanish.fragments.BottomSheetProfileFragment;
import com.chandrachud.vanish.helperClasses.CreateFirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pd.chocobar.ChocoBar;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import in.goodiebag.carouselpicker.CarouselPicker;

public class OtpActivity extends AppCompatActivity implements CreateFirebaseUser.signUpFinishedListener, BottomSheetProfileFragment.ButtonClickListener {

    private ImageButton backButton;
    private TextView otpText;
    private TextView otpNumberText;
    private ImageView lockImage;
    private EditText lockEditText;
    private ImageView otpImage;
    private LottieAnimationView lockAnimation;
    private Button continueButton;
    private String mVerificationId;
    private String prevActivity;
    private String mobileNumber;
    private String profileName;
    private TextView infoText;

    private String onlyNum;
    private String onlyCcc;


    private ArrayList<CarouselPicker.PickerItem> mPickerItems;
    private int[] profilePictures;

    private BottomSheetProfileFragment mProfileFragment;
    private int profilePicPosition;

    private AuthCredential mAuthCredential;
    private PhoneAuthCredential mPhoneAuthCredential;

    private boolean phoneAuthFinished;

    private FirebaseAuth mFirebaseAuth;

    private AlertDialog verifyOtpDialog;
    private AlertDialog continueDialog;

    private boolean phoneVerifiedDone;
    private boolean phoneVerifiedFailed;
    private FirebaseAuthSettings firebaseAuthSettings;

    private static final int MY_PERMISSIONS_SMS = 1001;

    private SmsVerifyCatcher smsVerifyCatcher;

    private boolean codeManuallyVerified;
    private String manuallyReadCode;

    private boolean codeAutoVerified;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(OtpActivity.this,R.color.white));// set status background white

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }



        Intent intent = getIntent();
        mobileNumber = intent.getStringExtra(Constants.phone_Intent);
        prevActivity = intent.getStringExtra(Constants.activity_Intent);
        mAuthCredential = intent.getParcelableExtra(Constants.auth_intent);

        if (prevActivity.equals(Constants.signup_Activity)) {
            profileName = intent.getStringExtra(Constants.nameSignup);
            onlyNum = intent.getStringExtra(Constants.onlyNum_intent);
            onlyCcc = intent.getStringExtra(Constants.onlyCcc_intent);
        }

        findViewsById();
        setOnClickListeners();

        checkForSmsPermission();




    }

    private void findViewsById()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int screen_width = displayMetrics.widthPixels;

        otpImage = findViewById(R.id.otpImage);
        lockAnimation = findViewById(R.id.lockAnimation);

        otpImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    otpImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    otpImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }



                otpImage.getLayoutParams().width = screen_width;
                int newHeight = (int) (screen_width*0.8);

                otpImage.getLayoutParams().height = newHeight;



            }
        });

        lockAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    lockAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    lockAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }



                int newDimensions = (int) (screen_width * 0.1);
                lockAnimation.getLayoutParams().width = newDimensions;


                lockAnimation.getLayoutParams().height = newDimensions;

                setLoadingAnimation(true, true);


            }
        });

        backButton = findViewById(R.id.backButton);
        otpText = findViewById(R.id.otpText);
        otpNumberText = findViewById(R.id.otpNumberText);
        lockImage = findViewById(R.id.lockImage);
        lockEditText = findViewById(R.id.lockEditText);
        lockEditText = findViewById(R.id.lockEditText);

        continueButton = findViewById(R.id.continueButton);
        infoText = findViewById(R.id.otpInfoText);
        infoText.setVisibility(View.INVISIBLE);


        if(prevActivity.equals(Constants.signup_Activity))
        {
            profilePicPosition =0;
            profilePictures = Constants.profilePics_medium;

            mPickerItems = new ArrayList<>();
            for(int i=0;i<profilePictures.length;i++)
            {
                mPickerItems.add(new CarouselPicker.DrawableItem(profilePictures[i]));
            }

            mProfileFragment = new BottomSheetProfileFragment(screen_width, OtpActivity.this, mPickerItems);

        }

        mFirebaseAuth = FirebaseAuth.getInstance();

        verifyOtpDialog = new SpotsDialog.Builder().setContext(OtpActivity.this).build();
        verifyOtpDialog.setTitle("Auto-Verifying Your OTP");
        verifyOtpDialog.setMessage("Please Wait...");

        continueDialog = new SpotsDialog.Builder().setContext(OtpActivity.this).build();
        continueDialog.setMessage("Please Wait...");

        phoneVerifiedDone = false;

        firebaseAuthSettings = mFirebaseAuth.getFirebaseAuthSettings();
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber("+917022458655", "123456");

        codeAutoVerified = false;




    }

    private void setOnClickListeners()
    {
        PushDownAnim.setPushDownAnimTo(backButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(OtpActivity.this);
                        builder.setTitle("OTP Cancellation");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (prevActivity.equals(Constants.login_Activity))
                                {
                                    changeActivity(LoginActivity.class);
                                }
                                else if (prevActivity.equals(Constants.signup_Activity))
                                {
                                    changeActivity(SignupActivity.class);
                                }

                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        builder.setMessage("Your OTP Request will no longer be valid, are you sure you want to go back?");

                        AlertDialog alertDialog = builder.create();

                        alertDialog.show();


                    }
                });

        PushDownAnim.setPushDownAnimTo(continueButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    if (phoneVerifiedDone)
                    {

                        if (prevActivity.equals(Constants.signup_Activity))
                        {
                            openModalBottomDialog();

                        }
                        else if (prevActivity.equals(Constants.login_Activity))
                        {
                            completeGoogleSignIn();
                        }

                    }
                    else if (phoneVerifiedFailed)
                    {
                        setUpChocoBar("Unable to verify your number, please try again", R.color.chocoRed, R.color.white, R.drawable.error_snackbar);
                    }
                    else if (!phoneVerifiedDone)
                    {
                        setUpChocoBar("Please wait for the Auto-OTP Verification to be completed", R.color.loginMain, R.color.white, R.drawable.tick_snackbar);
                    }

                    }
                });
    }

    private void sendVerificationCode(String mobile) {
        verifyOtpDialog.show();

        if (mobile.equals("+97022458655")) {

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+917022458655",
                    60L,
                    TimeUnit.SECONDS,
                    this, /* activity */
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential credential) {
                            // Instant verification is applied and a credential is directly returned.
                            // ...

                            mPhoneAuthCredential = credential;
                            verifyOtpDialog.dismiss();
                            phoneVerifiedDone = true;

                            //sometime the code is not detected automatically
                            //in this case the code will be null
                            //so user has to manually enter the code

                            setCheckAnimation(true, true);
                            setOtpText(true);
                            lockEditText.setText("123456");
                            //verifying the code


                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {

                        }

                        // ...
                    });
        }
       else {
              PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);

            Log.d("TAG", "sendVerificationCode: otp sent");

        }


    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            Log.d("TAG", "sendVerificationCode: entered");


            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            verifyOtpDialog.dismiss();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                codeAutoVerified = true;
                setCheckAnimation(true, true);
                setOtpText(true);
                if (codeManuallyVerified && manuallyReadCode.length()>0)
                {
                    if (!(code.equals(manuallyReadCode)))
                    {
                        lockEditText.setText(code);
                        verifyVerificationCode(code);
                        smsVerifyCatcher.onStop();
                    }
                }

                else {
                    lockEditText.setText(code);
                    verifyVerificationCode(code);
                    smsVerifyCatcher.onStop();

                }

                //verifying the code


            }

            else {
                Toast.makeText(getBaseContext(), "Unable to automatically verify code", Toast.LENGTH_LONG).show();
                setOtpText(false);
                setCancelAnimation(true, true);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d("TAG", "onVerificationFailed: entered");
            verifyOtpDialog.dismiss();
            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            setOtpText(false);
            setCancelAnimation(true, true);
            phoneVerifiedDone = true;
            phoneVerifiedFailed = true;
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            Log.d("TAG", "onCodeSent: entered");
            verifyOtpDialog.dismiss();
            //storing the verification id that is sent to the user
            mVerificationId = s;
            setUpMessageListener();

        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        mPhoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, code);
        phoneVerifiedDone = true;

        //signing the user
       // signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential() {
        if (mPhoneAuthCredential != null && mFirebaseAuth.getCurrentUser()!=null) {

            mFirebaseAuth.getCurrentUser().linkWithCredential(mPhoneAuthCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "linkWithCredential:success");

                                if (prevActivity.equals(Constants.login_Activity)) {
                                    Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else if (prevActivity.equals(Constants.signup_Activity))
                                {
                                    String uid = mFirebaseAuth.getCurrentUser().getUid();
                                    String email = mFirebaseAuth.getCurrentUser().getEmail();
                                    CreateFirebaseUser createFirebaseUser = new CreateFirebaseUser(OtpActivity.this, profileName, mobileNumber, uid, onlyCcc, onlyNum, email, profilePicPosition);
                                    createFirebaseUser.writeToDatabase();

                                }



                            } else {
                                Log.w("TAG", "linkWithCredential:failure", task.getException());

                                continueDialog.dismiss();

                                final AlertDialog.Builder builder = new AlertDialog.Builder(OtpActivity.this);
                                builder.setMessage("It seems like your Phone Number and Email did not link to the same account, please Try Again...");
                                builder.setTitle("2-Step Authentication Failed");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Intent intent;
                                        if (prevActivity.equals(Constants.signup_Activity))
                                        {
                                            intent = new Intent(OtpActivity.this, SignupActivity.class);
                                        }
                                        else {
                                            intent = new Intent(OtpActivity.this, LoginActivity.class);

                                        }

                                        startActivity(intent);
                                    }
                                });

                            }

                            // ...
                        }
                    });

        }
    }

    private void setLoadingAnimation(boolean check, boolean v)
    {
        if (check) {

            lockAnimation.setAnimation(R.raw.loading_login);
            lockAnimation.setVisibility(View.VISIBLE);
            lockAnimation.playAnimation();
            lockAnimation.setRepeatMode(LottieDrawable.RESTART);
            lockAnimation.setRepeatCount(LottieDrawable.INFINITE);

        }

        else {

            lockAnimation.cancelAnimation();

            if (v)
            {
                lockAnimation.setVisibility(View.VISIBLE);
            }
            else {
                lockAnimation.setVisibility(View.GONE);
            }


        }
    }

    private void setCancelAnimation(boolean check, boolean v)
    {
        if (check) {

            lockAnimation.setAnimation(R.raw.login_fail);
            lockAnimation.setVisibility(View.VISIBLE);
            lockAnimation.playAnimation();
            lockAnimation.setRepeatCount(0);

        }

        else {

            lockAnimation.cancelAnimation();

            if (v)
            {
                lockAnimation.setVisibility(View.VISIBLE);
            }
            else {
                lockAnimation.setVisibility(View.GONE);
            }


        }


    }

    private void setCheckAnimation(boolean check, boolean v)
    {
        if (check) {

            lockAnimation.setAnimation(R.raw.phone_confirmed);
            lockAnimation.setVisibility(View.VISIBLE);
            lockAnimation.playAnimation();
            lockAnimation.setRepeatCount(0);

        }

        else {

            lockAnimation.cancelAnimation();

            if (v)
            {
                lockAnimation.setVisibility(View.VISIBLE);
            }
            else {
                lockAnimation.setVisibility(View.GONE);
            }


        }


    }

    private void setOtpText(boolean done)
    {
        if(done)
        {
            infoText.setVisibility(View.VISIBLE);
            infoText.setText("Number Verified");
        }
        else {
            infoText.setVisibility(View.VISIBLE);
            infoText.setText("Unable to automatically retrieve OTP");
        }
    }

    private void changeActivity(Class classObject)
    {
        final Intent intent = new Intent(OtpActivity.this, classObject);


        startActivity(intent);


    }


    private void setUpChocoBar(String text, int backgroundColor, int textColor, int icon)
    {

        Typeface montserrat = ResourcesCompat.getFont(OtpActivity.this, R.font.montserrat);
        Typeface gilroyBold = ResourcesCompat.getFont(OtpActivity.this, R.font.gilroy_extrabold);


        Snackbar snackbar = ChocoBar.builder().setBackgroundColor(getResources().getColor(backgroundColor))
                .setTextSize(15)
                .setTextColor(getResources().getColor(textColor))
                .setText(text)
                .setTextTypeface(montserrat)
                .setActionTextTypeface(gilroyBold)
                .setMaxLines(4)
                .setActionText("OK")

                .setActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setActionTextColor(getResources().getColor(textColor))
                .setActionTextSize(17)
                .setIcon(icon)
                .setActionTextTypefaceStyle(Typeface.BOLD)
                .setActivity(OtpActivity.this)
                .setDuration(ChocoBar.LENGTH_LONG)

                .build();


        snackbar.show();


    }


    @Override
    public void accountSetUpFinished(boolean finished) {

        continueDialog.dismiss();

        if (finished)
        {
            Intent intent = new Intent(OtpActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(OtpActivity.this);
            builder.setTitle("Account Set-Up Failed");
            builder.setMessage("We were unable to process your Sign-Up Request, please try again later.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent;
                    if (prevActivity.equals(Constants.signup_Activity))
                    {
                        intent = new Intent(OtpActivity.this, SignupActivity.class);
                    }
                    else {
                        intent = new Intent(OtpActivity.this, LoginActivity.class);

                    }

                    startActivity(intent);
                }
            });
        }

    }

    @Override
    public void onBottomSheetProfileButtonClicked(boolean type) {

        if(type)
        {
            continueDialog.setTitle("Setting Up Your Account");
            continueDialog.show();
            completeGoogleSignIn();

        }

    }

    @Override
    public void onProfilePictureSelected(int position) {

        profilePicPosition = position;

    }

    private void openModalBottomDialog()
    {
        mProfileFragment.show(getSupportFragmentManager(), "BottomSheetProfileFragment");
    }

    private void completeGoogleSignIn()
    {



        mFirebaseAuth.signInWithCredential(mAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();

                            if (user != null) {


                                if (prevActivity.equals(Constants.signup_Activity)) {

                                    signInWithPhoneAuthCredential();
                                }
                                else if (prevActivity.equals(Constants.login_Activity))
                                {
                                    final String phone = user.getPhoneNumber();
                                    if (phone!=null && !phone.equals("")) {

                                        if (phone.equals(mobileNumber)) {

                                            Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                                            continueDialog.dismiss();
                                            startActivity(intent);

                                        }

                                    }


                                    else {


                                        String uid = user.getUid();
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        DocumentReference documentReference = db.collection(Constants.users_fire).document(uid);
                                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                String storedPhone = documentSnapshot.getString(Constants.phoneNum_fire);
                                                Log.d("TAG", "onSuccess: Stored Phone:"+storedPhone);
                                                Log.d("TAG", "onSuccess: Phone Orig:"+mobileNumber);
                                                if (storedPhone.equals(mobileNumber)) {

                                                    signInWithPhoneAuthCredential();

                                                } else {

                                                    continueDialog.dismiss();

                                                    final AlertDialog.Builder builder = new AlertDialog.Builder(OtpActivity.this);

                                                    builder.setTitle("Phone Number Authentication Failed");
                                                    builder.setMessage("The phone number does not seem to link with the email you provided");
                                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                            Intent intent = new Intent();

                                                            if (prevActivity.equals(Constants.signup_Activity)) {
                                                                intent = new Intent(OtpActivity.this, SignupActivity.class);
                                                            } else if (prevActivity.equals(Constants.login_Activity)) {
                                                                intent = new Intent(OtpActivity.this, LoginActivity.class);

                                                            }

                                                            dialogInterface.dismiss();
                                                            startActivity(intent);


                                                        }
                                                    });

                                                    AlertDialog alertDialog = builder.create();

                                                    alertDialog.show();

                                                }

                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });

                                    }





                                }


                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            continueDialog.dismiss();
                            Log.w("TAG", "signInWithCredential:failure", task.getException());

                            final AlertDialog.Builder builder = new AlertDialog.Builder(OtpActivity.this);

                            builder.setTitle("Google Sign-In Failed");
                            builder.setMessage("Unable to Authenticate with Google, please try again");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Intent intent = new Intent(OtpActivity.this, VerifyPhone.class);
                                    intent.putExtra(Constants.activity_Intent, prevActivity);
                                    intent.putExtra(Constants.phone_Intent, mobileNumber);
                                    if(prevActivity.equals(Constants.signup_Activity))
                                    {
                                        intent.putExtra(Constants.nameSignup, profileName);
                                        intent.putExtra(Constants.onlyCcc_intent, onlyCcc);
                                        intent.putExtra(Constants.onlyPhoneNum_fire, onlyNum);
                                    }

                                    dialogInterface.dismiss();
                                    startActivity(intent);



                                }
                            });

                            AlertDialog alertDialog = builder.create();

                            alertDialog.show();


                        }

                        // ...
                    }
                });
    }

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(OtpActivity.this);
            builder.setTitle("Permission Request Denied");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    ActivityCompat.requestPermissions(OtpActivity.this,
                            new String[]{Manifest.permission.READ_SMS},
                            MY_PERMISSIONS_SMS);


                }
            });

            builder.setMessage("We need access to Read your SMS to auto-verify the your OTP as a part of our user security check");

            AlertDialog alertDialog = builder.create();

            alertDialog.show();



        } else {

            sendVerificationCode(mobileNumber);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    sendVerificationCode(mobileNumber);


                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(OtpActivity.this);
                    builder.setTitle("Permission Request Denied");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent();
                            if (prevActivity.equals(Constants.signup_Activity)) {
                                intent = new Intent(OtpActivity.this, SignupActivity.class);
                            } else if (prevActivity.equals(Constants.login_Activity)) {
                                intent = new Intent(OtpActivity.this, LoginActivity.class);

                            }
                            dialogInterface.dismiss();
                            startActivity(intent);


                        }
                    });

                    builder.setMessage("We were unable to auto-verify your OTP since you didn't grant the permission...");

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();

                }
                break;
        }
    }

    private void setUpMessageListener()
    {



        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                message = message.trim();

                Log.d("TAG", "onSmsCatch: message - "+message);

                int index = message.indexOf(' ');
                manuallyReadCode = message.substring(0, index);
                Log.d("TAG", "onSmsCatch: Manually read code: "+manuallyReadCode);

                if (!codeAutoVerified)
                {
                    verifyOtpDialog.dismiss();
                    lockEditText.setText(String.valueOf(manuallyReadCode));
                    setCheckAnimation(true, true);
                    setOtpText(true);
                    codeManuallyVerified = true;

                    verifyVerificationCode(manuallyReadCode);

                }

            }
        });

        smsVerifyCatcher.onStart();


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }
}