package com.chandrachud.vanish.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.chandrachud.vanish.Constants;
import com.chandrachud.vanish.R;
import com.chandrachud.vanish.items.NotificationManagerItem;
import com.chandrachud.vanish.items.backgroundItem;
import com.chandrachud.vanish.items.numberItemFirebase;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DeleteBottomDialog {

    private Activity mActivity;
    private String cons;
    private int screenWidth;

    private LottieAnimationView deleteAnimation;
    private LottieAnimationView loadingAnimation;
    private TextView mainButton;
    private TextView cancelButton;
    private TextView headingText;
    private TextView infoText;
    private TextView phoneText;
    private String uid;
    private String actualNumber;

    private Dialog deleteDialog;

    private RewardedVideoAd mRewardedVideoAd;

    private TextView loadingText;

    private CountryCodePicker mCountryCodePicker;

    private String userNumber;

    private String userCcd;
    private boolean userPremium;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private ArrayList<String> premiumNumbersList;

    private boolean otherIsPremium;


    private DatabaseReference mDatabaseReference;

    private boolean foundInDatabase=false;

    public DeleteBottomDialog(Activity activity, String cons, int screenWidth, String uid, String actualNumber, String userNumber, String userCcd, boolean userPremium) {

        this.mActivity = activity;
        this.cons = cons;
        this.screenWidth = screenWidth;
        this.uid = uid;
        this.actualNumber = actualNumber;
        this.userNumber = userNumber;
        this.userCcd = userCcd;
        this.userPremium = userPremium;

        mSharedPreferences = mActivity.getSharedPreferences(Constants.shared_prefs, Context.MODE_PRIVATE );
        mEditor = mSharedPreferences.edit();
        premiumNumbersList = new ArrayList<>();



    }

    public void startDialog()
    {
        deleteDialog = new Dialog(mActivity );
        deleteDialog.setContentView(R.layout.delete_popup);
        Window window = deleteDialog.getWindow();

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        findViewsById();
        deleteDialog.show();

    }

    private void findViewsById()
    {
        deleteAnimation = deleteDialog.findViewById(R.id.deletePopupAnimation);
        loadingAnimation = deleteDialog.findViewById(R.id.loadingPopupAnimation);
        mainButton = deleteDialog.findViewById(R.id.mainPopupButton);
        cancelButton = deleteDialog.findViewById(R.id.cancelPopupButton);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        deleteAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    deleteAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    deleteAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth*0.5);

                deleteAnimation.getLayoutParams().width = newDimensions;
                deleteAnimation.getLayoutParams().height = newDimensions;

            }
        });

        loadingAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    loadingAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    loadingAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int newDimensions = (int) (screenWidth*0.5);

                loadingAnimation.getLayoutParams().width = newDimensions;
                loadingAnimation.getLayoutParams().height = newDimensions;

            }
        });

        headingText =  deleteDialog.findViewById(R.id.headingPopupText);
        infoText = deleteDialog.findViewById(R.id.infoPopupText);
        phoneText = deleteDialog.findViewById(R.id.headingPhoneText);
        loadingText = deleteDialog.findViewById(R.id.loadingText);
        loadingText.setVisibility(View.INVISIBLE);
        mCountryCodePicker = deleteDialog.findViewById(R.id.countryCodePicker);
        mCountryCodePicker.setVisibility(View.GONE);
        mCountryCodePicker.setClickable(true);

        if (cons.equals(Constants.not_found_delete_dialog))
        {
            deleteAnimation.setAnimation(R.raw.user_not_found);

            deleteAnimation.playAnimation();

        }

        else if (cons.equals(Constants.countryCode_delete_dialog))
        {
            deleteAnimation.setAnimation(R.raw.choose_country_code);
            deleteAnimation.playAnimation();

        }
        else if (cons.equals(Constants.premium_user_dialog))
        {
            deleteAnimation.setAnimation(R.raw.premium_animation);
            deleteAnimation.playAnimation();
        }

        setOnClickListeners();

        if (cons.equals(Constants.normal_delete_dialog))
        {

            mainButton.setVisibility(View.INVISIBLE);
            phoneText.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);
            headingText.setVisibility(View.INVISIBLE);
            infoText.setVisibility(View.INVISIBLE);
            deleteAnimation.setVisibility(View.INVISIBLE);

            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(mActivity);

            loadingAnimation.setAnimation(R.raw.delete_button_loading);
            playLandingAnimation(loadingAnimation);
            loadingAnimation.playAnimation();

            checkIfNumberExistsInOther();

        }
        else if (cons.equals(Constants.not_found_delete_dialog))
        {

            Log.d("TAG", "findViewsById: not found Entered");

            playLandingAnimation(mainButton);
            mainButton.setText("Share");

            phoneText.setText(actualNumber+" is not Registered on Vanish");

            playLandingAnimation(cancelButton);

            playLandingAnimation(headingText);
            headingText.setText(mActivity.getResources().getString(R.string.notfound_delete_heading));
            playLandingAnimation(infoText);
            infoText.setText(mActivity.getResources().getString(R.string.notfound_delete_info));

        }

        else if (cons.equals(Constants.countryCode_delete_dialog))
        {

            Log.d("TAG", "findViewsById: Country code Entered");

            playLandingAnimation(mainButton);
            mainButton.setText("Choose");

            phoneText.setText(actualNumber);

            playLandingAnimation(cancelButton);

            playLandingAnimation(headingText);
            headingText.setText(mActivity.getResources().getString(R.string.countrycode_delete_heading));
            playLandingAnimation(infoText);
            infoText.setText(mActivity.getResources().getString(R.string.countrycode_delete_info));

        }

        else if (cons.equals(Constants.premium_user_dialog))
        {
            playLandingAnimation(mainButton);
            mainButton.setText("Go Premium!");

            phoneText.setText(actualNumber);

            playLandingAnimation(cancelButton);

            playLandingAnimation(headingText);
            headingText.setText(mActivity.getResources().getString(R.string.premium_delete_dialog_heading));
            playLandingAnimation(infoText);
            infoText.setText(mActivity.getResources().getString(R.string.premium_delete_dialog_info));

            checkRecentPremiumAttempted();

        }

    }

    private void setOnClickListeners()
    {

        PushDownAnim.setPushDownAnimTo(cancelButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        deleteAnimation.cancelAnimation();
                        deleteDialog.dismiss();


                    }
                });

        PushDownAnim.setPushDownAnimTo(mainButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (cons.equals(Constants.normal_delete_dialog))
                        {

                            setLoadingAnimation(true );
                            Log.d("TAG", "onClick:entered normal ");

                            mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {

                                @Override
                                public void onRewardedVideoAdLoaded() {
                                    Log.d("TAG", "onRewardedVideoAdLoaded: ");

                                    mRewardedVideoAd.show();
                                }

                                @Override
                                public void onRewardedVideoAdOpened() {

                                    Log.d("TAG", "onRewardedVideoAdOpened: ");

                                }

                                @Override
                                public void onRewardedVideoStarted() {

                                    Log.d("TAG", "onRewardedVideoStarted: ");

                                }

                                @Override
                                public void onRewardedVideoAdClosed() {

                                    Log.d("TAG", "onRewardedVideoAdClosed: ");
                                    setLoadingAnimation(false);

                                }

                                @Override
                                public void onRewarded(RewardItem rewardItem) {

                                    Log.d("TAG", "onRewarded: ");
                                    setLoadingAnimation(false);

                                }

                                @Override
                                public void onRewardedVideoAdLeftApplication() {

                                    Log.d("TAG", "onRewardedVideoAdLeftApplication: ");
                                    setLoadingAnimation(false);

                                }

                                @Override
                                public void onRewardedVideoAdFailedToLoad(int i) {

                                    Log.d("TAG", "onRewardedVideoAdFailedToLoad: "+i);
                                    setLoadingAnimation(false);


                                }

                                @Override
                                public void onRewardedVideoCompleted() {

                                    cons = Constants.delete_continue_dialog;
                                    Log.d("TAG", "onRewardedVideoCompleted: ");
                                    infoText.setText(mActivity.getResources().getString(R.string.ad_finished_info));
                                    headingText.setText(mActivity.getResources().getString(R.string.ad_finished_heading));
                                    mainButton.setText("Continue");
                                    deleteAnimation.setAnimation(R.raw.continue_delete_request_2);
                                    setLoadingAnimation(false);

                                }
                            });



                            mRewardedVideoAd.loadAd(Constants.testRewardedAdId, new AdRequest.Builder().build());

                        }

                        else if (cons.equals(Constants.delete_continue_dialog))
                        {
                            loadingAnimation.setAnimation(R.raw.sending_request);
                            loadingText.setText("Sending Your Delete Request\nPlease do not close the App...");
                            setLoadingAnimation(true);
                            sendDeleteRequest();

                        }

                        else if (cons.equals(Constants.delete_request_finished_dialog))
                        {
                            deleteAnimation.cancelAnimation();
                            deleteDialog.dismiss();

                        }

                        else if (cons.equals(Constants.countryCode_delete_dialog))
                        {
                            mCountryCodePicker.launchCountrySelectionDialog();
                            Log.d(TAG, "onClick: country code entered");
                        }

                        else if (cons.equals(Constants.premium_user_dialog))
                        {

                        }

                    }
                });

        mCountryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                if (cons.equals(Constants.countryCode_delete_dialog)) {

                    Log.d("TAG", "onCountrySelected: Selected Country code: " + mCountryCodePicker.getSelectedCountryCodeWithPlus());
                    loadingText.setText("Checking for Number on Vanish...");
                    loadingAnimation.setAnimation(R.raw.sending_request);
                    setLoadingAnimation(true);
                    actualNumber=mCountryCodePicker.getSelectedCountryCodeWithPlus()+actualNumber;
                    checkIfNumberExistsInDatabase();
                }
            }
        });

    }

    private void playLandingAnimation(View v)
    {
        YoYo.with(Techniques.Landing)
                .duration(1000)
                .repeat(0)
                .playOn(v);


        v.setVisibility(View.VISIBLE);

    }

    private void setLoadingAnimation(boolean show)
    {
        if(show) {
            mainButton.setVisibility(View.INVISIBLE);
            deleteAnimation.setVisibility(View.INVISIBLE);
            deleteAnimation.cancelAnimation();
            cancelButton.setVisibility(View.INVISIBLE);
            mainButton.setVisibility(View.INVISIBLE);
            headingText.setVisibility(View.INVISIBLE);
            infoText.setVisibility(View.INVISIBLE);
            phoneText.setVisibility(View.INVISIBLE);

            playLandingAnimation(loadingAnimation);
            loadingAnimation.setVisibility(View.VISIBLE);
            loadingAnimation.playAnimation();
            playLandingAnimation(loadingText);
            loadingText.setVisibility(View.VISIBLE);
        }
        else {
            loadingText.setVisibility(View.INVISIBLE);
            loadingAnimation.setVisibility(View.INVISIBLE);
            loadingAnimation.cancelAnimation();

            mainButton.setVisibility(View.VISIBLE);
            deleteAnimation.setVisibility(View.VISIBLE);
            deleteAnimation.playAnimation();
            cancelButton.setVisibility(View.VISIBLE);
            mainButton.setVisibility(View.VISIBLE);
            headingText.setVisibility(View.VISIBLE);
            infoText.setVisibility(View.VISIBLE);
            phoneText.setVisibility(View.VISIBLE);

        }



    }

    private void sendDeleteRequest() {
        final String yourUid = FirebaseAuth.getInstance().getUid();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        if (yourUid != null) {

            Log.d("TAG", "sendDeleteRequest: ccd: "+userCcd);

            mDatabase.child(uid).child(Constants.toDeleteNumList).push().setValue(new numberItemFirebase(yourUid, userNumber, userCcd))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            cons = Constants.delete_request_finished_dialog;

                            deleteAnimation.setAnimation(R.raw.success_delete_request_sent);
                            deleteAnimation.setRepeatCount(0);
                            headingText.setText(mActivity.getResources().getString(R.string.request_sent_heading));
                            infoText.setText(mActivity.getResources().getString(R.string.request_sent_info));


                            loadingText.setVisibility(View.INVISIBLE);
                            loadingAnimation.setVisibility(View.INVISIBLE);
                            loadingAnimation.cancelAnimation();

                            mainButton.setText("Close");
                            mainButton.setVisibility(View.VISIBLE);
                            deleteAnimation.setVisibility(View.VISIBLE);
                            deleteAnimation.playAnimation();
                            headingText.setVisibility(View.VISIBLE);
                            infoText.setVisibility(View.VISIBLE);
                            phoneText.setVisibility(View.GONE);

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d("TAG", "onFailure: " + e.getMessage());

                        }
                    });

        }
    }

    private void checkIfNumberExistsInOther()
    {
        final String yourUid = FirebaseAuth.getInstance().getUid();
        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (yourUid!=null)
        {
            Query phoneQuery = mDatabaseReference.child(uid).child(Constants.toDeleteNumList).orderByChild(Constants.number).equalTo(userNumber).limitToFirst(1);

            phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists())
                    {
                        boolean found = false;

                        for (DataSnapshot outputSnap : snapshot.getChildren()){

                            backgroundItem item = outputSnap.getValue(backgroundItem.class);

                            if (item!=null && item.getUid().equals(yourUid) && item.getCcc().equals(userCcd))
                            {
                                found = true;
                                Log.d("TAG", "onDataChange: Number found");
                                break;


                            }


                        }

                        if (found)
                        {
                            chooseLayoutIfNumberFound(true);

                        }
                        else {

                            chooseLayoutIfNumberFound(false);
                            Log.d("TAG", "onDataChange: Number not found");

                        }


                    }

                    else {

                        Log.d("TAG", "onDataChange: Snapshot does not exist checkIfNumberExistsInOther()");

                                        chooseLayoutIfNumberFound(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Log.d("TAG", "onCancelled: "+error.getMessage());

                }


            });

                }



        }

    private void chooseLayoutIfNumberFound(boolean pending)
    {
        if (pending)
        {
            deleteAnimation.setAnimation(R.raw.request_already_sent);
            mainButton.setVisibility(View.GONE);
            phoneText.setVisibility(View.GONE);
            cancelButton.setText("Close");
            headingText.setText(mActivity.getResources().getString(R.string.request_already_sent_heading));
            infoText.setText(mActivity.getResources().getString(R.string.request_already_sent_info));

        }

        else {

            deleteAnimation.setAnimation(R.raw.watch_ads);
            mainButton.setText("Watch Ad");
            phoneText.setText(actualNumber+" is a Confirmed Vanish User");
            playLandingAnimation(mainButton);
            headingText.setText(mActivity.getResources().getString(R.string.normal_delete_heading));
            infoText.setText(mActivity.getResources().getString(R.string.normal_delete_info));



        }



        playLandingAnimation(cancelButton);
        playLandingAnimation(headingText);
        playLandingAnimation(infoText);
        playLandingAnimation(deleteAnimation);
        deleteAnimation.playAnimation();

        loadingAnimation.setVisibility(View.INVISIBLE);
        loadingAnimation.cancelAnimation();


    }

    private void checkRecentPremiumAttempted()
    {
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(Constants.premium_numbers_prefs, "");
        if (json.isEmpty())
        {
            sendBlockedNotification();

        }
        else {

            Type type = new TypeToken<List<String>>() {}.getType();
            premiumNumbersList = gson.fromJson(json, type);

            if (!(checkPremiumNumberList(actualNumber)))
            {
                sendBlockedNotification();

            }

        }

    }

    private boolean checkPremiumNumberList(String number)
    {
        if (premiumNumbersList!=null) {

            for (int i = 0; i < premiumNumbersList.size(); i++) {

                Log.d("TAG", "checkPremiumNumberList: "+i+" Value: "+premiumNumbersList.get(i));
                if (premiumNumbersList.get(i).equals(number)) {
                    return true;
                }
            }
        }
        else {

            premiumNumbersList = new ArrayList<>();
        }

        return false;

    }

    private void sendBlockedNotification()
    {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference notifyReference = mDatabase.child(uid).child(Constants.notifications_realtime).push();
        final String pushedId = notifyReference.getKey();

        notifyReference.setValue(new NotificationManagerItem(Constants.blocked_success, "", System.currentTimeMillis()))

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        premiumNumbersList.add(actualNumber);

                        Gson gson = new Gson();
                        String json = gson.toJson(premiumNumbersList);
                        mEditor.putString(Constants.premium_numbers_prefs, json);
                        mEditor.apply();

                        mDatabase.child(uid).child(Constants.isNotify_realtime).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists())
                                {
                                    String origNotify = snapshot.getValue(String.class);
                                    String newNotify = origNotify+pushedId+",";
                                    Log.d("TAG", "onDataChange: New notification: "+newNotify);

                                    Map<String, Object> data = new HashMap<>();
                                    data.put(Constants.isNotify_realtime, newNotify);

                                    mDatabase.child(uid).updateChildren(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Log.d("TAG", "onSuccess: Blocked notification successfully sent ");

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Log.d("TAG", "onFailure: set ToNotify"+e.getMessage());

                                                }
                                            });
                                }
                                else {

                                    Log.d("TAG", "onDataChange: Snapshot does not exist");
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Log.d("TAG", "onCancelled: "+error.getMessage());

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("TAG", "onFailure: "+e.getMessage());

                    }
                });





    }

    private void checkIfNumberExistsInDatabase()
    {
            foundInDatabase = false;

            Query phoneQuery = mDatabaseReference.orderByChild(Constants.phone_realtime).equalTo(actualNumber).limitToFirst(1);

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

                                foundInDatabase = true;
                                Log.d("TAG", "onDataChange: Item not null in database ");

                                otherIsPremium = item.isPremium();
                                uid = item.getUid();

                                setLoadingAnimation(false);



                                if (otherIsPremium&&!userPremium)
                                {
                                    cons = Constants.premium_user_dialog;

                                    playLandingAnimation(mainButton);
                                    mainButton.setText("Go Premium!");

                                    phoneText.setText(actualNumber);

                                    playLandingAnimation(cancelButton);

                                    playLandingAnimation(headingText);
                                    headingText.setText(mActivity.getResources().getString(R.string.premium_delete_dialog_heading));
                                    playLandingAnimation(infoText);
                                    infoText.setText(mActivity.getResources().getString(R.string.premium_delete_dialog_info));

                                    checkRecentPremiumAttempted();
                                }
                                else {

                                    cons=Constants.normal_delete_dialog;

                                    mainButton.setVisibility(View.INVISIBLE);
                                    phoneText.setVisibility(View.INVISIBLE);
                                    cancelButton.setVisibility(View.INVISIBLE);
                                    headingText.setVisibility(View.INVISIBLE);
                                    infoText.setVisibility(View.INVISIBLE);
                                    deleteAnimation.setVisibility(View.INVISIBLE);

                                    mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(mActivity);

                                    loadingAnimation.setAnimation(R.raw.delete_button_loading);
                                    playLandingAnimation(loadingAnimation);
                                    loadingAnimation.playAnimation();

                                    checkIfNumberExistsInOther();

                                }

                                Log.d("TAG", "setUpCustomDialog: UID 2 - "+uid);
                                break;

                            }


                        }

                        if (!foundInDatabase)
                        {
                            Log.d("TAG", "onDataChange: Number does not exist in database ");
                            cons = Constants.not_found_delete_dialog;

                            mainButton.setText("Share");
                            phoneText.setText(actualNumber+" is not Registered on Vanish");
                            headingText.setText(mActivity.getResources().getString(R.string.notfound_delete_heading));
                            infoText.setText(mActivity.getResources().getString(R.string.notfound_delete_info));

                            setLoadingAnimation(false);
                        }
                    }

                    else {
                        Log.d("TAG", "onDataChange: Number does not exist in database ");
                        cons = Constants.not_found_delete_dialog;

                        mainButton.setText("Share");
                        phoneText.setText(actualNumber+" is not Registered on Vanish");
                        headingText.setText(mActivity.getResources().getString(R.string.notfound_delete_heading));
                        infoText.setText(mActivity.getResources().getString(R.string.notfound_delete_info));

                        setLoadingAnimation(false);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Log.d("TAG", "onCancelled: Error:"+error.toString());
                }
            });

        }
}
