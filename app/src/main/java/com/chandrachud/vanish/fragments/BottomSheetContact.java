package com.chandrachud.vanish.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.chandrachud.vanish.Constants;
import com.chandrachud.vanish.R;
import com.chandrachud.vanish.items.backgroundItem;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.thekhaeng.pushdownanim.PushDownAnim;

public class BottomSheetContact extends BottomSheetDialogFragment {

    private Context mContext;
    private String name;
    private String phoneNumber;
    private int screenWidth;
    private int screenHeight;

    private ImageButton deleteButton;
    private ImageButton shareButton;
    private ImageButton blockButton;
    private TextView  nameText;
    private ImageView profilePic;
    private TextView phoneText;
    private ImageButton backButton;

    private LottieAnimationView profileLoadingAnimation;

    private ImageView blockImage;
    private ImageView adFreeImage;
    private ImageView notifyImage;
    private ImageView deleteImage;

    private LottieAnimationView premiumAnimation;

    private Button premiumButton;

    private ImageView premiumHeadingImage;

    private Bitmap imageBitmap;

    private ColorGenerator generator;

    private String actualNumber;

    private boolean hasCountryCode = false;

    private LinearLayout warningLayout;
    private LottieAnimationView warningAnimation;

    private ConstraintLayout bottomMainConstraintLayout;

    private LottieAnimationView loadingContactBottomAnimation;

    private TextView loadingContactBottomText;

    private LinearLayout deleteHeadingLayout;
    private LinearLayout shareHeadingLayout;
    private LinearLayout blockHeadingLayout;

    private ConstraintLayout premiumLayout;
    private ConstraintLayout premiumHeadingLayout;

    private DatabaseReference mDatabaseReference;

    private boolean otherIsPremium;

    private int otherProfilePic;
    private String otherUid;

    private int[] profilePicArray;

    private String ccc;

    private boolean otherFound;

    private boolean userIsPremium;
    private String userNumber;
    private String userCcd;



    public BottomSheetContact(String name, String phoneNumber, Context context, int screenWidth, int screenHeight, Bitmap imageBitmap, boolean userIsPremium, String userNumber, String userCcd)
    {
        this.mContext = context;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.imageBitmap = imageBitmap;
        this.userIsPremium = userIsPremium;
        this.userNumber = userNumber;
        this.userCcd = userCcd;

        generator = ColorGenerator.MATERIAL;

        actualNumber = phoneNumber;
        actualNumber = actualNumber.trim();
        actualNumber = actualNumber.replaceAll("[^\\d+]","");

        Log.d("TAG", "BottomSheetContact: actual number: "+actualNumber.charAt(0));

        otherIsPremium = false;





    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme_Contacts);

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.bottom_sheet_contacts, container, false);

        MobileAds.initialize(mContext,Constants.admobAppId);

        findViewsById(v);


        return v;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    private void findViewsById(final View v)
    {

        profilePicArray = Constants.profilePics;

        bottomMainConstraintLayout = v.findViewById(R.id.subConstraintLayout_BottomContact);
        bottomMainConstraintLayout.setVisibility(View.INVISIBLE);
        loadingContactBottomText = v.findViewById(R.id.bottomContactLoadingText);
        loadingContactBottomText.setVisibility(View.VISIBLE);

        Log.d("TAG", "findViewsById: screenWidth -  "+screenWidth);

        profilePic = v.findViewById(R.id.contactProfilePicture);
        profilePic.setVisibility(View.INVISIBLE);

        blockButton = v.findViewById(R.id.blockBottomButton);
        deleteButton = v.findViewById(R.id.deleteButtonBottom);
        shareButton = v.findViewById(R.id.shareButtonBottom);
        backButton = v.findViewById(R.id.backButton);
        premiumAnimation = v.findViewById(R.id.premiumBottomLottie);
        adFreeImage = v.findViewById(R.id.removeAdsBottomPremiumImage);
        deleteImage = v.findViewById(R.id.deleteBottomPremiumImage);
        notifyImage = v.findViewById(R.id.notifyBottomPremiumImage);
        blockImage = v.findViewById(R.id.blockBottomPremiumImage);
        premiumHeadingImage = v.findViewById(R.id.premiumCrownImgBottom);
        profileLoadingAnimation = v.findViewById(R.id.contactProfilePictureAnimation);
        warningLayout = v.findViewById(R.id.warningLinearLayout);
        warningAnimation = v.findViewById(R.id.bottomWarningAnimation);
        loadingContactBottomAnimation = v.findViewById(R.id.loadingContactsBottomAnimation);



        loadingContactBottomAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    loadingContactBottomAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    loadingContactBottomAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int newDimensions = (int) (screenWidth * 0.45);
                loadingContactBottomAnimation.getLayoutParams().width = newDimensions;


                loadingContactBottomAnimation.getLayoutParams().height = newDimensions;

                loadingContactBottomAnimation.playAnimation();

            }



        });



        profilePic.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    profilePic.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    profilePic.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int newDimensions = (int) (screenWidth * 0.25);
                profilePic.getLayoutParams().width = newDimensions;
                Log.d("TAG", "onGlobalLayout: profile pic Dimensions"+newDimensions);


                profilePic.getLayoutParams().height = newDimensions;

                //Log.d("TAG", "findViewsById: screenwidth:"+screen_width);
                //Log.d("TAG", "findViewsById: newHeight"+newHeight);

                /*profileLoadingAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            profileLoadingAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            profileLoadingAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }


                        int newDimensions = (int) (screenWidth * 0.2);
                        profileLoadingAnimation.getLayoutParams().width = newDimensions;


                        profileLoadingAnimation.getLayoutParams().height = newDimensions;

                        profileLoadingAnimation.setAnimation(R.raw.loading_contact_bottom);
                        profileLoadingAnimation.playAnimation();

                    }



                });*/

                setDefaultProfilePicture();











            }



        });

        deleteButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    deleteButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    deleteButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.25);
                deleteButton.getLayoutParams().width = newDimensions;


                deleteButton.getLayoutParams().height = newDimensions;
                Log.d("TAG", "onGlobalLayout: deleteButton Dimensions"+newDimensions);


                //Log.d("TAG", "findViewsById: screenwidth:"+screen_width);
                //Log.d("TAG", "findViewsById: newHeight"+newHeight);

                Log.d("TAG", "onGlobalLayout: deleteButton Dimensions - Direct"+deleteButton.getHeight());



            }



        });

        blockButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    blockButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    blockButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.146);
                blockButton.getLayoutParams().width = newDimensions;


                blockButton.getLayoutParams().height = newDimensions;



            }



        });

        shareButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    shareButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    shareButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.146);
                shareButton.getLayoutParams().width = newDimensions;


                shareButton.getLayoutParams().height = newDimensions;





            }



        });

        backButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    backButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    backButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.10);
                backButton.getLayoutParams().width = newDimensions;




                backButton.getLayoutParams().height = newDimensions;


            }



        });

        premiumHeadingImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    premiumHeadingImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    premiumHeadingImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.05);
                premiumHeadingImage.getLayoutParams().width = newDimensions;




                premiumHeadingImage.getLayoutParams().height = newDimensions;



            }



        });

        premiumAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    premiumAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    premiumAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.25);
                premiumAnimation.getLayoutParams().width = newDimensions;




                premiumAnimation.getLayoutParams().height = newDimensions;



            }



        });

        blockImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    blockImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    blockImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.07);
                blockImage.getLayoutParams().width = newDimensions;




                blockImage.getLayoutParams().height = newDimensions;


            }



        });

        adFreeImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    adFreeImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    adFreeImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.07);
                adFreeImage.getLayoutParams().width = newDimensions;




                adFreeImage.getLayoutParams().height = newDimensions;



            }



        });

        deleteImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    deleteImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    deleteImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.07);
                deleteImage.getLayoutParams().width = newDimensions;




                deleteImage.getLayoutParams().height = newDimensions;



            }



        });

        notifyImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    notifyImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    notifyImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.07);
                notifyImage.getLayoutParams().width = newDimensions;




                notifyImage.getLayoutParams().height = newDimensions;



            }



        });

        warningAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    warningAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    warningAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.1);
                warningAnimation.getLayoutParams().width = newDimensions;




                warningAnimation.getLayoutParams().height = newDimensions;



            }



        });



        warningLayout.setVisibility(View.GONE);


        nameText = v.findViewById(R.id.nameBottomContact);
        phoneText = v.findViewById(R.id.phoneBottomContact);
        premiumButton = v.findViewById(R.id.premiumBottomButton);

        deleteHeadingLayout = v.findViewById(R.id.deleteNumberTextLayout);
        blockHeadingLayout = v.findViewById(R.id.blockNumberTextLayout);
        shareHeadingLayout = v.findViewById(R.id.shareAppTextLayout);

        premiumLayout = v.findViewById(R.id.premiumBottomSheetBanner);


        premiumHeadingLayout = v.findViewById(R.id.premiumHeadingConstraintLayout);

        if (userIsPremium)
        {
            premiumLayout.setVisibility(View.GONE);
            premiumHeadingLayout.setVisibility(View.GONE);
        }


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        otherIsPremium = false;

        otherFound = false;

        checkIfNumberIsValid(actualNumber);




    }

    private void setOnClickListeners()
    {
        PushDownAnim.setPushDownAnimTo(deleteButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        setUpCustomDialog();

                    }
                });

        PushDownAnim.setPushDownAnimTo(shareButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {




                    }
                });

        PushDownAnim.setPushDownAnimTo(blockButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {




                    }
                });

        PushDownAnim.setPushDownAnimTo(backButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dismiss();

                }
                });

        PushDownAnim.setPushDownAnimTo(premiumButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                    }
                });

    }

    private void checkIfNumberIsValid(String phone)
    {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Log.d("TAG", "checkIfNumberIsValid: phone number:"+phone);
            // phone must begin with '+'
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phone, "");
            int countryCode = numberProto.getCountryCode();
            Log.d("TAG", "checkIfNumberIsValid: country code:"+countryCode);

            hasCountryCode = true;

            checkIfNumberExistsInDatabase();



        } catch (NumberParseException e) {
            Log.d("TAG", ("NumberParseException was thrown: " + e.toString()));

            setDefaultInformation(true);

        }

    }

    private void checkIfNumberExistsInDatabase()
    {
        if (hasCountryCode)
        {
            Query phoneQuery = mDatabaseReference.orderByChild(Constants.phone_realtime).equalTo(actualNumber);

            phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists())
                    {
                        Log.d("TAG", "onDataChange: Children count: "+snapshot.getChildrenCount());

                        Log.d("TAG", "onDataChange: Number found exist in database ");

                        for (DataSnapshot outputSnap : snapshot.getChildren())
                        {
                            backgroundItem item = outputSnap.getValue(backgroundItem.class);
                            if (item!=null) {

                                Log.d("TAG", "onDataChange: Item not null in database ");

                                otherIsPremium = item.isPremium();
                                otherProfilePic = item.getProfileNum();
                                otherUid = item.getUid();

                                otherFound = true;

                                Log.d("TAG", "setUpCustomDialog: UID 2 - "+otherUid);



                                loadingContactBottomAnimation.setVisibility(View.GONE);
                                loadingContactBottomAnimation.cancelAnimation();
                                loadingContactBottomText.setVisibility(View.GONE);

                                bottomMainConstraintLayout.setVisibility(View.VISIBLE);

                                playAnimationOnView(profilePic, true, Techniques.Landing);
                                profilePic.setImageResource(profilePicArray[otherProfilePic]);


                                playAnimationOnView(nameText, true, Techniques.Landing);
                                nameText.setVisibility(View.VISIBLE);
                                nameText.setText(name);

                                playAnimationOnView(phoneText, true, Techniques.Landing);
                                phoneText.setVisibility(View.VISIBLE);
                                phoneText.setText(phoneNumber);



                                setVisibilityForRemainingViews();

                            }


                        }

                    }

                    else {
                        otherFound = false;
                        Log.d("TAG", "onDataChange: Number does not exist in database ");
                        setDefaultInformation(false);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    otherFound = false;

                    Log.d("TAG", "onCancelled: Error:"+error.toString());
                    setDefaultInformation(false);

                }
            });



        }

        else
        {

            setDefaultInformation(true);

        }


    }

    private void setDefaultProfilePicture()
    {

        int newDimensions = (int) (screenWidth * 0.25);


        if (imageBitmap!=null){
            playAnimationOnView(profilePic, true, Techniques.Landing);

            profilePic.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, newDimensions, newDimensions, false));
            Log.d("TAG", "onGlobalLayout: bitmap set");


        }

        else {

            Typeface font = ResourcesCompat.getFont(mContext, R.font.montserrat);


            TextDrawable imageDrawable = TextDrawable.builder()
                    .beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(font)
                    .width(newDimensions)
                    .height(newDimensions)
                    .bold().toUpperCase()
                    .endConfig()
                    .buildRound(Character.toString(Character.toUpperCase(name.charAt(0))), generator.getColor(name));

            playAnimationOnView(profilePic, true, Techniques.Landing);

            profilePic.setImageDrawable(imageDrawable);


        }

    }

    private void playAnimationOnView(View v, boolean show, Techniques animation)
    {
            YoYo.with(animation)
                    .duration(1500)
                    .repeat(0)
                    .playOn(v);

            if (v.getVisibility()==View.INVISIBLE)
            {
                v.setVisibility(View.VISIBLE);
            }




    }

    private void setVisibilityForRemainingViews()
    {
        setOnClickListeners();

        YoYo.with(Techniques.Landing)
                .duration(1500)
                .repeat(0)
                .playOn(deleteButton);

        deleteButton.setVisibility(View.VISIBLE);


        YoYo.with(Techniques.Landing)
                .duration(1500)
                .repeat(0)
                .playOn(blockHeadingLayout);

        blockHeadingLayout.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.Landing)
                .duration(1500)
                .repeat(0)
                .playOn(deleteHeadingLayout);

        deleteHeadingLayout.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.Landing)
                .duration(1500)
                .repeat(0)
                .playOn(shareHeadingLayout);

        shareHeadingLayout.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.Landing)
                .duration(1500)
                .repeat(0)
                .playOn(premiumHeadingLayout);

        premiumHeadingImage.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.Landing)
                .duration(2000)
                .repeat(0)
                .playOn(premiumLayout);

        premiumLayout.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.Landing)
                .duration(1500)
                .repeat(0)
                .playOn(backButton);

        backButton.setVisibility(View.VISIBLE);

        blockButton.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.Landing)
                .duration(1500)

                .repeat(0)
                .playOn(blockButton);


        blockButton.clearAnimation();

        YoYo.with(Techniques.Landing)
                .duration(1500)
                .repeat(0)
                .playOn(shareButton);

        shareButton.setVisibility(View.VISIBLE);






    }

    private void setDefaultInformation(boolean showWarning)
    {
        loadingContactBottomAnimation.setVisibility(View.GONE);
        loadingContactBottomAnimation.cancelAnimation();
        loadingContactBottomText.setVisibility(View.GONE);

        bottomMainConstraintLayout.setVisibility(View.VISIBLE);

        setDefaultProfilePicture();

        playAnimationOnView(nameText, true, Techniques.Landing);
        nameText.setVisibility(View.VISIBLE);
        nameText.setText(name);

        playAnimationOnView(phoneText, true, Techniques.Landing);
        phoneText.setVisibility(View.VISIBLE);
        phoneText.setText(phoneNumber);

        if (showWarning) {
            warningLayout.setVisibility(View.VISIBLE);
            warningAnimation.playAnimation();
        }

        setVisibilityForRemainingViews();

    }

    private void setUpCustomDialog()
    {

        String cons="";
        if(!hasCountryCode)
        {
            cons = Constants.countryCode_delete_dialog;
        }
        else if (!otherFound)
        {
            cons =Constants.not_found_delete_dialog;
        }
        else if (otherFound)
        {
            if (otherIsPremium)
            {
                if (userIsPremium)
                {
                    cons = Constants.normal_delete_dialog;
                }
                else {
                    cons = Constants.premium_user_dialog;
                }

            }
            else {
                cons = Constants.normal_delete_dialog;
            }

        }

        Log.d("TAG", "setUpCustomDialog: UID 1 - "+otherUid);

        DeleteBottomDialog deleteDialog = new DeleteBottomDialog((Activity) mContext, cons, screenWidth, otherUid, actualNumber, userNumber, userCcd, userIsPremium);
        deleteDialog.startDialog();



    }

}
