package com.sayt.chatview.models;

import android.net.Uri;

import java.io.Serializable;

public class ImageList implements Serializable {
    private Uri imageUri;
    private String  localLocation;

    public ImageList(Uri imageUri, String localLocation) {
        this.imageUri = imageUri;
        this.localLocation = localLocation;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getLocalLocation() {
        return localLocation;
    }

    public void setLocalLocation(String localLocation) {
        this.localLocation = localLocation;
    }
}
