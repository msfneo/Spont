package model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.dan.spont.CaptureActivity;
import com.example.dan.spont.MainActivity;

import java.io.File;

import Task.UpdateLocationTask;

/**
 * Created by ramani on 07/09/2015.
 */
public class WebAppInterface implements android.location.LocationListener {
    private Context mContext;
    private final static int REQUEST_FILE_PICKER=1;
    private final static int TAKE_A_PIC=2;
    private WebView mWebView;
    private LocationManager lm;
    private String longitude;
    private String latitude;
    private String baseUrl;

    public WebAppInterface(Context mContext_, WebView mWebView_, String baseUrl_) {
        this.mContext = mContext_;
        this.mWebView = mWebView_;
        this.lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,1, this);
        this.baseUrl = baseUrl_;
    }

    @JavascriptInterface
    public void uploadImage(String offerId_, String mobilePhone_, String password_) {
        final CharSequence[] items = { "Prendre une photo", "Choisir dans la galerie",
                "Annuler" };
        Globals Globals = new Globals();
        Globals.offerId = offerId_;
        Globals.mobilePhone = mobilePhone_;
        Globals.password = password_;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle("Ajouter une Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Prendre une photo")) {
                    /*String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/Spont");
                    myDir.mkdirs();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File current_file = new File(android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Spont", "temp.jpg");
                    Uri uriImage = Uri.fromFile(current_file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);*/
                    Intent intent = new Intent(mContext, CaptureActivity.class);
                    ((MainActivity)mContext).startActivityForResult(intent, TAKE_A_PIC);
                } else if (items[item].equals("Choisir dans la galerie")) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    ((MainActivity)mContext).startActivityForResult(galleryIntent, REQUEST_FILE_PICKER);
                } else if (items[item].equals("Annuler")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @JavascriptInterface
    public void updateLocation(String mobilePhone_, String password_) {
        System.out.println("appel a updateLocation()");
        Globals.mobilePhone = mobilePhone_;
        Globals.password = password_;
        if (this.longitude != null && this.latitude != null) {
            System.out.println("new location : "+this.longitude+" "+this.latitude);
            new UpdateLocationTask(this.baseUrl, this.longitude, this.latitude).execute();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.longitude = String.valueOf(location.getLongitude());
        this.latitude = String.valueOf(location.getLatitude());
        Toast.makeText(mContext, "YOU ARE MOVING",
                Toast.LENGTH_SHORT).show();
        if (Globals.mobilePhone != null && Globals.password != null) {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:js_android_getGeo()");
                }
            });
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(mContext, "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        mContext.startActivity(intent);
        Toast.makeText(mContext, "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }
}
