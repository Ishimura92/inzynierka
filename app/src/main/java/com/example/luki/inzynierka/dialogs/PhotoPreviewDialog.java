package com.example.luki.inzynierka.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.luki.inzynierka.R;

public class PhotoPreviewDialog extends Dialog {

    private ImageView imageViewPhotoPreview;

    public PhotoPreviewDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_photo_preview);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        configViews();
    }

    private void configViews() {
        imageViewPhotoPreview = (ImageView) findViewById(R.id.imageViewPhotoPreview);

        imageViewPhotoPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public ImageView getImageView(){
        return imageViewPhotoPreview;
    }
}