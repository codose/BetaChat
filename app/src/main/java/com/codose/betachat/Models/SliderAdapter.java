package com.codose.betachat.Models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.codose.betachat.R;
import com.codose.betachat.Register;
import com.codose.betachat.SignIn;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;

    }

    //Arrays
    public int[] slide_images = {

            R.drawable.splash1,
            R.drawable.splash2,
            R.drawable.splash3,

    };

    public int[] background = {
            R.color.white,
            R.color.bg2,
            R.color.white,

    };
    public int[] visible = {

            View.GONE,
            View.GONE,
            View.VISIBLE,

    };



    @Override
    public int getCount() {
        return slide_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slide_image = view.findViewById(R.id.slide_image);
        ConstraintLayout slider_layout = view.findViewById(R.id.slider_layout);
        Button reg_btn = view.findViewById(R.id.reg_button);
        Button login = view.findViewById(R.id.login_button);
        TextView welcome = view.findViewById(R.id.welcome_txt);
        slide_image.setImageResource(slide_images[position]);

        welcome.setVisibility(visible[position]);
        Typeface face = ResourcesCompat.getFont(context,R.font.pacifico);
        welcome.setTypeface(face);

        reg_btn.setVisibility(visible[position]);
        login.setVisibility(visible[position]);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_open = new Intent(context, Register.class);
                context.startActivity(reg_open);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent log_open = new Intent(context, SignIn.class);
                context.startActivity(log_open);
            }
        });

        slider_layout.setBackgroundResource(background[position]);

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((ConstraintLayout)object);
    }
}
