package com.chandrachud.vanish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.chandrachud.vanish.items.PhoneValidateResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.pd.chocobar.ChocoBar;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private ImageButton backButton;
    private ImageView phoneImage;
    private EditText phoneText;
    private CountryCodePicker ccp;
    private LottieAnimationView loginAnimation;
    private TextView signText;
    private TextView infoText;
    private View phoneDivider;
    private ImageView loginImage;
    boolean textEntered;

    boolean numberVerified;

    private FirebaseFirestore db;

    public AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(LoginActivity.this,R.color.white));// set status background white

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }

        findViewsById();
        setOnClickListeners();

    }

    private void findViewsById()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int screen_width = displayMetrics.widthPixels;
        loginImage = findViewById(R.id.loginImage);
        loginAnimation = findViewById(R.id.loginAnimation);


        loginImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    loginImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    loginImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }



                loginImage.getLayoutParams().width = screen_width;
                int newHeight = (int) (screen_width*0.8);

                loginImage.getLayoutParams().height = newHeight;




            }
        });

        loginAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    loginAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    loginAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int)(screen_width*0.13);
                loginAnimation.getLayoutParams().width = newDimensions;


                loginAnimation.getLayoutParams().height = newDimensions;





            }
        });


        loginButton = findViewById(R.id.loginButton);
        signText = findViewById(R.id.signInText);
        infoText = findViewById(R.id.loginInfoText);
        ccp = findViewById(R.id.loginCcp);
        phoneImage = findViewById(R.id.loginPhoneImage);
        phoneText = findViewById(R.id.loginEditText);
        infoText = findViewById(R.id.loginInfoText);
        signText = findViewById(R.id.signInText);
        backButton = findViewById(R.id.backButton);
        phoneDivider = findViewById(R.id.loginUnderDivider);

        textEntered = false;
        numberVerified = false;

        db = FirebaseFirestore.getInstance();

        loadingDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).build();
        loadingDialog.setMessage("Please Wait...");
        loadingDialog.setTitle("Logging In");





    }

    private void setOnClickListeners()
    {
        PushDownAnim.setPushDownAnimTo(backButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                       changeActivity(ChooseSignup.class);


                    }
                });

        PushDownAnim.setPushDownAnimTo(loginButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (!phoneText.getText().toString().isEmpty())
                        {
                            String countryCode = ccp.getSelectedCountryCodeWithPlus();
                            String number = phoneText.getText().toString();

                            String completePhone = countryCode+number;

                            setLoadingAnimation(true, true);


                            if (checkIfNumberIsValid(number, countryCode)) {

                                loadingDialog.show();

                                checkForNumber(completePhone, true);

                            }

                            else {

                                setCustomLoginInfoText("The Number is Invalid");
                                setCancelAnimation(true, true);
                                setUpChocoBar("Invalid Number", R.color.chocoYellow, R.color.white, R.drawable.warning_snackbar);

                            }


                        }

                        else {

                            setUpChocoBar("The Phone number field cannot be Empty", R.color.loginMain, R.color.white, R.drawable.error_snackbar);


                        }



                    }
                });

        PushDownAnim.setPushDownAnimTo(signText)
                .setScale(PushDownAnim.MODE_SCALE, 0.9f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        changeActivity(SignupActivity.class);

                    }
                });

        phoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (textEntered && charSequence.toString().trim().isEmpty())
                {
                    setDividerAndPhone(false);
                }

                else {
                    setDividerAndPhone(true);
                }

                loginAnimation.cancelAnimation();


            }

            @Override
            public void afterTextChanged(Editable editable) {



                if (!(phoneText.getText().toString().isEmpty()))
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            String countryCode = ccp.getSelectedCountryCodeWithPlus();
                            String number = phoneText.getText().toString();
                            String completePhone = countryCode + number;



                            if (!checkIfNumberIsValid(number, countryCode)) {
                                setCheckAnimation(true, true);

                                Log.d("TAG", "afterTextChanged: Number is not valid");
                                setCancelAnimation(true, true);
                            }



                        }
                    }, 1200);
                }




            }
        });



    }

    private void changeActivity(Class classObject)
    {
        final Intent intent = new Intent(LoginActivity.this, classObject);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(intent);

            }
        }, 1200);
    }

    private void setDividerAndPhone(boolean check)
    {
        if(check)
        {
            phoneDivider.setBackgroundColor(getResources().getColor(R.color.loginMain));
            phoneImage.setImageResource(R.drawable.phone_login_blue);

            textEntered = true;
        }
        else {

            phoneDivider.setBackgroundColor(getResources().getColor(R.color.grayMain));
            phoneImage.setImageResource(R.drawable.phone_login);

            textEntered = false;


        }

    }

    private void checkForNumber(final String number, final boolean continueToOtp)
    {
        setLoadingAnimation(true, true);
        db.collection(Constants.users_fire).whereEqualTo(Constants.phoneNum_fire, number)
                .get()

                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful() && task.getResult().size()>0)
                        {
                            Log.d("TAG", "onComplete: number found");

                            if (continueToOtp)
                            {
                                setCheckAnimation(true, true);

                               Intent intent = new Intent(LoginActivity.this, VerifyPhone.class);
                               intent.putExtra(Constants.phone_Intent, number);
                                intent.putExtra(Constants.activity_Intent, Constants.login_Activity);
                                loadingDialog.dismiss();
                                startActivity(intent);
                            }

                            else {
                                setCheckAnimation(true, true);
                                setLoginInfoText(true);
                            }

                        }
                        else {


                            Log.d("TAG", "onComplete: number not found"+number);

                            setCancelAnimation(true, true);
                            setLoginInfoText(false);

                            if (continueToOtp)
                            {
                                loadingDialog.dismiss();
                                setUpChocoBar("This Number is unregistered", R.color.chocoRed, R.color.white, R.drawable.error_snackbar);

                            }

                        }

                    }
                });


    }

    private void setLoadingAnimation(boolean check, boolean v)
    {
        if (check) {
            if (loginAnimation.isAnimating())
            {
                loginAnimation.cancelAnimation();
            }
            loginAnimation.setAnimation(R.raw.loading_login);
            loginAnimation.setVisibility(View.VISIBLE);
            loginAnimation.playAnimation();
            loginAnimation.setRepeatMode(LottieDrawable.RESTART);
            loginAnimation.setRepeatCount(LottieDrawable.INFINITE);

        }

        else {

            loginAnimation.cancelAnimation();

            if (v)
            {
                loginAnimation.setVisibility(View.VISIBLE);
            }
            else {
                loginAnimation.setVisibility(View.GONE);
            }


        }
    }

    private void setCancelAnimation(boolean check, boolean v)
    {
        if (check) {
            if (loginAnimation.isAnimating())
            {
                loginAnimation.cancelAnimation();
            }
            loginAnimation.setAnimation(R.raw.login_fail);
            loginAnimation.setVisibility(View.VISIBLE);
            loginAnimation.playAnimation();
            loginAnimation.setRepeatCount(0);

        }

        else {

            loginAnimation.cancelAnimation();

            if (v)
            {
                loginAnimation.setVisibility(View.VISIBLE);
            }
            else {
                loginAnimation.setVisibility(View.GONE);
            }


        }


    }

    private void setCheckAnimation(boolean check, boolean v)
    {
        if (check) {
            if (loginAnimation.isAnimating())
            {
                loginAnimation.cancelAnimation();
            }
            loginAnimation.setAnimation(R.raw.phone_confirmed);
            loginAnimation.setVisibility(View.VISIBLE);
            loginAnimation.playAnimation();
            loginAnimation.setRepeatCount(0);

        }

        else {

            loginAnimation.cancelAnimation();

            if (v)
            {
                loginAnimation.setVisibility(View.VISIBLE);
            }
            else {
                loginAnimation.setVisibility(View.GONE);
            }


        }


    }

    private void setLoginInfoText(boolean found)
    {
        if (infoText.getVisibility()==View.INVISIBLE)
        {
            infoText.setVisibility(View.VISIBLE);
        }
        if (found)
        {

            infoText.setText("Valid Registered Number Confirmed!");
        }
        else {
            infoText.setText("This Number is Unregistered");
        }
    }

    private void setCustomLoginInfoText(String text)
    {
        infoText.setText(text);
    }

    private boolean checkIfNumberIsValid(String number, String countryCode)
    {
        PhoneValidateResponse phoneNumberValidate = new PhoneValidateResponse();
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        boolean finalNumber = false;
        PhoneNumberUtil.PhoneNumberType isMobile = null;
        boolean isValid = false;
        try {
            String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
            phoneNumber = phoneNumberUtil.parse(number, isoCode);
            isValid = phoneNumberUtil.isValidNumber(phoneNumber);
            isMobile = phoneNumberUtil.getNumberType(phoneNumber);
            phoneNumberValidate.setCode(String.valueOf(phoneNumber.getCountryCode()));
            phoneNumberValidate.setPhone(String.valueOf(phoneNumber.getNationalNumber()));
            phoneNumberValidate.setValid(false);

        } catch (NumberParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (isValid && (PhoneNumberUtil.PhoneNumberType.MOBILE == isMobile ||
                PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE == isMobile)) {
            finalNumber = true;
            phoneNumberValidate.setValid(true);
        }

        return phoneNumberValidate.isValid();


    }

    private void setUpChocoBar(String text, int backgroundColor, int textColor, int icon)
    {

        Typeface montserrat = ResourcesCompat.getFont(LoginActivity.this, R.font.montserrat);
        Typeface gilroyBold = ResourcesCompat.getFont(LoginActivity.this, R.font.gilroy_extrabold);


        Snackbar snackbar = ChocoBar.builder().setBackgroundColor(getResources().getColor(backgroundColor))
                .setTextSize(14)
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
                .setActivity(LoginActivity.this)
                .setDuration(ChocoBar.LENGTH_LONG)

                .build();


        snackbar.show();


    }


}