/*
* edited by Tahina on 07/09/2015 11:49
* add addJavascriptInterface to bind javascript with Android class "WebAppInterface"
* */

package com.example.dan.spont;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;
import model.WebAppInterface;


public class MainActivity extends AppCompatActivity {
    /* URL saved to be loaded after fb login */
    private static final String target_url="http://app.spont.fr";
    private static final String target_url_prefix="app.spont.fr";
    private static final int FILECHOOSER_RESULTCODE = 1;
    private Context mContext;
    private WebView mWebview;
    private WebView mWebviewPop;
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent intent)
    {
        if (resultCode == FILECHOOSER_RESULTCODE)
        {
            Toast.makeText(mContext, "return : gallerie", Toast.LENGTH_SHORT).show();
            mWebview.loadUrl("javascript:changeThumb('rien')");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // final View controlsView =
        // findViewById(R.id.fullscreen_content_controls);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        mWebview = (WebView) findViewById(R.id.webview);
        //mWebviewPop = (WebView) findViewById(R.id.webviewPop);
        WebSettings webSettings = mWebview.getSettings();
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.setInitialScale(1);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        mWebview.setWebViewClient(new UriWebViewClient());
        mWebview.addJavascriptInterface(new WebAppInterface(this), "Android");
        mWebview.setWebChromeClient(new UriChromeClient());
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
            Log.d("onReceivedSslError", "onReceivedSslError");
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
}



