package Task;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.webkit.WebView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import model.Globals;

/**
 * Created by ramani on 07/09/2015.
 */
public class FileUploaderTask extends AsyncTask<String, String, String> {
    private Context mContext;
    private String pathFile;
    private WebView mWebView;

    public FileUploaderTask(Context mContext_, String pathFile_, WebView mWebView_) {
        this.mContext = mContext_;
        this.pathFile = pathFile_;
        this.mWebView = mWebView_;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mWebView.loadUrl("javascript:loadingAnimation()");
    }

    @Override
    protected String doInBackground(String... params) {
        System.out.println(Globals.offerId);
        System.out.println(pathFile);
        HttpClient client = new DefaultHttpClient();
        File imageFile = new File(this.pathFile);
        System.out.println(imageFile.getAbsolutePath());
        HttpPost postRequest = new HttpPost ("http://app.spont.fr/upload_image/"+Globals.offerId);
        MultipartEntity multiPartEntity = new MultipartEntity () ;
        try {
            multiPartEntity.addPart("action", new StringBody("save-image")) ;
            multiPartEntity.addPart("mobilePhone", new StringBody(Globals.mobilePhone)) ;
            multiPartEntity.addPart("password", new StringBody(Globals.password)) ;
            multiPartEntity.addPart("android_exception", new StringBody("1")) ;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        FileBody fileBody = new FileBody(imageFile, imageFile.getName(), "image/jpeg","UTF-8") ;
        multiPartEntity.addPart("image", fileBody) ;
        postRequest.setEntity(multiPartEntity) ;
        return executeRequest(postRequest);
    }
    private String executeRequest(HttpRequestBase requestBase){
        String responseString = "" ;
        InputStream responseStream = null ;
        HttpClient client = new DefaultHttpClient () ;
        try{
            HttpResponse response = client.execute(requestBase) ;
            if (response != null){
                HttpEntity responseEntity = response.getEntity() ;

                if (responseEntity != null){
                    responseStream = responseEntity.getContent() ;
                    if (responseStream != null){
                        BufferedReader br = new BufferedReader (new InputStreamReader (responseStream)) ;
                        String responseLine = br.readLine() ;
                        System.out.println(responseLine);
                        String tempResponseString = "" ;
                        while (responseLine != null){
                            tempResponseString = tempResponseString + responseLine + System.getProperty("line.separator") ;
                            responseLine = br.readLine() ;
                        }
                        br.close() ;
                        if (tempResponseString.length() > 0){
                            responseString = tempResponseString;
                            return responseString;
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if (responseStream != null){
                try {
                    responseStream.close() ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        client.getConnectionManager().shutdown();
        return null;
    }

    @Override
    protected void onPostExecute(String urlImage) {
        super.onPostExecute(urlImage);
        this.mWebView.loadUrl("javascript:changeThumb('"+urlImage+"')");
    }
}
