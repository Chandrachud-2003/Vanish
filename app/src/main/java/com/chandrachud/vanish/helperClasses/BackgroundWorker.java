package com.chandrachud.vanish.helperClasses;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.chandrachud.vanish.Constants;
import com.chandrachud.vanish.MainActivity;
import com.chandrachud.vanish.R;
import com.chandrachud.vanish.items.NotificationManagerItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BackgroundWorker extends Worker {

    private int notificationID = 1000;
    private int notificationID_2 = 1003;
    private int requestCode = 7777;
    private String uid;
    private DatabaseReference mDatabaseReference;
    private Context context;

    public BackgroundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        uid = FirebaseAuth.getInstance().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        this.context = context;

    }

    @NonNull
    @Override
    public Result doWork() {

        checkToDelete();
        checkUserDocument();

        return Result.success();
    }

    private void checkUserDocument()
    {
        mDatabaseReference.child(uid).child(Constants.isNotify_realtime).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {


                    String notifications = snapshot.getValue(String.class);

                    if (notifications != null && !(notifications.equals("")) && notifications.length() > 0) {

                        String[] notifyKeys = notifications.split(",");

                        if (notifyKeys.length > 0) {
                            checkNotifications(9, notifyKeys.length, notifyKeys);
                        } else {
                            Log.d("TAG", "onDataChange: field notify is empty");
                        }


                    } else {

                        Log.d("TAG", "onDataChange: Field notify is null or empty");
                        showDefaultNotification("Empty notifications found");


                    }

                }
                else {

                    Log.d("TAG", "onDataChange: Snapsot of UID does not exist");
                    showDefaultNotification("No notifications to show");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("TAG", "onCancelled: "+error.getMessage());
                showDefaultNotification("Unable ti retrieve notificaions");

            }
        });

    }

    private void checkNotifications(int i, final int max, final String[] ids)
    {
        final int index = i;
        if (i<max)
        {
            mDatabaseReference.child(uid).child(Constants.notifications_realtime).child(ids[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {


                        NotificationManagerItem item = snapshot.getValue(NotificationManagerItem.class);
                        if (item != null) {
                            showNotification(item.getType(), item.getNumber());

                        }

                        checkNotifications(index+1, max, ids);


                    }

                    else {
                        Log.d("TAG", "onDataChange: snapshot does not exist");
                        checkNotifications(index+1, max, ids);


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Log.d("TAG", "onCancelled: "+error.getMessage());

                    checkNotifications(index+1, max, ids);


                }
            });

        }

        else {

            mDatabaseReference.child(uid).child(Constants.isNotify_realtime).setValue("")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.d("TAG", "onSuccess: All notifications sent");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d("TAG", "onFailure: "+e.getMessage());

                        }
                    });

        }


    }

    private void checkToDelete()
    {
        mDatabaseReference.child(uid).child(Constants.deletedNumList_fire).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {


                    for (DataSnapshot deleteSnapsot : snapshot.getChildren()) {

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("TAG", "onCancelled: "+error.getMessage());

            }
        });

    }

    private void showNotification(String cons, String number)
    {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "VANISH_NOTIFICATION_CHANNEL_ID");
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);

        notificationBuilder.setContentTitle("Vanish");

        String message="";
        if (cons.equals(Constants.deleted_success))
        {
            message = "Update! Your Number was successfully deleted from "+number;
        }
        else if (cons.equals(Constants.num_not_found))
        {
            message = "We could not delete your Number from "+number+", since the User did not have your Number saved";
        }
        else if (cons.equals(Constants.blocked_success))
        {
            message = "Update! We just blocked a Non-Premium User from deleting their Number from your Device";
        }

        notificationBuilder.setContentText(message);
        notificationBuilder.setStyle( new NotificationCompat.BigTextStyle().bigText(message));
        PendingIntent contentIntent =
                PendingIntent.getActivity(context, requestCode, new Intent(context, MainActivity.class ), PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(contentIntent);
        notificationBuilder.setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "VANISH_NOTIFICATION_CHANNEL_ID";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Background Task - VANISH",
                        NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
                notificationBuilder.setChannelId(channelId);
            }


            manager.notify(notificationID, notificationBuilder.build());
            notificationID++;
            Log.d("TAG", "doWork: notification sent");
        } else {
            Log.d("TAG", "doWork: manager is null");

        }





    }

    private void showDefaultNotification(String message)
    {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "VANISH_NOTIFICATION_CHANNEL_ID");
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        notificationBuilder.setContentTitle("Vanish");
        notificationBuilder.setContentText(message);
        notificationBuilder.setStyle( new NotificationCompat.BigTextStyle().bigText(message));
        PendingIntent contentIntent =
                PendingIntent.getActivity(context, requestCode, new Intent(context, MainActivity.class ), PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(contentIntent);
        notificationBuilder.setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "VANISH_NOTIFICATION_CHANNEL_ID";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Background Task - VANISH",
                        NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
                notificationBuilder.setChannelId(channelId);
            }


            manager.notify(notificationID_2, notificationBuilder.build());
            notificationID_2++;
            Log.d("TAG", "doWork: notification sent");
        } else {
            Log.d("TAG", "doWork: manager is null");

        }

    }


}
