package model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.Toast;

import com.example.dan.spont.MainActivity;

import java.io.File;

/**
 * Created by ramani on 07/09/2015.
 */
public class WebAppInterface {
    private Context mContext;
    private final static int REQUEST_FILE_PICKER=1;
    private final static int FILECHOOSER_RESULTCODE=1;
    private String currentPicFile;

    public WebAppInterface(Context mContext_) {
        this.mContext = mContext_;
    }

    @JavascriptInterface   // must be added for API 17 or higher
    public void uploadImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        ((MainActivity)this.mContext).startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

}
