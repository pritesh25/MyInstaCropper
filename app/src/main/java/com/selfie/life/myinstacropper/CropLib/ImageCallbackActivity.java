package com.selfie.life.myinstacropper.CropLib;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.selfie.life.myinstacropper.Filter.MainFilterActivity;
import com.selfie.life.myinstacropper.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCallbackActivity extends AppCompatActivity {

    private static final String TAG = ImageCallbackActivity.class.getSimpleName();

    private InstaCropperView mInstaCropper;

    private Button pickPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instacallback);

        getSupportActionBar().hide();

        mInstaCropper   = findViewById(R.id.instacropper);

        pickPhoto       = findViewById(R.id.pickPhoto);
        pickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageBrowse();
            }
        });

        ImageBrowse();

    }

    private void ImageBrowse() {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {

                  //  Uri selectedImage = data.getData();

                    Intent intent = ImageCropperActivity.getIntent(this, data.getData(), Uri.fromFile(new File(getExternalCacheDir(), "test.jpg")), 720, 50);
                    startActivityForResult(intent, 2);
                }
                else
                {
                    Log.d(TAG,"requestCode 1 is not executed , finish called , since no image is selected");
                    finish();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    // MyConfiguration.setPreferences(getApplicationContext(),"imagepath",data.getData().toString());
                    Log.d(TAG,"resultCode 2, data = "+data.getData());
                    Intent intent = new Intent(getApplicationContext(),MainFilterActivity.class);
                    intent.setData(data.getData());
                    startActivityForResult(intent, 3);
                    }
                else
                {
                    Log.d(TAG,"requestCode 2 is not executed , finish called , since no image is selected");
                    finish();
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG,"resultCode 21, data = "+data.getData());
                    mInstaCropper.setImageUri(data.getData());
                }
                else
                {
                    Log.d(TAG,"requestCode 2 is not executed , finish called , since no image is selected");
                    finish();
                }
                break;
            default:
                Log.d(TAG,"finish called , since no image is selected");
                finish();
            break;
        }
    }

    public void crop(View v) {

        Log.d(TAG,"cropped file path = "+getFile());

        mInstaCropper.crop(View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), new InstaCropperView.BitmapCallback()
        {
            @Override
            public void onBitmapReady(Bitmap bitmap)
            {
                if (bitmap == null) {
                    Toast.makeText(ImageCallbackActivity.this, "Returned bitmap is null.", Toast.LENGTH_SHORT).show();
                    return;
                }

                File file = getFile();
                try {

                    FileOutputStream os = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
                    os.flush();
                    os.close();
                    mInstaCropper.setImageUri(Uri.fromFile(file));
                    Log.d(TAG, "Image updated , final image path = "+file.toString());

                    Intent intent = getIntent();
                    intent.putExtra("message",file.toString());
                    setResult(RESULT_OK, intent);
                    finish();

                } catch (IOException e) {
                    Log.e(TAG, "Failed to compress bitmap = "+e.getMessage());
                }
            }


        });
    }

    private File getFile() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "instaCropper.jpg");
    }
}