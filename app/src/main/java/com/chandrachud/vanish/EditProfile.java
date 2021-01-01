package com.chandrachud.vanish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
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
import com.chandrachud.vanish.fragments.BottomSheetProfileFragment;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.goodiebag.carouselpicker.CarouselPicker;

public class EditProfile extends AppCompatActivity implements BottomSheetProfileFragment.ButtonClickListener {

    private int screenWidth;
    private ImageButton backButton;
    private ImageView profileImage;
    private ImageView editProfileImage;

    private int[] profileImgs = Constants.profilePics;

    private CardView updateButton;
    private TextInputEditText nameText;
    private TextInputEditText phoneText;
    private TextInputEditText emailText;
    private LottieAnimationView updateAnim;
    private TextView updateText;

    private String origName;
    private String origPhone;
    private String origEmail;
    private int origProfileNum;

    private FirebaseFirestore db;
    private DatabaseReference mDatabase;

    private String uid;

    private int newProfilePicNum;

    private int changedProfilePicNum;

    private BottomSheetProfileFragment mProfileFragment;
    private ArrayList<CarouselPicker.PickerItem> mPickerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getIntentInfo();
        assignScreenSize();
        findViewsById();
        setOnClickListeners();
    }

    private void assignScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = (int) (displayMetrics.widthPixels);
    }

    private void findViewsById()
    {
        backButton = findViewById(R.id.backButton);
        profileImage = findViewById(R.id.profileImage);
        updateButton = findViewById(R.id.updateButton);
        editProfileImage = findViewById(R.id.editProfilePicImg);

        nameText = findViewById(R.id.nameInputEditText);
        playAnimationOnView(nameText, Techniques.Landing);
        nameText.setText(origName);
        nameText.setVisibility(View.VISIBLE);

        phoneText = findViewById(R.id.phoneInputEditText);
        phoneText.setEnabled(false);
        playAnimationOnView(phoneText, Techniques.Landing);
        phoneText.setText(origPhone);
        phoneText.setVisibility(View.VISIBLE);

        emailText = findViewById(R.id.emailInputEditText);
        emailText.setEnabled(false);
        playAnimationOnView(emailText, Techniques.Landing);
        emailText.setText(origEmail);
        emailText.setVisibility(View.VISIBLE);

        updateAnim = findViewById(R.id.updateButtonAnim);
        updateText = findViewById(R.id.updateText);

        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        newProfilePicNum = origProfileNum;

        mPickerItems = new ArrayList<>();

        for(int i=0;i<profileImgs.length;i++)
        {
            mPickerItems.add(new CarouselPicker.DrawableItem(profileImgs[i]));
        }
        mProfileFragment = new BottomSheetProfileFragment(screenWidth, EditProfile.this, mPickerItems);


        backButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    backButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    backButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int newHeight = (int) (screenWidth*0.0729);
                backButton.getLayoutParams().width = newHeight;

                backButton.getLayoutParams().height = newHeight;

                playAnimationOnView(backButton, Techniques.Landing);



            }
        });

        profileImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    profileImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    profileImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int newHeight = (int) (screenWidth*0.2292);
                profileImage.getLayoutParams().width = newHeight;

                profileImage.getLayoutParams().height = newHeight;

                playAnimationOnView(profileImage, Techniques.Landing);
                profileImage.setImageResource(profileImgs[origProfileNum]);
                profileImage.setVisibility(View.VISIBLE);


            }
        });

        editProfileImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    editProfileImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    editProfileImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int newHeight = (int) (screenWidth*0.0625);
                editProfileImage.getLayoutParams().width = newHeight;

                editProfileImage.getLayoutParams().height = newHeight;

            }
        });

        updateAnim.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    updateAnim.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    updateAnim.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int newHeight = (int) (screenWidth*0.1458);
                updateAnim.getLayoutParams().width = newHeight;

                updateAnim.getLayoutParams().height = newHeight;

            }
        });




    }

    private void getIntentInfo()
    {
        Intent intent = getIntent();
        uid = FirebaseAuth.getInstance().getUid();
        if (intent != null && uid != null)
        {
            origName = intent.getStringExtra(Constants.name_Intent);
            origPhone = intent.getStringExtra(Constants.phone_Intent);
            origEmail = intent.getStringExtra(Constants.email_Intent);
            origProfileNum = intent.getIntExtra(Constants.profile_num_Intent, 0);
        }

        else {

            Toast.makeText(EditProfile.this, "Error retrieving Profile Info", Toast.LENGTH_LONG).show();
            Intent backIntent = new Intent(EditProfile.this, ProfileClass.class);
            startActivity(backIntent);
            finish();

        }
    }

    private void setOnClickListeners()
    {
        PushDownAnim.setPushDownAnimTo(backButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(EditProfile.this, ProfileClass.class);
                        startActivity(intent);
                        finish();

                    }
                });

        PushDownAnim.setPushDownAnimTo(updateButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        boolean profilePicChanged = false;

                        Map<String, Object> data = new HashMap<>();

                        updateText.setVisibility(View.GONE);
                        updateAnim.setVisibility(View.VISIBLE);
                        updateAnim.playAnimation();

                        boolean emptyName = false;

                        if (nameText.getText().toString() != null && !(nameText.getText().toString().isEmpty()))
                        {
                            if (!(nameText.getText().toString().equals(origName)))
                            {
                                data.put(Constants.name_fire, nameText.getText().toString());
                            }
                        }

                        else
                        {
                            Toast.makeText(EditProfile.this, "Name Field can't be empty", Toast.LENGTH_SHORT).show();
                            updateAnim.setVisibility(View.GONE);
                            updateAnim.cancelAnimation();
                            updateText.setVisibility(View.VISIBLE);
                            emptyName = true;
                        }

                        if (emailText.getText().toString() != null && !(emailText.getText().toString().isEmpty()))
                        {
                            if (!(emailText.getText().toString().equals(origEmail)))
                            {
                                data.put(Constants.email_fire, emailText.getText().toString());
                            }
                        }

                        else
                        {
                            Toast.makeText(EditProfile.this, "Email Field can't be empty", Toast.LENGTH_SHORT).show();
                            updateAnim.setVisibility(View.GONE);
                            updateAnim.cancelAnimation();
                            updateText.setVisibility(View.VISIBLE);
                        }

                        if (newProfilePicNum != origProfileNum)
                        {
                            profilePicChanged = true;
                            data.put(Constants.profilePicNum_fire, newProfilePicNum);
                        }



                        if (data.size() > 0)
                        {
                            updateToFirestore(uid, data, profilePicChanged);
                        }

                        else if (!emptyName){
                            updateAnim.cancelAnimation();
                            Intent intent = new Intent(EditProfile.this, ProfileClass.class);
                            startActivity(intent);
                            finish();
                        }


                    }
                });

        PushDownAnim.setPushDownAnimTo(profileImage)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        changedProfilePicNum = 0;
                        openModalBottomDialog();

                    }
                });

        PushDownAnim.setPushDownAnimTo(emailText)
                .setScale(PushDownAnim.MODE_SCALE, 0.85f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                    }
                });

        PushDownAnim.setPushDownAnimTo(phoneText)
                .setScale(PushDownAnim.MODE_SCALE, 0.85f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(EditProfile.this, "You can't change your Phone Number", Toast.LENGTH_SHORT).show();

                    }
                });
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


    private void updateToFirestore(final String uid, Map<String, Object> map, final boolean changedProfilePic)
    {
        db.collection(Constants.users_fire).document(uid).update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if (changedProfilePic)
                        {
                            final DatabaseReference editReference = mDatabase.child(uid).child("profileNum");

                            editReference.setValue(newProfilePicNum)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Intent editIntent = new Intent(EditProfile.this, ProfileClass.class);
                                            Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                            startActivity(editIntent);
                                            finish();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            updateAnim.setVisibility(View.GONE);
                                            updateAnim.cancelAnimation();
                                            updateText.setVisibility(View.VISIBLE);

                                            Toast.makeText(EditProfile.this, "Error Updating Profile", Toast.LENGTH_SHORT).show();


                                        }
                                    });
                        }

                        else {
                            updateAnim.cancelAnimation();
                            Intent intent = new Intent(EditProfile.this, ProfileClass.class);
                            Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "onFailure: "+e.getMessage());
                        updateAnim.setVisibility(View.GONE);
                        updateAnim.cancelAnimation();
                        updateText.setVisibility(View.VISIBLE);

                        Toast.makeText(EditProfile.this, "Error Updating Profile", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void openModalBottomDialog()
    {
        mProfileFragment.show(getSupportFragmentManager(), "BottomSheetProfileFragment");
    }

    @Override
    public void onBottomSheetProfileButtonClicked(boolean type) {

        if (type)
        {
            playAnimationOnView(profileImage, Techniques.Landing);
            newProfilePicNum = changedProfilePicNum;
            changedProfilePicNum = 0;
            profileImage.setImageResource(profileImgs[newProfilePicNum]);
        }
        else {
            changedProfilePicNum = 0;
        }

    }

    @Override
    public void onProfilePictureSelected(int position) {

        changedProfilePicNum = position;

    }
}