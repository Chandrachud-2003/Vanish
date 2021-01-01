package com.chandrachud.vanish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.av.smoothviewpager.Smoolider.SmoothViewpager;
import com.chandrachud.vanish.adapters.MainDeletionsAdapter;
import com.chandrachud.vanish.adapters.MainNotificationsAdapter;
import com.chandrachud.vanish.adapters.SmooliderAdapter;
import com.chandrachud.vanish.helperClasses.BackgroundWorker;
import com.chandrachud.vanish.items.NotificationManagerItem;
import com.chandrachud.vanish.items.deletedNumberItem;
import com.chandrachud.vanish.items.smooliderItem;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;
import nl.joery.animatedbottombar.AnimatedBottomBar;

import static android.graphics.Typeface.BOLD;


public class MainActivity extends AppCompatActivity {

    private AnimatedBottomBar mAnimatedBottomBar;
    private NestedScrollView bottomSheetScrollView;
    private BottomSheetBehavior mBottomSheetBehavior;


    private SmoothBottomBar mSmoothBottomBar;

    private LottieAnimationView bottomLottie;
    private TextView bottomEmptyText;

    private int screenHeight;
    private int screenWidth;
    private int screenDensity;

    private LinearLayout alignDivider;

    private SmoothViewpager mSmoothViewpager;

    private String uid;
    private String prevActivity;

    private FirebaseUser mUser;

    private AlertDialog loadingDialog;

    private FirebaseFirestore db;

    private boolean isPremium=false;

    private String phoneNumber;
    private String ccd;

    private int numDeletedYouWeek=0;
    private int numDeletedYouTotal=0;
    private int numDeletedOthersWeek=0;
    private int numDeletedOthersTotal=0;
    private int numBlockedTotal=0;
    private int numBlockedWeek=0;
    private int numAttemptsTotal =0;
    private int numAttemptsWeek =0;

    private int[] profilePictures;

    private int profilePicNum=0;

    private String fullName ="";

    private TextView nameText;
    private ImageView picImageView;

    private NavigationTabStrip mStrip;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private RecyclerView mainBottomRecycler;

    private MainNotificationsAdapter mNotificationsAdapter;
    private MainDeletionsAdapter mDeletionsAdapter;

    private ArrayList<NotificationManagerItem> mNotificationManagerItems;
    private ArrayList<deletedNumberItem> mDeletedNumberItems;

    private boolean loadingDeletions;
    private boolean loadingNotifications;

    private LinearLayoutManager verticalLayout;

    private DatabaseReference mDatabaseReference;

    private int[] profilePics = Constants.profilePics_medium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.mainBackground));// set status background white

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }


        //Intent intent = getIntent();
        //prevActivity = intent.getStringExtra(Constants.activity_Intent);


        // hideSystemUI();

        assignScreenSize();
        findViewsById();
        setListeners();



    }

    private void assignScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = (int) (displayMetrics.heightPixels);
        screenWidth = (int) (displayMetrics.widthPixels);
        screenDensity = (int) displayMetrics.density;
    }

    private void findViewsById() {


        mAnimatedBottomBar = findViewById(R.id.bottom_bar);

        mainBottomRecycler = findViewById(R.id.mainBottomRecycler);

        //Bottom Sheet
        bottomSheetScrollView = findViewById(R.id.bottomSheetScroll);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetScrollView);
        final int peekHeight = (int) ((screenHeight * 2) / 5);
        final int minHeight = (int) (screenHeight*0.6);

        mBottomSheetBehavior.setPeekHeight(peekHeight);

        bottomSheetScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    bottomSheetScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    bottomSheetScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                bottomSheetScrollView.setMinimumHeight(minHeight);


            }
        });


        alignDivider = findViewById(R.id.alignMainDivider);
        final MarginLayoutParams params = (MarginLayoutParams) alignDivider.getLayoutParams();


        ViewTreeObserver vto = mAnimatedBottomBar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mAnimatedBottomBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = mAnimatedBottomBar.getMeasuredHeight();
                params.bottomMargin = peekHeight - height;
                alignDivider.setLayoutParams(params);

            }
        });

        nameText = findViewById(R.id.fullNameText);
        picImageView = findViewById(R.id.profilePicture);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int screen_width = displayMetrics.widthPixels;

        picImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    picImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    picImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }




                int newHeight = (int) (screen_width*0.17);
                picImageView.getLayoutParams().width = newHeight;

                picImageView.getLayoutParams().height = newHeight;



            }
        });


        mSmoothBottomBar = findViewById(R.id.bubbleBottomSheetBar);
        mSmoothBottomBar.setItemFontFamily(R.font.drugs);

        mSmoothBottomBar.setClickable(false);

        bottomLottie = findViewById(R.id.bottomSheetLottie);
        bottomEmptyText = findViewById(R.id.bottomSheetDefaultText);

        mSmoothViewpager = findViewById(R.id.smooliderLayout);

        mStrip = findViewById(R.id.mainFilterNavigation);
        mStrip.setTabIndex(0, true);

        profilePictures = Constants.profilePics;

        loadingDialog = new SpotsDialog.Builder().setContext(MainActivity.this).build();
        loadingDialog.setTitle("Verifying Your Account");
        loadingDialog.setMessage("Please Wait...");
        loadingDialog.show();

        db = FirebaseFirestore.getInstance();

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mSharedPreferences = getSharedPreferences(Constants.shared_prefs, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mDeletedNumberItems = new ArrayList<>();
        mNotificationManagerItems = new ArrayList<>();
        loadingDeletions = true;
        loadingNotifications = true;
        Context context;
        verticalLayout = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);

        mainBottomRecycler.setLayoutManager(verticalLayout);

        if (mUser == null) {

            showFailedLoginDialog(true);

        } else {

            uid = mUser.getUid();

            getDatabaseInitialInfo(uid);
            getNotifications(uid);

            String uuid_string = mSharedPreferences.getString(Constants.workManager_UUID, null);
            if (uuid_string != null) {

                UUID uuid = UUID.fromString(uuid_string);

                if (uuid != null) {


                    WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(uuid)
                            .observe(this, new Observer<WorkInfo>() {
                                @Override
                                public void onChanged(@Nullable WorkInfo workInfo) {
                                    if (workInfo != null && (workInfo.getState() == WorkInfo.State.ENQUEUED || workInfo.getState() == WorkInfo.State.RUNNING)) {
                                        Log.d("TAG", "onChanged: WorkManager already registered in Android System");
                                    } else {

                                        createNewWorkManager();
                                    }
                                }
                            });

                } else {

                    createNewWorkManager();
                }


            }

            else {

                createNewWorkManager();
            }
        }



    }

    private void createNewWorkManager()
    {
        PeriodicWorkRequest.Builder periodicWorkRequest =
                new PeriodicWorkRequest.Builder(BackgroundWorker.class, 15,
                        TimeUnit.MINUTES);

        PeriodicWorkRequest periodicWork = periodicWorkRequest.build();
        WorkManager instance = WorkManager.getInstance();

        instance.enqueueUniquePeriodicWork(Constants.workManager_ID, ExistingPeriodicWorkPolicy.REPLACE , periodicWork);

        mEditor.putString(Constants.workManager_UUID, periodicWork.getId().toString());
        mEditor.apply();



    }

    private void setListeners() {
        mAnimatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {

                if (i1!=0)
                {
                    switch (i1)
                    {
                        case 1:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        }

                        case 2:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(MainActivity.this, ProfileClass.class);
                            startActivity(intent);
                            finish();
                            break;
                        }

                        case 3:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(MainActivity.this, SettingsClass.class);
                            startActivity(intent);
                            finish();
                            break;
                        }

                    }
                }

            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });


        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int state) {

                if (state == BottomSheetBehavior.STATE_DRAGGING) {
                    changeViewsVisibilityOnSliding(false);
                } else if (state == BottomSheetBehavior.STATE_EXPANDED || state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    changeViewsVisibilityOnSliding(false);
                } else if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    changeViewsVisibilityOnSliding(true);

                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        mSmoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                if (i == 0) {
                    if (mNotificationManagerItems!=null && mNotificationManagerItems.size()>0)
                    {
                        bottomEmptyText.setVisibility(View.GONE);
                        mainBottomRecycler.setVisibility(View.VISIBLE);
                        setBottomNotificationsData(false);

                    }
                    else {
                        mainBottomRecycler.setVisibility(View.GONE);
                        setBottomDefaultNotifications(true);
                    }
                } else {
                    if (mDeletedNumberItems!=null && mDeletedNumberItems.size()>0)
                    {
                        bottomEmptyText.setVisibility(View.GONE);
                        mainBottomRecycler.setVisibility(View.VISIBLE);
                        setBottomDeletionsData(false);
                    }
                    else {
                        mainBottomRecycler.setVisibility(View.GONE);
                        setBottomDefaultDeletions(true);
                    }
                }

                return false;
            }
        });

        mStrip.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {



            }

            @Override
            public void onEndTabSelected(String title, int index) {

                if (index==0)
                {
                    setSmooliderDataWithDefinedData(true);
                }
                else if(index==1) {

                    setSmooliderDataWithDefinedData(false);

                }



            }
        });


    }

    private void setSmooliderData(int yDeletions, int extDeletions, boolean premium, String deleteAttempts, String blockedDeletions, boolean dismissDialog) {
        ArrayList<smooliderItem> smooliderItems = new ArrayList<>();
        smooliderItems.add(new smooliderItem("Your Deletions", R.raw.delete_main_slider, yDeletions, "Numbers", getResources().getString(R.string.smoolider_1)));
        smooliderItems.add(new smooliderItem("External Deletions", R.raw.deleted_from_you, extDeletions, "Numbers", getResources().getString(R.string.smoolider_2)));

        if (premium) {
            int attempts = 0;
            int blocked = 0;
            if (!deleteAttempts.isEmpty()) {
                attempts = Integer.parseInt(deleteAttempts);
            }
            if (!blockedDeletions.isEmpty()) {
                blocked = Integer.parseInt(blockedDeletions);
            }


            smooliderItems.add(new smooliderItem("Delete Attempts", R.raw.delete_attempts, attempts, "Attempts", getResources().getString(R.string.smoolider_3)));
            smooliderItems.add(new smooliderItem("Blocked Deletions", R.raw.secure_smoolider, blocked, "Blocks", getResources().getString(R.string.smoolider_4)));
        }

        if(dismissDialog)
        {
            loadingDialog.dismiss();
        }

        mSmoothViewpager.setAdapter(new SmooliderAdapter(smooliderItems, MainActivity.this, screenHeight));


    }

    private void setBottomDefaultNotifications(boolean which) {
        if (which)
        {
            bottomLottie.cancelAnimation();
            bottomLottie.setVisibility(View.VISIBLE);
            bottomLottie.setScaleType(LottieAnimationView.ScaleType.FIT_CENTER);
            bottomEmptyText.setVisibility(View.VISIBLE);
            bottomEmptyText.setText(getResources().getString(R.string.bottom_default_notifications));
            bottomLottie.setAnimation(R.raw.no_notifications);
            bottomLottie.setProgress(0);
            bottomLottie.playAnimation();

        } else {

            bottomLottie.cancelAnimation();
            bottomLottie.setVisibility(View.GONE);
            bottomEmptyText.setVisibility(View.GONE);

        }
    }

    private void setBottomDefaultDeletions(boolean which) {
        if (which) {
            bottomLottie.cancelAnimation();
            bottomLottie.setVisibility(View.VISIBLE);
            bottomLottie.setScaleType(LottieAnimationView.ScaleType.FIT_CENTER);
            bottomEmptyText.setVisibility(View.VISIBLE);
            bottomEmptyText.setText(getResources().getString(R.string.bottom_default_deleted));
            bottomLottie.setAnimation(R.raw.deletion_empty);
            bottomLottie.setProgress(0);
            bottomLottie.playAnimation();

        } else {

            bottomLottie.cancelAnimation();
            bottomLottie.setVisibility(View.GONE);
            bottomEmptyText.setVisibility(View.GONE);

        }
    }

    private void getDatabaseInitialInfo(String uid) {

        final DocumentReference docRef = db.collection(Constants.users_fire).document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {

                        fullName = document.getString(Constants.name_fire);
                        mEditor.putString(Constants.name_prefs, fullName);
                        profilePicNum = document.getLong(Constants.profilePicNum_fire).intValue();
                        mEditor.putInt(Constants.profilePicNum_prefs, profilePicNum);
                        numDeletedYouTotal = document.getLong(Constants.deletedNumCountTotal_fire).intValue();
                        numDeletedYouWeek = document.getLong(Constants.deletedNumCountWeek_fire).intValue();
                        numDeletedOthersTotal = document.getLong(Constants.othersDeletedCountTotal_fire).intValue();
                        numDeletedOthersWeek = document.getLong(Constants.othersDeletedCountWeek_fire).intValue();
                        isPremium = document.getBoolean(Constants.premium_fire);
                        mEditor.putBoolean(Constants.premium_prefs, isPremium);
                        phoneNumber = document.getString(Constants.onlyPhoneNum_fire);
                        ccd = document.getString(Constants.onlyCCode_fire);
                        mEditor.putString(Constants.phone_prefs, phoneNumber);
                        mEditor.putString(Constants.ccc_prefs, ccd);
                        String email = document.getString(Constants.email_fire);
                        mEditor.putString(Constants.email_prefs, email);
                        mEditor.apply();
                        mDeletedNumberItems = (ArrayList<deletedNumberItem>) document.get(Constants.deletedNumList_fire);
                        if (isPremium) {
                            numBlockedWeek = document.getLong(Constants.blockedDeletionsWeek_fire).intValue();
                            numAttemptsWeek = document.getLong(Constants.deleteAttemptsWeek_fire).intValue();
                            numAttemptsTotal = document.getLong(Constants.deleteAttemptsTotal_fire).intValue();
                            numBlockedTotal = document.getLong(Constants.blockedDeletionsTotal_fire).intValue();

                            setSmooliderData(numDeletedYouWeek, numDeletedOthersWeek, isPremium, String.valueOf(numAttemptsWeek), String.valueOf(numBlockedWeek), true);
                        } else {
                            setSmooliderData(numDeletedYouWeek, numDeletedOthersWeek, isPremium, "", "", true);

                        }

                        setProfilePicAndName();

                        loadingDeletions = false;

                        if(!loadingNotifications)
                        {
                            mAnimatedBottomBar.setClickable(true);

                        }


                    } else {
                        Log.d("TAG", "No such document");
                        showFailedLoginDialog(true);

                    }

                } else {

                    showFailedLoginDialog(false);
                }

            }
        });

    }

    private void showFailedLoginDialog(boolean notExist)
    {
        if(true)
        {
            FirebaseAuth.getInstance().signOut();

        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Account Retrieval Failed");
        builder.setMessage("Something went wrong while setting up your account, please Sign-In Again");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(MainActivity.this, ChooseSignup.class);


                dialogInterface.dismiss();
                startActivity(intent);


            }
        });

        AlertDialog alertDialog = builder.create();

        loadingDialog.dismiss();

        alertDialog.show();
    }

    private void setProfilePicAndName()
    {
        nameText.setText(fullName);

        if (profilePicNum<profilePictures.length && profilePicNum>=0)
        {
            picImageView.setImageResource(profilePictures[profilePicNum]);
        }

    }

    private void setSmooliderDataWithDefinedData(boolean type) {
        ArrayList<smooliderItem> smooliderItems = new ArrayList<>();

        //type = true, then display week
        //type = false, then display total

        if (type) {
            smooliderItems.add(new smooliderItem("Your Deletions", R.raw.delete_main_slider, numDeletedYouWeek, "Numbers", getResources().getString(R.string.smoolider_1)));
            smooliderItems.add(new smooliderItem("External Deletions", R.raw.deleted_from_you, numDeletedOthersWeek, "Numbers", getResources().getString(R.string.smoolider_2)));

            if (isPremium) {
                smooliderItems.add(new smooliderItem("Delete Attempts", R.raw.delete_attempts, numAttemptsWeek, "Attempts", getResources().getString(R.string.smoolider_3)));
                smooliderItems.add(new smooliderItem("Blocked Deletions", R.raw.secure_smoolider, numBlockedWeek, "Blocks", getResources().getString(R.string.smoolider_4)));
            }
        }

        else {

            smooliderItems.add(new smooliderItem("Your Deletions", R.raw.delete_main_slider, numDeletedYouTotal, "Numbers", getResources().getString(R.string.smoolider_1)));
            smooliderItems.add(new smooliderItem("External Deletions", R.raw.deleted_from_you, numDeletedOthersTotal, "Numbers", getResources().getString(R.string.smoolider_2)));

            if (isPremium) {
                smooliderItems.add(new smooliderItem("Delete Attempts", R.raw.delete_attempts, numAttemptsTotal, "Attempts", getResources().getString(R.string.smoolider_3)));
                smooliderItems.add(new smooliderItem("Blocked Deletions", R.raw.secure_smoolider, numBlockedTotal, "Blocks", getResources().getString(R.string.smoolider_4)));
            }

        }

        mSmoothViewpager.setAdapter(new SmooliderAdapter(smooliderItems, MainActivity.this, screenHeight));


    }


    private void changeViewsVisibilityOnSliding(boolean visible)
    {
        int visibility = View.VISIBLE;
        if (visible)
        {
            visibility = View.VISIBLE;
        }
        else {
            visibility = View.INVISIBLE;
        }

        nameText.setVisibility(visibility);
        mStrip.setVisibility(visibility);
        mSmoothViewpager.setVisibility(visibility);
        picImageView.setVisibility(visibility);





    }

    private void getNotifications(String uid)
    {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Query notifyQuery = mDatabaseReference.child(uid).child(Constants.notifications_realtime).orderByChild(Constants.timestamp_fire).limitToLast(5);

        notifyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {

                    for (DataSnapshot notifySnapshot : snapshot.getChildren())
                    {

                        NotificationManagerItem item = notifySnapshot.getValue(NotificationManagerItem.class);

                        if(item!=null)
                        {
                            mNotificationManagerItems.add(item);

                        }

                        loadingNotifications = false;



                        setBottomNotificationsData(true);

                        if(!loadingDeletions)
                        {
                            mAnimatedBottomBar.setClickable(true);
                        }
                    }

                }
                else {
                    Log.d("TAG", "onDataChange: Notifications snapshot does not exist");

                    setBottomDefaultNotifications(true);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("TAG", "onCancelled: "+error.getMessage());

                setBottomDefaultNotifications(true);



            }
        });

    }

    private void setBottomDeletionsData(boolean first)
    {
        if(first) {
            mDeletionsAdapter = new MainDeletionsAdapter(MainActivity.this, screenWidth, mDeletedNumberItems, profilePics);
            mainBottomRecycler.setAdapter(mDeletionsAdapter);
            setBottomDefaultDeletions(false);
        }
        else {
            mainBottomRecycler.swapAdapter(mDeletionsAdapter, true);
        }

        bottomLottie.setVisibility(View.GONE);
        bottomLottie.cancelAnimation();



    }

    private void setBottomNotificationsData(boolean first)
    {
        if (first)
        {
            mNotificationsAdapter = new MainNotificationsAdapter(MainActivity.this,screenWidth, mNotificationManagerItems);
            mainBottomRecycler.setAdapter(mNotificationsAdapter);
            setBottomDefaultNotifications(false);

        }
        else {
            mainBottomRecycler.swapAdapter(mNotificationsAdapter, true);
        }

        bottomLottie.setVisibility(View.GONE);
        bottomLottie.cancelAnimation();
    }



}
