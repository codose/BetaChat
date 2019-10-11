package com.codose.betachat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codose.betachat.Models.SliderAdapter;

import static android.view.View.GONE;

public class StartActivity extends AppCompatActivity {
    private Button reg_btn;
    private Button login;
    private ViewPager SlidePager;

    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private LinearLayout dotsLayout;
    private int mCurrentPage;
    private Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        reg_btn = findViewById(R.id.reg_button);
        login = findViewById(R.id.login_button);

        skip = findViewById(R.id.Skip);

        SlidePager = findViewById(R.id.SlidePager);

        sliderAdapter = new SliderAdapter(this);

        SlidePager.setAdapter(sliderAdapter);

        dotsLayout = findViewById(R.id.dotsLayout);

        addDotIndicator(0);

        SlidePager.addOnPageChangeListener(viewListener);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SlidePager.setCurrentItem(2);
            }
        });


    }

    public void addDotIndicator(int position){
        mDots = new TextView[3];
        dotsLayout.removeAllViews();

        for(int i=0; i<mDots.length;i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.ash));

            dotsLayout.addView(mDots[i]);

        }
        if(mDots.length>0){

            mDots[position].setTextColor(getResources().getColor(R.color.white));

        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotIndicator(position);
            mCurrentPage = position;
            if(position==mDots.length -1){
                skip.setVisibility(GONE);
            }else{
                skip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
