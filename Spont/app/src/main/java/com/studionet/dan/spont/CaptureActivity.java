/*
* edited by Tahina on 07/09/2015 11:49
* add addJavascriptInterface to bind javascript with Android class "WebAppInterface"
* */

package com.studionet.dan.spont;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import Task.FileUploaderTask;
import Task.Helper;
import model.WebAppInterface;


public class CaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera.PictureCallback jpegCallback;
    private boolean takingPicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_activity);
        this.surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        this.surfaceHolder = surfaceView.getHolder();
        this.surfaceHolder.addCallback(this);
        this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    String tmpName = String.format("/sdcard/%d.jpg", System.currentTimeMillis());
                    outStream = new FileOutputStream(tmpName);
                    outStream.write(data);
                    outStream.close();
                    //rotate the original file
                    Helper.rotateBitmap(tmpName);
                    Intent output = new Intent();
                    output.putExtra("imagePath",tmpName);
                    setResult(RESULT_OK, output);
                    finish();
                    Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
                takingPicture = false;
                refreshCamera();
            }
        };
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            this.camera = Camera.open();
        } catch (RuntimeException e) {
            System.err.println(e);
            return;
        }
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width,selected.height);
        camera.setParameters(params);
        try {
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void captureImage(View v) throws IOException {
        if (this.takingPicture == false) {
            camera.takePicture(null, null, jpegCallback);
        }
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {
        }
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
        }
    }
}
