package com.sayt.chatview.ui.activities;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.sayt.chatview.R;
import com.squareup.picasso.Picasso;

public class ImageFFActivity extends AppCompatActivity {

    PhotoView photoView;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_ff);

        photoView = findViewById(R.id.photoView);
        imageView = findViewById(R.id.imageView);
        if (getIntent()!= null && getIntent().hasExtra("photoURI")){
            if (getIntent().hasExtra("its_gif")){
                photoView.setVisibility(View.GONE);
                Glide.with(this)
                        .asGif()
                        .load(getIntent().getStringExtra("photoURI"))
                        .into(imageView);
            }else{
                imageView.setVisibility(View.GONE);
                Picasso.get().load(getIntent().getStringExtra("photoURI")).into(photoView);
            }
        }else{
            finish();
        }

        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.image_transition));
        photoView.setTransitionName("photoTransition");
        imageView.setTransitionName("photoTransition");
    }

    @Override
    public void onBackPressed() {
        //To support reverse transitions when user clicks the device back button
        supportFinishAfterTransition();
    }

}
