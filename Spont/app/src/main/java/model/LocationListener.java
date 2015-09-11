package model;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.studionet.dan.spont.MainActivity;


/**
 * Created by Dan on 07/09/2015.
 */
public class LocationListener extends MainActivity {
    WebView webView = new WebView(this);
    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    Double longitude = location.getLongitude();
    String longitude2 = Double.toString(longitude);
    Double latitude = location.getLatitude();
    String latitude2 = Double.toString(longitude);


    @JavascriptInterface
    public void updateLocation(String mobilePhone_, String password_) {
        System.out.println("J UPDATE LOCATION");
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        Globals.mobilePhone = mobilePhone_;
        Globals.password = password_;
        webView.loadUrl("javascript:android_getGeo("+mobilePhone_+", "+ password_+","+ latitude2+","+longitude2+")");
    }

}
