package model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import com.example.dan.spont.MainActivity;

import java.io.File;

/**
 * Created by ramani on 07/09/2015.
 */
public class WebAppInterface {
    private Context mContext;
    private final static int REQUEST_FILE_PICKER=1;
    private final static int TAKE_A_PIC=2;

    public WebAppInterface(Context mContext_) {
        this.mContext = mContext_;
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
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/Spont");
                    myDir.mkdirs();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File current_file = new File(android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Spont", "temp.jpg");
                    Uri uriImage = Uri.fromFile(current_file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
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
}
