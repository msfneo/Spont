package model;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import com.example.dan.spont.MainActivity;

import java.io.File;

/**
 * Created by ramani on 07/09/2015.
 */
public class WebAppInterface {
    private Context mContext;
    private final static int REQUEST_FILE_PICKER=1;

    public WebAppInterface(Context mContext_) {
        this.mContext = mContext_;
    }

    @JavascriptInterface
    public void uploadImage(String offerId_, String mobilePhone_, String password_) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Globals Globals = new Globals();
        Globals.offerId = offerId_;
        Globals.mobilePhone = mobilePhone_;
        Globals.password = password_;
        ((MainActivity)mContext).startActivityForResult(galleryIntent, REQUEST_FILE_PICKER);
    }
}
