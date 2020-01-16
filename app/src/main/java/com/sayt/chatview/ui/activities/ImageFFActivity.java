package com.sayt.chatview.ui.activities;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.sayt.chatview.R;
import com.squareup.picasso.Picasso;

public class ImageFFActivity extends AppCompatActivity {

    PhotoView photoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_ff);

        photoView = findViewById(R.id.photoView);
        Picasso.get().load(getIntent().getStringExtra("photoURI")).into(photoView);
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.image_transition));
        photoView.setTransitionName("photoTransition");
    }

    @Override
    public void onBackPressed() {
        //To support reverse transitions when user clicks the device back button
        supportFinishAfterTransition();
    }

}