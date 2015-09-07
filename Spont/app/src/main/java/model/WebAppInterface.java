package model;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.example.dan.spont.MainActivity;

/**
 * Created by ramani on 07/09/2015.
 */
public class WebAppInterface {
    private Context mContext;

    public WebAppInterface(Context mContext_) {
        this.mContext = mContext_;
    }

    @JavascriptInterface   // must be added for API 17 or higher
    public void uploadImage(String toast) {
        Toast.makeText(this.mContext, toast, Toast.LENGTH_SHORT).show();
    }

}
