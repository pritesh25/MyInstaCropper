package com.selfie.life.myinstacropper;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.selfie.life.myinstacropper.CropLib.ImageCallbackActivity;
import com.selfie.life.myinstacropper.Filter.MainFilterActivity;
import com.selfie.life.myinstacropper.SqaureCamera.CameraActivity;
import com.selfie.life.myinstacropper.SqaureCamera.ImageUtility;

public class MainActivity extends AppCompatActivity {

    //editted by patil

    private static final String TAG = MainActivity.class.getSimpleName();
    Button button,capturebutton;

    private static final int     REQUEST_CAMERA              = 10;
    private static final int     REQUEST_CAMERA_PERMISSION   = 11;
    private static final int     REQUEST_IMAGE_FILTER        = 2;
    private static final int     REQUEST_MAIN_IMAGE_CROP     = 3;
    private static final int     REQUEST_IMAGE_CROP_RESULT   = 4;
    private static final int     REQUEST_ADD_MEMBER          = 5;
    private Point mSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        mSize = new Point();
        display.getSize(mSize);

        button = findViewById(R.id.button);
        capturebutton = findViewById(R.id.capturebutton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), ImageCallbackActivity.class), 100);
            }
        });

        capturebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestForCameraPermission();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "callback is done, RESULT_OK = " + data.getStringExtra("MESSAGE"));

                    ((ImageView) findViewById(R.id.image)).setImageBitmap(BitmapFactory.decodeFile(data.getStringExtra("MESSAGE")));

                } else {
                    Log.d(TAG, "callback is done, else executed");
                }
                break;

            case REQUEST_CAMERA:

                Intent intent = new Intent(getApplicationContext(),MainFilterActivity.class);
                intent.setData(data.getData());
                startActivityForResult(intent,3);

                break;

            case 3:
                if (resultCode == RESULT_OK)
                {
                    String imagePath = data.getStringExtra("MESSAGE");
                    Log.d(TAG,"requestCode 3 if executed , imagePath = "+imagePath);
                    Log.d(TAG,"requestCode 3 if executed , real path = "+getRealPathFromURI(getApplicationContext(),Uri.parse(imagePath)));
                    Bitmap bitmap = ImageUtility.decodeSampledBitmapFromPath(getRealPathFromURI(getApplicationContext(),Uri.parse(imagePath)), mSize.x, mSize.x);
                    ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
                }
                else
                {
                    Log.d(TAG,"requestCode 3 else executed");
                }
                break;
            default:

                break;
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CAMERA) {
            Uri photoUri = data.getData();
            // Get the bitmap in according to the width of the device
            Bitmap bitmap = ImageUtility.decodeSampledBitmapFromPath(photoUri.getPath(), mSize.x, mSize.x);
            ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    public void requestForCameraPermission() {
        final String permission = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                showPermissionRationaleDialog("Test", permission);
            } else {
                requestForPermission(permission);

            }
        } else {

            launch();
        }
    }

    private void showPermissionRationaleDialog(final String message, final String permission) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.requestForPermission(permission);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void requestForPermission(final String permission) {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, REQUEST_CAMERA_PERMISSION);
    }

    private void launch() {
        Intent startCustomCameraIntent = new Intent(this, CameraActivity.class);
        startActivityForResult(startCustomCameraIntent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                final int numOfRequest = grantResults.length;
                final boolean isGranted = numOfRequest == 1
                        && PackageManager.PERMISSION_GRANTED == grantResults[numOfRequest - 1];
                if (isGranted) {
                    launch();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}