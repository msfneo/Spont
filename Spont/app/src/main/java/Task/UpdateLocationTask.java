package Task;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.WebView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import model.Globals;

/**
 * Created by ramani on 07/09/2015.
 */
public class UpdateLocationTask extends AsyncTask<String, String, String> {
    private String longitude;
    private String latitude;
    private String base_url;

    public UpdateLocationTask(String baseUrl_,String longitude, String latitude) {
        this.base_url = baseUrl_;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    protected String doInBackground(String... params) {
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("longitude", this.longitude));
        parameters.add(new BasicNameValuePair("latitude", this.latitude));
        parameters.add(new BasicNameValuePair("mobilePhone", Globals.mobilePhone));
        parameters.add(new BasicNameValuePair("password", Globals.password));
        Helper.fetchAndSendDatas(base_url+"/android_getGeo", parameters);
        return null;
    }
}
