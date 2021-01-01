package com.chandrachud.vanish.helperClasses;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chandrachud.vanish.Constants;
import com.chandrachud.vanish.items.backgroundItem;
import com.chandrachud.vanish.items.numberItemFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateFirebaseUser {

    private FirebaseFirestore db;
    private DatabaseReference mDatabase;

    private String name;
    private String phoneNumber;
    private String uid;
    private Context mContext;
    private String ccc;
    private String onlyNum;
    private String email;
    private int profilePicNum;

    private signUpFinishedListener mSignUpFinishedListener;



    public CreateFirebaseUser( Context context, String name, String phoneNumber, String uid,String ccc, String onlyNum, String email, int profilePicNum) {
        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        this.name = name;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
        mContext = context;
        this.ccc = ccc;
        this.onlyNum = onlyNum;
        this.email = email;
        this.profilePicNum = profilePicNum;

        try {
            mSignUpFinishedListener = (signUpFinishedListener) context;

        }

        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()+"Must Implement signUpFinishedListener");
        }


    }

    public void writeToDatabase()
    {

        Map<String, Object> data = new HashMap<>();
        data.put(Constants.name_fire, name);
        data.put(Constants.uid_fire, uid);
        data.put(Constants.blockedDeletionsTotal_fire, 0);
        data.put(Constants.blockedDeletionsWeek_fire, 0);
        data.put(Constants.premium_fire, false);
        data.put(Constants.phoneNum_fire, phoneNumber);
        data.put(Constants.email_fire, email);
        data.put(Constants.onlyPhoneNum_fire, onlyNum);
        data.put(Constants.onlyCCode_fire, ccc);
        data.put(Constants.deletedNumCountTotal_fire, 0);
        data.put(Constants.deletedNumCountWeek_fire, 0);
        data.put(Constants.deleteAttemptsWeek_fire, 0);
        data.put(Constants.deleteAttemptsTotal_fire, 0);

        data.put(Constants.profilePicNum_fire, profilePicNum);
        data.put(Constants.othersDeletedCountTotal_fire, 0);
        data.put(Constants.othersDeletedCountWeek_fire, 0);

        db.collection(Constants.users_fire).document(uid).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        setUpRealtimeDatabase();

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        mSignUpFinishedListener.accountSetUpFinished(false);
                        Log.d("TAG", "firestore onFailure: "+e);

                    }
                });











    }

    private void setUpRealtimeDatabase()
    {
        backgroundItem item = new backgroundItem(phoneNumber, uid, "", false, false, new ArrayList<numberItemFirebase>(), new ArrayList<numberItemFirebase>(), false, profilePicNum, ccc);
        mDatabase.child(uid).setValue(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mSignUpFinishedListener.accountSetUpFinished(true);

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        mSignUpFinishedListener.accountSetUpFinished(false);
                        Log.d("TAG", " realtime onFailure: "+e);

                    }
                });
    }

    public interface signUpFinishedListener
    {
         void accountSetUpFinished(boolean finished);
    }
}
