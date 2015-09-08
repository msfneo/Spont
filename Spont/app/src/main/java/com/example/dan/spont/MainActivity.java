/*
* edited by Tahina on 07/09/2015 11:49
* add addJavascriptInterface to bind javascript with Android class "WebAppInterface"
* */

package com.example.dan.spont;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import Task.FileUploaderTask;
import model.Globals;
import model.LocationListener;
import model.WebAppInterface;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {
    /* URL saved to be loaded after fb login */
    private static final String target_url="http://app.spont.fr";
    private static final String target_url_prefix="app.spont.fr";
    private static final int REQUEST_FILE_PICKER = 1;
    private final static int TAKE_A_PIC=2;
    private Context mContext;
    public WebView mWebview;
    private WebView mWebviewPop;
    protected static final String TAG = "basic-location-sample";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected boolean loopLaunched = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent intent)
    {
        if (resultCode == MainActivity.RESULT_OK) {
            if (requestCode == REQUEST_FILE_PICKER || requestCode == TAKE_A_PIC)
            {
                String imagePath = "";
                if (requestCode == REQUEST_FILE_PICKER)
                    imagePath = getRealPathFromURI(this,intent.getData());
                else {
                    imagePath = intent.getStringExtra("imagePath");
                }
                new FileUploaderTask(this.mContext,imagePath,this.mWebview).execute();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timer autoUpdate = new Timer();
        autoUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i("tag", "This'll run 5000 milliseconds later");
                        launchLocalization();
                    }
                });
            }
        }, 0, 30000);
        //      this.lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //       lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        Location location = this.lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        buildGoogleApiClient();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        mWebview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebview.getSettings();
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.setInitialScale(1);
        //mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.getSettings().setSupportMultipleWindows(true);
        mWebview.setWebViewClient(new UriWebViewClient());
        mWebview.setWebChromeClient(new UriChromeClient());
        mWebview.getSettings().setUserAgentString(this.mWebview.getSettings().getUserAgentString()
                + " "
                + getString(R.string.user_agent_suffix));
        mWebview.addJavascriptInterface(new WebAppInterface(this,this.mWebview,this.target_url), "Android");
        mWebview.loadUrl(target_url);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mContext=this.getApplicationContext();
    }

    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            //Log.d("shouldOverrideUrlLoading", url);
            if (host.equals(target_url_prefix))
            {
                // This is my web site, so do not override; let my WebView load
                // the page
                if(mWebviewPop!=null)
                {
                    mWebviewPop.setVisibility(View.GONE);
                    mWebviewPop=null;
                }
                return false;
            }

            if(host.equals("m.facebook.com"))
            {
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch
            // another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
//            Log.d("onReceivedSslError", "onReceivedSslError");
            //super.onReceivedSslError(view, handler, error);
        }
    }

    class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            mWebviewPop = new WebView(mContext);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();
            return true;
        }
        @Override
        public void onCloseWindow(WebView window) {
            Log.d("onCloseWindow", "called");
        }

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    /*@Override
    public void onLocationChanged(Location location) {
        Globals.longitude = String.valueOf(location.getLongitude());
        Globals.latitude = String.valueOf(location.getLatitude());
        Log.v("GPS NOT WORKING ?", "IN ON LOCATION CHANGE, lat=" + Globals.latitude + ", lon=" + Globals.longitude);
        Toast.makeText(this, "YOU ARE MOVING",
                Toast.LENGTH_SHORT).show();
        if (Globals.mobilePhone != null && Globals.password != null) {
            this.mWebview.post(new Runnable() {
                @Override
                public void run() {
                    mWebview.loadUrl("javascript:js_android_getGeo()");
                }
            });
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.startActivity(intent);
        Toast.makeText(this, "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }*/

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        System.out.println("onConnected fired");
        launchLocalization();
    }

    private void launchLocalization() {
        System.out.println("isgoogle co?");
        if (mGoogleApiClient.isConnected()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Globals.longitude = String.valueOf(mLastLocation.getLongitude());
                Globals.latitude = String.valueOf(mLastLocation.getLatitude());
            }
            if (Globals.mobilePhone != null && Globals.password != null) {
                mWebview.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebview.loadUrl("javascript:js_android_getGeo()");
                    }
                });
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
}
