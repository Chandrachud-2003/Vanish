package com.chandrachud.vanish.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chandrachud.vanish.Constants;
import com.chandrachud.vanish.R;
import com.chandrachud.vanish.items.NotificationManagerItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProfileNotificationsAdapter extends RecyclerView.Adapter<ProfileNotificationsAdapter.MyView> {

    private ArrayList<NotificationManagerItem> mItems;
    private int screenWidth;
    private Context mContext;

    private int imageDimen;

    public ProfileNotificationsAdapter(Context context, int screenWidth, ArrayList<NotificationManagerItem> managerItems)
    {
        this.mContext = context;
        this.screenWidth = screenWidth;
        this.mItems = managerItems;

        imageDimen = (int) (screenWidth*0.073);



    }


    public class MyView
            extends RecyclerView.ViewHolder {

        // Text View
        ImageView image;
        TextView number;
        TextView message;
        TextView date;

        public MyView(View view)
        {
            super(view);

            image = view.findViewById(R.id.imageViewMainRecycler);
            number = view.findViewById(R.id.numberText);
            message = view.findViewById(R.id.messageText);
            date = view.findViewById(R.id.dateText);

            image.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        image.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }



                    image.getLayoutParams().width = imageDimen;
                    image.getLayoutParams().height = imageDimen;



                }
            });

        }
    }
    // Override onCreateViewHolder which deals
    // with the inflation of the card layout
    // as an item for the RecyclerView.
    @Override
    public ProfileNotificationsAdapter.MyView onCreateViewHolder(ViewGroup parent,
                                                              int viewType)
    {

        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.notifications_profile_item_layout,
                        parent,
                        false);

        // return itemView
        return new ProfileNotificationsAdapter.MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileNotificationsAdapter.MyView holder, int position) {

        NotificationManagerItem item = mItems.get(position);

        String cons = item.getType();

        if (cons.equals(Constants.deleted_success))
        {
            holder.image.setImageResource(R.drawable.trash_bin_white_70);
            holder.number.setText(item.getNumber());
            holder.message.setText(Constants.deleted_message);
        }
        else if (cons.equals(Constants.blocked_success))
        {
            holder.image.setImageResource(R.drawable.cancel_white_70);
            holder.number.setText("Blocked");
            holder.message.setText(Constants.blocked_message);
        }
        else if (cons.equals(Constants.num_not_found))
        {
            holder.image.setImageResource(R.drawable.not_found_white_70);
            holder.number.setText(item.getNumber());
            holder.message.setText(Constants.not_found_message);
        }

        long timestamp = item.getTimestamp();

        if (timestamp >0) {
            Date date = new Date(timestamp);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day= cal.get(Calendar.DAY_OF_MONTH);
            String month = getMonthName(cal.get(Calendar.MONTH));
            int year = cal.get(Calendar.YEAR);
            holder.date.setText(month+ " "+day+", "+year);

        }
        else {
            holder.date.setText("Unavailable");
        }


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private String getMonthName(int m)
    {
        String month="";

        switch (m)
        {
            case 0:
            {
                month = "Jan";
                break;
            }
            case 1:
            {
                month = "Feb";
                break;
            }

            case 2:
            {
                month = "Mar";
                break;
            }
            case 3:
            {
                month = "Apr";
                break;
            }
            case 4:
            {
                month = "May";
                break;
            }
            case 5:
            {
                month = "Jun";
                break;
            }
            case 6:
            {
                month = "Jul";
                break;
            }
            case 7:
            {
                month = "Aug";
                break;
            }
            case 8:
            {
                month = "Sep";
                break;
            }
            case 9:
            {
                month = "Oct";
                break;
            }
            case 10:
            {
                month = "Nov";
                break;
            }
            case 11:
            {
                month = "Dec";
                break;
            }
        }

        return month;
    }

}
