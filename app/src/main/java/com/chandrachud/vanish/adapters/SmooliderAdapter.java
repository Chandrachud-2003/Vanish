package com.chandrachud.vanish.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.chandrachud.vanish.R;
import com.chandrachud.vanish.items.smooliderItem;

import java.util.ArrayList;

public class SmooliderAdapter extends PagerAdapter {


    private Context mContext;
    private ArrayList<smooliderItem> itemList;
    private int screenHeight;

    private TextView titleText;
    private LottieAnimationView lottieView;
    private TextView num;
    private TextView numText;
    private TextView descriptionText;

    public SmooliderAdapter(ArrayList<smooliderItem> itemList, Context context, int screenHeight) {
        this.itemList=itemList;
        mContext = context;
        this.screenHeight = screenHeight;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        smooliderItem item = itemList.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.main_smoolider_layout, container, false);


        findViewsById(view);
        setInfoToItem(item);












        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem (ViewGroup container, int position, Object object){
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject (View view, Object object){
        return view.equals(object);
    }

    private void findViewsById(View view)
    {
        titleText = view.findViewById(R.id.smooliderTitle);
        num = view.findViewById(R.id.smooliderNumber);
        numText = view.findViewById(R.id.smooliderNumberText);
        lottieView = view.findViewById(R.id.smooliderLottie);
        descriptionText = view.findViewById(R.id.smooliderDescription);
    }

    private void setInfoToItem(smooliderItem item)
    {
        titleText.setText(item.getTitle());
        num.setText(String.valueOf(item.getNumber()));
        numText.setText(item.getNumberText());
        descriptionText.setText(item.getDescription());

        lottieView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(screenHeight/8)));

        lottieView.setAnimation(item.getLottieFile());

    }
}



