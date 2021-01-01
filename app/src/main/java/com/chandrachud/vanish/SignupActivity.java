package com.chandrachud.vanish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
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

import dmax.dialog.SpotsDialog;

public class SignupActivity extends AppCompatActivity {

    private Button signInButton;
    private ImageButton backButton;
    private ImageView signinImage;
    private ImageView profileImage;
    private EditText profileEditText;
    private View profileDivider;
    private ImageView phoneImage;
    private TextView phoneEditText;
    private View phoneDivider;
    private LottieAnimationView phoneAnimation;
    private TextView loginText;
    private CountryCodePicker ccp;

    private boolean textEnteredPhone;
    private boolean textEnteredName;

    private FirebaseFirestore db;

    private TextView infoText;

    private String profileName;

    private AlertDialog loadingDialog;

    private String onlyNum;
    private String onlyCcc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_layout);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(SignupActivity.this,R.color.white));// set status background white

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
        signinImage = findViewById(R.id.signupImage);
        phoneAnimation = findViewById(R.id.signupPhoneAnimation);

        signinImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    signinImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    signinImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }



                signinImage.getLayoutParams().width = screen_width;
                int newHeight = (int) (screen_width*0.8);

                signinImage.getLayoutParams().height = newHeight;


            }
        });

        phoneAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    phoneAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    phoneAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int)(screen_width*0.13);
                phoneAnimation.getLayoutParams().width = newDimensions;


                phoneAnimation.getLayoutParams().height = newDimensions;


            }
        });

        signInButton = findViewById(R.id.signupButton);
        backButton = findViewById(R.id.backButton);
        profileImage = findViewById(R.id.signupNameImage);
        profileEditText = findViewById(R.id.signupNameEditText);
        profileDivider = findViewById(R.id.signupNameDivider);
        phoneImage = findViewById(R.id.signupPhoneImage);
        phoneEditText = findViewById(R.id.signupPhoneEditText);
        phoneDivider = findViewById(R.id.signupPhoneDivider);

        loginText = findViewById(R.id.logInText);
        ccp = findViewById(R.id.signupCcp);
        infoText = findViewById(R.id.signupInfoText);
        infoText.setText("");
        infoText.setVisibility(View.INVISIBLE);


        textEnteredPhone = false;
        textEnteredName = false;

        db = FirebaseFirestore.getInstance();

        //loadingDialog = new SpotsDialog(SignupActivity.this, R.style.loadingSignUp);

        loadingDialog = new SpotsDialog.Builder().setContext(SignupActivity.this).build();
        loadingDialog.setTitle("Setting Up Your Account");
        loadingDialog.setMessage("Please Wait...");




    }

    private void setOnClickListeners()
    {
        PushDownAnim.setPushDownAnimTo(signInButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (!phoneEditText.getText().toString().isEmpty() && !(profileEditText.getText().toString().isEmpty()))
                        {
                            String countryCode = ccp.getSelectedCountryCodeWithPlus();
                            String number = phoneEditText.getText().toString();

                            String completePhone = countryCode+number;

                            setLoadingAnimation(true, true);


                            if (checkIfNumberIsValid(number, countryCode)) {

                                setProfileName();
                                loadingDialog.show();
                                checkForNumber(completePhone, true);
                            }

                            else {

                                setProfileText("The Number is Invalid");
                                setCancelAnimation(true, true);
                                setUpChocoBar("Invalid Number", R.color.chocoYellow, R.color.white, R.drawable.warning_snackbar);

                            }


                        }

                        else {
                            if (profileEditText.getText().toString().isEmpty())
                            {
                                setUpChocoBar("Your Name field cannot be Empty", R.color.loginMain, R.color.white, R.drawable.error_snackbar);

                            }

                            else if (phoneEditText.getText().toString().isEmpty())
                            {
                                setUpChocoBar("Your Phone Number field cannot be Empty", R.color.loginMain, R.color.white, R.drawable.error_snackbar);

                            }
                        }



                    }
                });

        PushDownAnim.setPushDownAnimTo(backButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        changeActivity(ChooseSignup.class);



                    }
                });

        PushDownAnim.setPushDownAnimTo(loginText)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        changeActivity(LoginActivity.class);



                    }
                });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                phoneAnimation.cancelAnimation();

                if (textEnteredPhone && charSequence.toString().trim().isEmpty())
                {
                    setDividerAndPhone(false);
                }

                else {
                    setDividerAndPhone(true);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!(phoneEditText.getText().toString().isEmpty())) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            String countryCode = ccp.getSelectedCountryCodeWithPlus();
                            String number = phoneEditText.getText().toString();
                            String completePhone = countryCode + number;

                            if (checkIfNumberIsValid(number, countryCode)) {
                                setCheckAnimation(true, true);
                            } else {
                                Log.d("TAG", "afterTextChanged: Number is not valid");
                                setCancelAnimation(true, true);
                            }


                        }
                    }, 1200);


                }
            }
        });

        profileEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (textEnteredName && charSequence.toString().trim().isEmpty())
                {
                    setDividerAndProfile(false);
                }

                else {
                    setDividerAndProfile(true);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void changeActivity(Class classObject)
    {
        final Intent intent = new Intent(SignupActivity.this, classObject);
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
            phoneImage.setImageResource(R.drawable.phone_blue_30);

            textEnteredPhone = true;
        }
        else {

            phoneDivider.setBackgroundColor(getResources().getColor(R.color.grayMain));
            phoneImage.setImageResource(R.drawable.phone_gray_30);

            textEnteredPhone = false;


        }

    }

    private void setDividerAndProfile(boolean check)
    {
        if(check)
        {
            profileDivider.setBackgroundColor(getResources().getColor(R.color.loginMain));
            profileImage.setImageResource(R.drawable.user_blue);

            textEnteredName = true;
        }
        else {

            profileDivider.setBackgroundColor(getResources().getColor(R.color.grayMain));
            profileImage.setImageResource(R.drawable.user_grey);

            textEnteredName = false;


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
                            setCancelAnimation(true, true);
                            setSignUpInfoText(true);

                            if (continueToOtp)
                            {
                                setUpChocoBar("Number already Registered", R.color.chocoRed, R.color.white, R.drawable.error_snackbar);
                                loadingDialog.dismiss();

                            }

                        }
                        else {

                            if (continueToOtp)
                            {
                                //setCheckAnimation(true, true);
                                onlyNum = phoneEditText.getText().toString();
                                onlyCcc = ccp.getSelectedCountryCodeWithPlus();
                                Intent intent = new Intent(SignupActivity.this, VerifyPhone.class);
                                intent.putExtra(Constants.phone_Intent, number);
                                intent.putExtra(Constants.activity_Intent, Constants.signup_Activity);
                                intent.putExtra(Constants.nameSignup, profileName);
                                intent.putExtra(Constants.onlyNum_intent, onlyNum);
                                intent.putExtra(Constants.onlyCcc_intent, onlyCcc);
                                loadingDialog.dismiss();
                                startActivity(intent);
                            }

                            else {
                                setCheckAnimation(true, true);
                                setSignUpInfoText(false);
                            }





                        }

                    }
                });


    }

    private void setLoadingAnimation(boolean check, boolean v)
    {
        if (check) {

            phoneAnimation.setAnimation(R.raw.loading_login);
            phoneAnimation.setVisibility(View.VISIBLE);
            phoneAnimation.playAnimation();
            phoneAnimation.setRepeatMode(LottieDrawable.RESTART);
            phoneAnimation.setRepeatCount(LottieDrawable.INFINITE);

        }

        else {

            phoneAnimation.cancelAnimation();

            if (v)
            {
                phoneAnimation.setVisibility(View.VISIBLE);
            }
            else {
                phoneAnimation.setVisibility(View.GONE);
            }


        }
    }

    private void setCancelAnimation(boolean check, boolean v)
    {
        if (check) {

            phoneAnimation.setAnimation(R.raw.login_fail);
            phoneAnimation.setVisibility(View.VISIBLE);
            phoneAnimation.playAnimation();
            phoneAnimation.setRepeatCount(0);

        }

        else {

            phoneAnimation.cancelAnimation();

            if (v)
            {
                phoneAnimation.setVisibility(View.VISIBLE);
            }
            else {
                phoneAnimation.setVisibility(View.GONE);
            }


        }


    }

    private void setCheckAnimation(boolean check, boolean v)
    {
        if (check) {

            phoneAnimation.setAnimation(R.raw.phone_confirmed);
            phoneAnimation.setVisibility(View.VISIBLE);
            phoneAnimation.playAnimation();
            phoneAnimation.setRepeatCount(0);

        }

        else {

            phoneAnimation.cancelAnimation();

            if (v)
            {
                phoneAnimation.setVisibility(View.VISIBLE);
            }
            else {
                phoneAnimation.setVisibility(View.GONE);
            }


        }


    }

    private void setSignUpInfoText(boolean found)
    {
        if (found)
        {
            infoText.setText("This Number is already registered");
            infoText.setVisibility(View.VISIBLE);
        }
        else {
            infoText.setText("");
            infoText.setVisibility(View.INVISIBLE);
        }
    }

    private void setProfileName()
    {
        profileName = profileEditText.getText().toString();

    }

    private void setProfileText(String text)
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

        Typeface montserrat = ResourcesCompat.getFont(SignupActivity.this, R.font.montserrat);
        Typeface gilroyBold = ResourcesCompat.getFont(SignupActivity.this, R.font.gilroy_extrabold);


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
                .setActivity(SignupActivity.this)
                .setDuration(ChocoBar.LENGTH_LONG)

                .build();


        snackbar.show();


    }

}