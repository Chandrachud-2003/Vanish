package com.chandrachud.vanish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.chandrachud.vanish.adapters.MainDeletionsAdapter;
import com.chandrachud.vanish.adapters.MainNotificationsAdapter;
import com.chandrachud.vanish.adapters.ProfileDeletionsAdapter;
import com.chandrachud.vanish.adapters.ProfileNotificationsAdapter;
import com.chandrachud.vanish.items.NotificationManagerItem;
import com.chandrachud.vanish.items.deletedNumberItem;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import me.ibrahimsn.lib.SmoothBottomBar;
import nl.joery.animatedbottombar.AnimatedBottomBar;

public class ProfileClass extends AppCompatActivity {

    private TextView nameText;
    private TextView phoneText;
    private TextView emailText;
    private LottieAnimationView profilePicAnimation;
    private ImageView profilePic;
    private NavigationTabStrip mStrip;
    private AnimatedBottomBar mAnimatedBottomBar;
    private ImageButton signOutButton;
    private ImageButton editButton;
    private LottieAnimationView profileLoadingAnimation;
    private TextView profileLoadingText;
    private NestedScrollView bottomSheetScrollView;
    private BottomSheetBehavior mBottomSheetBehavior;
    private RecyclerView profileRecycler;
    private LottieAnimationView emptyAnim;
    private TextView emptyText;

    private String emptyNotifications = "It looks like your Notifications are Empty";
    private String emptyDeletions = "It looks like you haven't Deleted your Number from anyone. Send a delete request to fill this up!";

    private ConstraintLayout profileConstraintLayout;

    private boolean loadingDeletions;
    private boolean loadingNotifications;
    private ArrayList<NotificationManagerItem> mNotificationManagerItems;
    private ArrayList<deletedNumberItem> mDeletedNumberItems;

    private LottieAnimationView topLoading;

    private int[] profilePics = Constants.profilePics_medium;

    private LinearLayoutManager verticalLayout;
    private ProfileDeletionsAdapter mDeletionsAdapter;
    private ProfileNotificationsAdapter mNotificationsAdapter;

    private String uid;
    private FirebaseFirestore db;
    private DatabaseReference mDatabaseReference;

    private String name;
    private String phone;
    private String ccd;
    private boolean isPremium;
    private int profilePicNum;
    private String email;

    private int screen_width;
    private int screen_height;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_class);

        uid = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();


        findViewsById();
        setOnClickListeners();
        getInfoFromFirestore();
        getNotifications();
    }


    private void findViewsById()
    {
        mSharedPreferences = getSharedPreferences(Constants.shared_prefs, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_width = displayMetrics.widthPixels;
        screen_height = displayMetrics.heightPixels;

        nameText = findViewById(R.id.nameText);
        phoneText = findViewById(R.id.phoneText);
        emailText = findViewById(R.id.emailText);
        profilePicAnimation = findViewById(R.id.profilePictureLoading);
        profilePicAnimation.setVisibility(View.GONE);
        profilePic = findViewById(R.id.profileImage);
        mStrip = findViewById(R.id.profileNavigation);
        mStrip.setTabIndex(0, true);
        mStrip.setVisibility(View.GONE);
        mStrip.setClickable(false);
        mAnimatedBottomBar = findViewById(R.id.bottom_bar);
        mAnimatedBottomBar.selectTabAt(2, true);
        signOutButton = findViewById(R.id.signOutButton);
        editButton = findViewById(R.id.editButton);
        profileConstraintLayout = findViewById(R.id.profileConstraintLayout);

        bottomSheetScrollView = findViewById(R.id.profileBottomSheetScrollView);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetScrollView);

        profileLoadingAnimation = findViewById(R.id.profileBottomLoadingAnimation);
        profileLoadingText = findViewById(R.id.profileLoadingText);

        emptyAnim = findViewById(R.id.emptyProfileAnim);
        emptyText = findViewById(R.id.emptyText);
        topLoading = findViewById(R.id.topLoadingAnim);

        profileRecycler = findViewById(R.id.profileRecycler);

        loadingDeletions = true;
        loadingNotifications = true;
        mNotificationManagerItems = new ArrayList<>();
        mDeletedNumberItems = new ArrayList<>();

        verticalLayout = new LinearLayoutManager(ProfileClass.this, LinearLayoutManager.VERTICAL, false);
        profileRecycler.setLayoutManager(verticalLayout);

        profilePicAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    profilePicAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    profilePicAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                 int layoutHeight = profileConstraintLayout.getHeight();
                final int peekHeight = screen_height - layoutHeight;

                mBottomSheetBehavior.setPeekHeight(peekHeight);

                bottomSheetScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            bottomSheetScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            bottomSheetScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                        bottomSheetScrollView.setMinimumHeight(peekHeight);




                    }
                });



            }
        });


        profilePicAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    profilePicAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    profilePicAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int newDimensions = (int) (screen_width*0.389);

                profilePicAnimation.getLayoutParams().width = newDimensions;
                profilePicAnimation.getLayoutParams().height = newDimensions;

                profilePicAnimation.playAnimation();

            }
        });

        topLoading.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    topLoading.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    topLoading.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int newDimensions = (int) (screen_width*0.3125);

                topLoading.getLayoutParams().width = newDimensions;
                topLoading.getLayoutParams().height = newDimensions;


            }
        });

        emptyAnim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    emptyAnim.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    emptyAnim.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int newDimensions = (int) (screen_width*0.479);

                emptyAnim.getLayoutParams().width = newDimensions;
                emptyAnim.getLayoutParams().height = newDimensions;

            }
        });


        profileLoadingAnimation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    profileLoadingAnimation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    profileLoadingAnimation.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int newDimensions = (int) (screen_width*0.417);

                profileLoadingAnimation.getLayoutParams().width = newDimensions;
                profileLoadingAnimation.getLayoutParams().height = newDimensions;


            }
        });

    }

    private void setOnClickListeners()
    {
        PushDownAnim.setPushDownAnimTo(signOutButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {




                    }
                });

        PushDownAnim.setPushDownAnimTo(editButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(ProfileClass.this, EditProfile.class);
                        intent.putExtra(Constants.name_Intent, name);
                        intent.putExtra(Constants.profile_num_Intent, profilePicNum);
                        intent.putExtra(Constants.email_Intent, email);
                        intent.putExtra(Constants.phone_Intent, ccd+phone);
                        startActivity(intent);
                        finish();
                    }
                });


        mAnimatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {

                if (i1!=2)
                {
                    switch (i1)
                    {
                        case 0:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(ProfileClass.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        }

                        case 1:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(ProfileClass.this, ContactActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        }

                        case 3:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(ProfileClass.this, SettingsClass.class);
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

        mStrip.setOnTabStripSelectedIndexListener(new NavigationTabStrip.OnTabStripSelectedIndexListener() {
            @Override
            public void onStartTabSelected(String title, int index) {

            }

            @Override
            public void onEndTabSelected(String title, int index) {

                if (index==0)
                {
                    if (mNotificationManagerItems!=null && mNotificationManagerItems.size()>0)
                    {
                        setBottomDefaultNotifications(false);
                        profileRecycler.setVisibility(View.VISIBLE);
                        setBottomNotificationsData(false);

                    }
                    else {
                        profileRecycler.setVisibility(View.GONE);
                        setBottomDefaultNotifications(true);
                    }
                }
                else if (index==1)
                {
                    if (mDeletedNumberItems!=null && mDeletedNumberItems.size()>0)
                    {
                        setBottomDefaultDeletions(false);
                        profileRecycler.setVisibility(View.VISIBLE);
                        setBottomDeletionsData();
                    }
                    else {
                        profileRecycler.setVisibility(View.GONE);
                        setBottomDefaultDeletions(true);
                    }
                }

            }
        });




    }

    private void getInfoFromFirestore()
    {
        final DocumentReference docRef = db.collection(Constants.users_fire).document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists())
                    {
                        name = document.getString(Constants.name_fire);
                        mEditor.putString(Constants.name_prefs, name);

                        profilePicNum = document.getLong(Constants.profilePicNum_fire).intValue();
                        mEditor.putInt(Constants.profilePicNum_prefs, profilePicNum);
                        if (profilePicNum<0)
                        {
                            profilePicNum = 0;
                        }
                        isPremium = document.getBoolean(Constants.premium_fire);
                        mEditor.putBoolean(Constants.premium_prefs, isPremium);
                        phone = document.getString(Constants.onlyPhoneNum_fire);
                        ccd = document.getString(Constants.onlyCCode_fire);
                        mEditor.putString(Constants.phone_prefs, phone);
                        mEditor.putString(Constants.ccc_prefs, ccd);
                        email = document.getString(Constants.email_fire);
                        mEditor.putString(Constants.email_prefs, email);
                        mEditor.apply();
                        mDeletedNumberItems = (ArrayList<deletedNumberItem>) document.get(Constants.deletedNumList_fire);

                        setTopInfo();

                        loadingDeletions = false;

                        if(!loadingNotifications)
                        {
                            mStrip.setClickable(true);
                        }


                    }

                    else {
                        loadingDeletions = false;
                        Toast.makeText(ProfileClass.this, "Failed to get Profile Info", Toast.LENGTH_LONG).show();

                    }

                }

                else {

                    loadingDeletions = false;
                    Toast.makeText(ProfileClass.this, "Failed to get Profile Info", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void getNotifications()
    {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Query notifyQuery = mDatabaseReference.child(uid).child(Constants.notifications_realtime).orderByChild(Constants.timestamp_fire);

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

                        mStrip.setVisibility(View.VISIBLE);

                        if(!loadingDeletions)
                        {
                            mStrip.setClickable(true);
                        }
                    }

                }
                else {
                    Log.d("TAG", "onDataChange: Notifications snapshot does not exist");

                    mStrip.setVisibility(View.VISIBLE);

                    profileRecycler.setVisibility(View.GONE);
                    loadingNotifications = false;
                    profileLoadingAnimation.setVisibility(View.GONE);
                    profileLoadingAnimation.cancelAnimation();
                    profileLoadingText.setVisibility(View.GONE);
                    setBottomDefaultNotifications(true);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("TAG", "onCancelled: "+error.getMessage());

                mStrip.setVisibility(View.VISIBLE);

                profileRecycler.setVisibility(View.GONE);
                loadingNotifications = false;
                profileLoadingAnimation.setVisibility(View.GONE);
                profileLoadingAnimation.cancelAnimation();
                profileLoadingText.setVisibility(View.GONE);
               setBottomDefaultNotifications(true);

            }
        });

    }

    private void setTopInfo()
    {
        topLoading.setVisibility(View.GONE);
        topLoading.cancelAnimation();

        playAnimationOnView(profilePic, Techniques.Landing);
        profilePic.setVisibility(View.VISIBLE);
        profilePic.setImageResource(profilePics[profilePicNum]);
        profilePicAnimation.setVisibility(View.VISIBLE);
        profilePicAnimation.playAnimation();
        playAnimationOnView(nameText, Techniques.Landing);
        nameText.setVisibility(View.VISIBLE);
        nameText.setText(name);
        playAnimationOnView(phoneText, Techniques.Landing);
        phoneText.setVisibility(View.VISIBLE);
        phoneText.setText(ccd+" "+phone);
        playAnimationOnView(emailText, Techniques.Landing);
        emailText.setVisibility(View.VISIBLE);
        emailText.setText(email);
        playAnimationOnView(signOutButton, Techniques.Landing);
        playAnimationOnView(editButton, Techniques.Landing);

    }

    private void playAnimationOnView(View v, Techniques animation)
    {
        YoYo.with(animation)
                .duration(1500)
                .repeat(0)
                .playOn(v);

        if (v.getVisibility()==View.INVISIBLE || v.getVisibility()==View.GONE)
        {
            v.setVisibility(View.VISIBLE);
        }




    }

    private void setBottomNotificationsData(boolean first)
    {
        if (first)
        {
            mNotificationsAdapter = new ProfileNotificationsAdapter(ProfileClass.this,screen_width, mNotificationManagerItems);
            profileRecycler.setAdapter(mNotificationsAdapter);
            profileLoadingAnimation.setVisibility(View.GONE);
            profileLoadingAnimation.cancelAnimation();
            profileLoadingText.setVisibility(View.GONE);


        }
        else {
            profileRecycler.swapAdapter(mNotificationsAdapter, true);
        }


    }

    private void setBottomDeletionsData()
    {

        profileRecycler.swapAdapter(mDeletionsAdapter, true);
    }

    private void setBottomDefaultNotifications(boolean show)
    {
        if (!show)
        {
            emptyAnim.setVisibility(View.GONE);
            emptyAnim.cancelAnimation();
            emailText.setVisibility(View.GONE);
        }
        else {
            emptyAnim.setVisibility(View.VISIBLE);
            emptyAnim.setAnimation(R.raw.no_notifications_profile);
            emptyAnim.playAnimation();
            emptyText.setText(emptyNotifications);
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    private void setBottomDefaultDeletions(boolean show)
    {
        if (!show)
        {
            emptyAnim.setVisibility(View.GONE);
            emptyAnim.cancelAnimation();
            emailText.setVisibility(View.GONE);
        }
        else {
            emptyAnim.setVisibility(View.VISIBLE);
            emptyAnim.setAnimation(R.raw.no_deletions_profile);
            emptyAnim.playAnimation();
            emptyText.setText(emptyDeletions);
            emptyText.setVisibility(View.VISIBLE);
        }
    }

}