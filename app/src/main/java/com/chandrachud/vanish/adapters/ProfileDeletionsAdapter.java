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

import com.chandrachud.vanish.R;
import com.chandrachud.vanish.items.deletedNumberItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProfileDeletionsAdapter extends RecyclerView.Adapter<ProfileDeletionsAdapter.MyView>{

    private ArrayList<deletedNumberItem> mItems;
    private int screenWidth;
    private Context mContext;

    private int imageDimen;

    private int[] profileImgList;

    public ProfileDeletionsAdapter(Context context, int screenWidth, ArrayList<deletedNumberItem> deletedNumberItems, int[] profileImgList)
    {
        this.mContext = context;
        this.screenWidth = screenWidth;
        this.mItems = deletedNumberItems;
        this.profileImgList = profileImgList;

        imageDimen = (int) (screenWidth*0.073);




    }


    public class MyView
            extends RecyclerView.ViewHolder {

        // Text View
        ImageView image;
        TextView number;

        TextView date;

        public MyView(View view)
        {
            super(view);

            image = view.findViewById(R.id.imageViewMainRecycler);
            number = view.findViewById(R.id.numberText);
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
    public ProfileDeletionsAdapter.MyView onCreateViewHolder(ViewGroup parent,
                                                          int viewType)
    {

        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.delete_item_profile_layout,
                        parent,
                        false);

        // return itemView
        return new ProfileDeletionsAdapter.MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileDeletionsAdapter.MyView holder, int position) {

        deletedNumberItem item = mItems.get(position);

        holder.image.setImageResource(profileImgList[item.getProfilePic()]);
        holder.number.setText(item.getNumber());

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
