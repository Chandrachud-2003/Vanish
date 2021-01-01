package com.chandrachud.vanish.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.av.smoothviewpager.Smoolider.SmoothViewpager;
import com.chandrachud.vanish.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.List;

import in.goodiebag.carouselpicker.CarouselPicker;
import in.goodiebag.carouselpicker.CarouselPicker.CarouselViewAdapter;

public class BottomSheetProfileFragment extends BottomSheetDialogFragment {

    private ButtonClickListener mButtonClickListener;

    private Button continueButton;
    private ImageButton closeButton;

    private int screenWidth;

    private Context mContext;

    private ArrayList<CarouselPicker.PickerItem> carouselList;

    private CarouselPicker mCarouselPicker;


    public BottomSheetProfileFragment(int screenWidth, Context context, ArrayList<CarouselPicker.PickerItem> carouselList) {
        // Required empty public constructor
        this.screenWidth = screenWidth;
        this.mContext = context;
        this.carouselList = carouselList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.bottom_sheet_profile, container, false);
        findViewsById(v);
        setOnClickListeners();
        setUpCarousel();


        return  v;
    }

    public interface ButtonClickListener
    {
        void onBottomSheetProfileButtonClicked(boolean type);
        void onProfilePictureSelected(int position);
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mButtonClickListener = (ButtonClickListener) context;

        }

        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()+"Must Implement BottomSheetListener");
        }




    }

    private void findViewsById(View v)
    {
        continueButton = v.findViewById(R.id.continueButton);
        closeButton = v.findViewById(R.id.closeButton);



        closeButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    closeButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    closeButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }


                int newDimensions = (int) (screenWidth * 0.07);
                closeButton.getLayoutParams().width = newDimensions;


                closeButton.getLayoutParams().height = newDimensions;

                //Log.d("TAG", "findViewsById: screenwidth:"+screen_width);
                //Log.d("TAG", "findViewsById: newHeight"+newHeight);


            }
        });

        mCarouselPicker = v.findViewById(R.id.profilePicker);



    }

    private void setOnClickListeners()
    {
        PushDownAnim.setPushDownAnimTo(continueButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mButtonClickListener.onBottomSheetProfileButtonClicked(true);
                        dismiss();


                    }
                });


        PushDownAnim.setPushDownAnimTo(closeButton)
                .setScale(PushDownAnim.MODE_SCALE, 0.8f)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mButtonClickListener.onBottomSheetProfileButtonClicked(false);
                        dismiss();


                    }
                });



    }

    private void setUpCarousel()
    {
        CarouselViewAdapter imageAdapter = new CarouselViewAdapter(mContext, carouselList, 0);
        Log.d("TAG", "setUpCarousel: Carousel list size:"+carouselList.size());
        mCarouselPicker.setAdapter(imageAdapter);

        mCarouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mButtonClickListener.onProfilePictureSelected(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }




}
