package com.example.luki.inzynierka.databaseUtils;

import android.net.Uri;

import org.androidannotations.annotations.EBean;

@EBean(scope = EBean.Scope.Singleton)
public class Variables {

    private Uri photoUri;
    private String properPhotoPath;

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public String getProperPhotoPath() {
        return properPhotoPath;
    }

    public void setProperPhotoPath(String properPhotoPath) {
        this.properPhotoPath = properPhotoPath;
    }
}
