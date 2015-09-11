package Task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.studionet.dan.spont.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by ramani on 08/09/2015.
 */
public class Helper {

    public static String fetchAndSendDatas(String address, List<NameValuePair> parameters) {
        System.out.println("CAL URL=>"+address);
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpGet = new HttpPost(address);
        try{
            httpGet.setEntity(new UrlEncodedFormEntity(parameters));
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
                System.out.println("Received=>"+builder.toString());
            } else {
                Log.e(MainActivity.class.toString(), "Failed to get JSON object code" + statusCode);
                return null;
            }
        }catch(ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static void rotateBitmap(String pathImage) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1; //1/4 of the original image

        Bitmap originalImage = BitmapFactory.decodeFile(pathImage, options);
        int tempW = originalImage.getWidth();
        int tempH = originalImage.getHeight();

        if (tempW > tempH) {
            File file = new File(pathImage); // the File to save to
            OutputStream fOut = null;
            try {
                fOut = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Matrix mtx = new Matrix();
            mtx.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(originalImage, 0, 0, tempW, tempH, mtx, true);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            try {
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
