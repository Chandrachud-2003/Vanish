package com.chandrachud.vanish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.chandrachud.vanish.adapters.ContactAdapter;
import com.chandrachud.vanish.items.contactDataItem;
import com.chandrachud.vanish.items.contactHeadingItem;
import com.chandrachud.vanish.items.modelContact;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.PhoneNumber;
import com.github.tamir7.contacts.Query;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.thekhaeng.pushdownanim.PushDownAnim;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class ContactActivity extends AppCompatActivity {

    private ArrayList<modelContact> modelContacts;
    private ArrayList<modelContact> sortedContacts;
    private int REQUEST_CODE = 12345;

    private AnimatedBottomBar mAnimatedBottomBar;

    private NestedScrollView mNestedScrollView;

    private EditText phoneEditText;
    private CountryCodePicker ccpSpinner;
    private RecyclerView recentRecycler;
    private RecyclerView contactRecycler;

    private ContactAdapter mContactAdapter;

    private LinearLayoutManager verticalLayout;

    private AddContactsAsync addTask;

    private LinearLayout loadingLayout;
    private LottieAnimationView loadingAnim;

    private TextView noRecentText;
    private LottieAnimationView noRecentAnim;

    private boolean isPremium;
    private String phoneNum;
    private String ccd;

    private int screenHeight;
    private int screenWidth;

    private EditText searchEditText;
    private TextView searchButton;
    private ImageView searchImage;

    private CardView searchCard;

    private LottieAnimationView searchButtonAnim;

    private TextView loadingText;

    private ArrayList<modelContact> searchContacts;

    private boolean isSearching;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(ContactActivity.this,R.color.mainBlue));// set status background white

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }

        Contacts.initialize(this);

        findViewsById();
        setOnClickListeners();
        requestContactPermissions();

    }

    private void findViewsById()
     {
         DisplayMetrics displayMetrics = new DisplayMetrics();
         getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         screenHeight = (int) (displayMetrics.heightPixels);
         screenWidth = (int) (displayMetrics.widthPixels);

         mSharedPreferences = getSharedPreferences(Constants.shared_prefs, MODE_PRIVATE);
         mEditor = mSharedPreferences.edit();

        isPremium = mSharedPreferences.getBoolean(Constants.premium_prefs, false);
        phoneNum = mSharedPreferences.getString(Constants.phone_prefs,"");
        ccd = mSharedPreferences.getString(Constants.ccc_prefs, "");

        mAnimatedBottomBar = findViewById(R.id.bottom_bar);
        mAnimatedBottomBar.selectTabAt(1, true);

        phoneEditText = findViewById(R.id.phoneNumberEdit);
        recentRecycler = findViewById(R.id.recentlyDeletedRecycler);
        contactRecycler = findViewById(R.id.allContactsRecycler);
        mNestedScrollView = findViewById(R.id.nestedScrollView);

        searchButton = findViewById(R.id.searchButton);
        searchEditText = findViewById(R.id.searchEditText);
        searchImage = findViewById(R.id.searchImage);
        searchCard = findViewById(R.id.searchLayout);
        searchButtonAnim = findViewById(R.id.searchButtonAnim);

        searchCard.setVisibility(View.GONE);

        loadingText = findViewById(R.id.loadingText);
        searchContacts = new ArrayList<>();
        isSearching = false;

         contactRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
             @Override
             public void onGlobalLayout() {

                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                     contactRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                 } else {
                     contactRecycler.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                 }

                 contactRecycler.getLayoutParams().height = screenHeight;



             }
         });

         searchImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
             @Override
             public void onGlobalLayout() {

                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                     searchImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                 } else {
                     searchImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                 }
                 int newDimen = (int)(screenWidth * 0.0479);

                 searchImage.getLayoutParams().height = newDimen;
                 searchImage.getLayoutParams().width = newDimen;



             }
         });

        ccpSpinner = findViewById(R.id.ccp);

        modelContacts = new ArrayList<>();
        sortedContacts = new ArrayList<>();

        verticalLayout = new LinearLayoutManager(
                ContactActivity.this,
                LinearLayoutManager.VERTICAL,
                false);


        loadingAnim = findViewById(R.id.loadingContactsAnimation);
        loadingLayout = findViewById(R.id.contactLoadingLayout);

        noRecentAnim = findViewById(R.id.noRecentAnimation);
        noRecentText = findViewById(R.id.noRecentText);

        setNoRecentLayout(true);




    }

    private void setOnClickListeners()
    {
        mAnimatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NotNull AnimatedBottomBar.Tab tab1) {

                if (i1!=1)
                {
                    switch (i1)
                    {
                        case 0:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        }

                        case 2:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(ContactActivity.this, ProfileClass.class);
                            startActivity(intent);
                            finish();
                            break;
                        }
                        case 3:
                        {
                            mAnimatedBottomBar.setClickable(false);
                            Intent intent = new Intent(ContactActivity.this, SettingsClass.class);
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

        mNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) mNestedScrollView.getChildAt(mNestedScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (mNestedScrollView.getHeight() + mNestedScrollView
                        .getScrollY()));

                if (diff == 0) {

                    contactRecycler.suppressLayout(false);

                }

            }
        });

        PushDownAnim.setPushDownAnimTo(searchButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.9f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!searchButton.getText().toString().isEmpty())
                        {
                            searchEditText.clearFocus();
                            searchEditText.setEnabled(false);
                            searchButton.setText("");
                            searchButton.setClickable(false);
                            searchButtonAnim.setVisibility(View.VISIBLE);
                            searchButtonAnim.playAnimation();
                            contactRecycler.setVisibility(View.GONE);
                            loadingLayout.setVisibility(View.VISIBLE);
                            loadingAnim.setVisibility(View.VISIBLE);
                            loadingAnim.playAnimation();
                            loadingText.setText("Searching...");

                            searchForContact(searchEditText.getText().toString());

                        }

                    }
                });



        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {



            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                Log.d("TAG", "afterTextChanged: TEXT : "+searchEditText.getText().toString());

                if (isSearching && searchEditText.getText().toString().isEmpty())
                {
                    isSearching = false;
                    searchEditText.clearFocus();
                    mContactAdapter.setDataSet(sortedContacts);
                    contactRecycler.setVisibility(View.VISIBLE);
                    loadingLayout.setVisibility(View.GONE);
                    mContactAdapter.notifyDataSetChanged();

                }

            }
        });


    }

    private void requestContactPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},REQUEST_CODE);

        }
        
        else {
            addTask=new AddContactsAsync(ContactActivity.this);
            addTask.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE)
        {
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                addTask=new AddContactsAsync(ContactActivity.this);
                addTask.execute();
            }

            else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
                builder.setTitle("Permission Error");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (addTask!=null) {
                            addTask = new AddContactsAsync(ContactActivity.this);
                            addTask.execute();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setMessage("You have not granted the \"Read Contacts\", would you like to try again?");

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        }


    }

    private void getContacts() {

        /*String prevName="";
        String prevNumber="";
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (cursor != null && cursor.getCount()>0) {
            while (cursor.moveToNext())
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));


                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String mobile = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));




                String image = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                *//*Bitmap photo = null;

                try {
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                    }




                } catch (IOException e) {
                    e.printStackTrace();
                }*//*

                if (!name.equals(prevName)) {

                    modelContacts.add(new modelContact(modelContact.DATA, null, new contactDataItem(name, mobile, image)));
                }
               *//* else {
                    if (!mobile.equals(prevNumber)) {
                        if (checkIfNumberHasCountryCode(mobile)) {

                            modelContacts.get(modelContacts.size()-1).setDataItem( new contactDataItem(name, mobile, image));

                        }
                    }

                }*//*
                prevNumber = mobile;
                prevName = name;


                Log.d("TAG", "getContacts: Name: "+name);
                Log.d("TAG", "getContacts: Prev Name: "+prevName);


            }
            cursor.close();


        }*/

        String prevName="";
        String prevNum = "";

        Query q = Contacts.getQuery();
        q.hasPhoneNumber();
        List<Contact> contacts = q.find();

        String number = "";

        for(int i=0;i<contacts.size();i++)
        {
            String name = contacts.get(i).getDisplayName();
            List<PhoneNumber> list = contacts.get(i).getPhoneNumbers();
            if (list.size()>0) {
                number = list.get(0).getNumber();

                if (list.size() > 1 && !checkIfNumberHasCountryCode(number)) {
                    for (int j = 1; j < list.size(); j++) {
                        String tempNum = list.get(j).getNumber();
                        if (checkIfNumberHasCountryCode(tempNum)) {
                            number = tempNum;
                            break;
                        }
                    }
                }

                if (!name.equals(prevName)) {
                    modelContacts.add(new modelContact(modelContact.DATA, null, new contactDataItem(contacts.get(i).getDisplayName(), contacts.get(i).getPhoneNumbers().get(0).getNumber(), contacts.get(i).getPhotoUri())));

                } else {

                    if (!checkIfNumberHasCountryCode(prevNum) && checkIfNumberHasCountryCode(number)) {
                        modelContacts.get(i - 1).setDataItem(new contactDataItem(contacts.get(i).getDisplayName(), contacts.get(i).getPhoneNumbers().get(0).getNumber(), contacts.get(i).getPhotoUri()));
                    }

                }
            }

            prevName = name;
            prevNum = number;


        }
    }

    private boolean checkIfNumberHasCountryCode(String phone)
    {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Log.d("TAG", "checkIfNumberIsValid: phone number:"+phone);
            // phone must begin with '+'
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phone, "");
            int countryCode = numberProto.getCountryCode();

            return true;



        } catch (NumberParseException e) {
            Log.d("TAG", ("NumberParseException was thrown: " + e.toString()));

            return  false;

        }

    }

    private void sortContacts()
    {
        Collections.sort(modelContacts, new Comparator<modelContact>() {
            public int compare(modelContact v1, modelContact v2) {
                return v1.getDataItem().getName().compareTo(v2.getDataItem().getName());
            }
        });

        //addHeadersToArrayList();




    }

    private void addHeadersToArrayList()
    {
        if (modelContacts.size()>0) {
            Log.d(Constants.tag, "addHeadersToArrayList: number of contacts"+modelContacts.size());
            sortedContacts.addAll(modelContacts);
            sortedContacts.add(0, new modelContact(modelContact.HEADING, new contactHeadingItem(Character.toString(Character.toUpperCase(sortedContacts.get(0).getDataItem().getName().charAt(0)))), null ));

            if (sortedContacts.size()>2) {

                for (int i = 2; i < sortedContacts.size(); i++) {
                    if (sortedContacts.get(i).getDataItem() != null) {


                        char ch1 = sortedContacts.get(i).getDataItem().getName().charAt(0);
                        char ch2 = sortedContacts.get(i - 1).getDataItem().getName().charAt(0);

                        if (ch1 != ch2) {
                            sortedContacts.add(i, new modelContact(modelContact.HEADING, new contactHeadingItem(Character.toString(Character.toUpperCase(ch1))), null));
                            i += 1;
                        }
                    }
                }
            }








        }



    }

    private void setUpContactRecycler(boolean searching)
    {
        if (!searching) {
            mContactAdapter = new ContactAdapter(sortedContacts, ContactActivity.this, isPremium, phoneNum, ccd);
            contactRecycler.setLayoutManager(verticalLayout);
            contactRecycler.setAdapter(mContactAdapter);
        }
        else {

            mContactAdapter.setDataSet(searchContacts);
            mContactAdapter.notifyDataSetChanged();
            loadingAnim.cancelAnimation();
            loadingLayout.setVisibility(View.GONE);
            searchButtonAnim.cancelAnimation();
            searchButtonAnim.setVisibility(View.GONE);
            searchButton.setText("GO");
            searchButton.setClickable(true);
            contactRecycler.setVisibility(View.VISIBLE);
            searchEditText.setEnabled(true);

        }
    }

    private class AddContactsAsync extends AsyncTask<Void, Integer, Void> {

        //private final WeakReference<Activity> weakActivity;

        AddContactsAsync(Activity myActivity) {
            //this.weakActivity = new WeakReference<>(myActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingLayout.setVisibility(View.VISIBLE);
            loadingAnim.playAnimation();
        }

        protected Void doInBackground(Void... voids) {
            // code that will run in the background

           // Activity activity = weakActivity.get();



                getContacts();
                sortContacts();
                addHeadersToArrayList();




            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.setVisibility(View.GONE);
                    loadingAnim.cancelAnimation();

                    setUpContactRecycler(false);
                    YoYo.with(Techniques.Landing)
                            .duration(1500)
                            .repeat(0)
                            .playOn(searchCard);

                    searchCard.setVisibility(View.VISIBLE);
                    contactRecycler.suppressLayout(true);
                }
            }, 3000);


        }


    }

    private void setNoRecentLayout(boolean appear)
    {
        if (appear)
        {
            noRecentText.setVisibility(View.VISIBLE);
            noRecentAnim.setVisibility(View.VISIBLE);
            noRecentAnim.playAnimation();
        }
        else {
            noRecentAnim.setVisibility(View.GONE);
            noRecentAnim.cancelAnimation();
            noRecentText.setVisibility(View.GONE);
        }
    }

    private void searchForContact(String query) {

        isSearching = true;

        Query mainQuery = Contacts.getQuery();
        Query q1 = Contacts.getQuery();
        q1.whereStartsWith(Contact.Field.DisplayName, query);

        List<Query> qs = new ArrayList<>();
        qs.add(q1);
        mainQuery.or(qs);
        List<Contact> contacts = mainQuery.find();

        String prevName = "";
        String prevNum = "";
        String number = "";

        searchContacts.clear();

        for (int i = 0; i < contacts.size(); i++) {
            String name = contacts.get(i).getDisplayName();
            List<PhoneNumber> list = contacts.get(i).getPhoneNumbers();
            if (list.size() > 0) {
                number = list.get(0).getNumber();

                if (list.size() > 1 && !checkIfNumberHasCountryCode(number)) {
                    for (int j = 1; j < list.size(); j++) {
                        String tempNum = list.get(j).getNumber();
                        if (checkIfNumberHasCountryCode(tempNum)) {
                            number = tempNum;
                            break;
                        }
                    }
                }

                if (!name.equals(prevName)) {
                    searchContacts.add(new modelContact(modelContact.DATA, null, new contactDataItem(contacts.get(i).getDisplayName(), contacts.get(i).getPhoneNumbers().get(0).getNumber(), contacts.get(i).getPhotoUri())));

                } else {

                    if (!checkIfNumberHasCountryCode(prevNum) && checkIfNumberHasCountryCode(number)) {
                        searchContacts.get(i - 1).setDataItem(new contactDataItem(contacts.get(i).getDisplayName(), contacts.get(i).getPhoneNumbers().get(0).getNumber(), contacts.get(i).getPhotoUri()));
                    }

                }
            }

            prevName = name;
            prevNum = number;

        }

        if (searchContacts.size()>0)
        {
            setUpContactRecycler(true);
        }
        else {

            searchButtonAnim.cancelAnimation();
            searchButtonAnim.setVisibility(View.GONE);
            searchButton.setText("GO");
            searchButton.setClickable(true);
            loadingAnim.cancelAnimation();
            loadingAnim.setAnimation(R.raw.contact_not_found);
            loadingAnim.playAnimation();
            loadingText.setText("No results");
            searchEditText.setEnabled(true);
            searchButton.setClickable(true);

        }

    }




}