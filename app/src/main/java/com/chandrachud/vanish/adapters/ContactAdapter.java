package com.chandrachud.vanish.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.chandrachud.vanish.fragments.BottomSheetContact;
import com.chandrachud.vanish.R;
import com.chandrachud.vanish.items.contactDataItem;
import com.chandrachud.vanish.items.contactHeadingItem;
import com.chandrachud.vanish.items.modelContact;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter   {

    private ArrayList<modelContact> dataSet;
    Context mContext;
    int total_types;
    private int screenWidth;
    private int screenHeight;
    private ColorGenerator generator;

    private BottomSheetContact mBottomSheetContact;

    private Bitmap imageBitmap;

    private int lastPosition;

    private boolean isPremium;
    private String userPhone;
    private String userCcd;




    public static class HeadingTypeViewHolder extends RecyclerView.ViewHolder {

        TextView heading;


        public HeadingTypeViewHolder(View itemView) {
            super(itemView);

           this.heading = itemView.findViewById(R.id.contactHeadingTitle);
        }
    }

    public static class DataTypeViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView number;
        ImageView picture;

        public DataTypeViewHolder(View itemView) {
            super(itemView);

            this.name = itemView.findViewById(R.id.contactDataName);
            this.number = itemView.findViewById(R.id.contactDataNumber);
            picture = itemView.findViewById(R.id.contactDataImage);


        }
    }


    public ContactAdapter(ArrayList<modelContact>data, Context context, boolean isPremium, String userPhone, String userCcd) {
        this.dataSet = data;
        this.mContext = context;
        this.isPremium = isPremium;
        this.userPhone = userPhone;
        this.userCcd = userCcd;
        total_types = dataSet.size();

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        generator = ColorGenerator.MATERIAL;

        lastPosition = -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case modelContact.HEADING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_heading_item, parent, false);
                return new HeadingTypeViewHolder(view);
            case modelContact.DATA:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_data_item, parent, false);
                return new DataTypeViewHolder(view);

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).getType()) {
            case 0:
                return modelContact.HEADING;
            case 1:
                return modelContact.DATA;

            default:
                return -1;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof LinearLayoutManager && getItemCount() > 0) {
            final LinearLayoutManager llm = (LinearLayoutManager) manager;
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }



                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int visiblePosition = llm.findFirstCompletelyVisibleItemPosition();

                    Log.d("TAG", "onScrolled: onScrolled called");

                    if(visiblePosition > -1) {
                        View v = llm.findViewByPosition(visiblePosition);
                        //do something

                       /* YoYo.with(Techniques.Landing)
                                .duration(700)
                                .repeat(0)
                                .playOn(v);
*/
                        Log.d("TAG", "onScrolled: onScrolled entered");


                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, final int position) {

        int type = dataSet.get(position)
                .getType();

        setAnimation(holder.itemView, position);


        switch (type)
        {
            case modelContact.HEADING:
            {
                contactHeadingItem item = dataSet.get(position).getHeadingItem();
                if (item!=null) {
                    String title = item.getLetter();

                    ((HeadingTypeViewHolder) holder).heading.setText(title);
                }

                break;
            }
            case modelContact.DATA:
            {
                contactDataItem item = dataSet.get(position).getDataItem();
                if (item!=null)
                {
                    setAnimation(holder.itemView, position);
                    final String name = item.getName();
                    final String number = item.getNumber();
                    YoYo.with(Techniques.Landing)
                            .duration(700)
                            .repeat(0)
                            .playOn(((DataTypeViewHolder)holder).name);

                    ((DataTypeViewHolder)holder).name.setText(name);

                    YoYo.with(Techniques.Landing)
                            .duration(700)
                            .repeat(0)
                            .playOn(((DataTypeViewHolder)holder).number);
                    ((DataTypeViewHolder)holder).number.setText(number);

                    imageBitmap = null;

                    String photo = item.getPicture();
                    int width = screenWidth/10;
                    if (photo!=null && photo.length()>0)
                    {

                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(photo));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        YoYo.with(Techniques.Landing)
                                .duration(700)
                                .repeat(0)
                                .playOn(((DataTypeViewHolder)holder).picture);

                        ((DataTypeViewHolder)holder).picture.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, width, width, false));


                    }

                    else {

                        Typeface font = ResourcesCompat.getFont(mContext, R.font.montserrat);

                        TextDrawable imageDrawable = TextDrawable.builder()
                                .beginConfig()
                                .textColor(Color.WHITE)
                                .useFont(font)
                                .width(width)
                                .height(width)
                                .bold().toUpperCase()
                                .endConfig()
                                .buildRound(Character.toString(Character.toUpperCase(name.charAt(0))), generator.getColor(name));





                        //((DataTypeViewHolder)holder).picture.setImageBitmap(Bitmap.createScaledBitmap(defaultBitmap, width, width, false));

                        YoYo.with(Techniques.Landing)
                                .duration(700)
                                .repeat(0)
                                .playOn(((DataTypeViewHolder)holder).picture);

                        ((DataTypeViewHolder)holder).picture.setImageDrawable(imageDrawable);


                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mBottomSheetContact = new BottomSheetContact(name, number, mContext, screenWidth, screenHeight, imageBitmap, isPremium, userPhone, userCcd);
                            mBottomSheetContact.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "BottomSheetContactFragment");



                        }
                    });





                }

                break;
            }
        }

    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public ArrayList<modelContact> getDataSet() {
        return dataSet;
    }

    public void setDataSet(ArrayList<modelContact> dataSet) {
        this.dataSet = dataSet;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
