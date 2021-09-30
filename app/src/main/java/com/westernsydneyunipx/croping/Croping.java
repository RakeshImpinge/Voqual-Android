package com.westernsydneyunipx.croping;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;
import com.westernsydneyunipx.voqual.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Croping extends AppCompatActivity {

    CropImageView cropImageView;
    Uri sourceUri;
    int source;
    String oldPath, imagePath;
    TextView tvCancel,tvtitle,tvDone;
    ImageView rotateleft,rotateright;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_croping);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvtitle = (TextView) findViewById(R.id.tvtitle);
        tvDone = (TextView) findViewById(R.id.tvDone);

        rotateleft=(ImageView)findViewById(R.id.rotate);
        rotateright=(ImageView)findViewById(R.id.rotateright);

        tvtitle.setText("Crop Image");

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneAction();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rotateleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);            }
        });
        rotateright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);

            }
        });
        if (getIntent().hasExtra("COVER"))
        {
            source = 101;
            File image = new File(getIntent().getStringExtra("COVER"));
            if (image.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("COVER"));
                cropImageView.setImageBitmap(bitmap);
                cropImageView.setCropMode(CropImageView.CropMode.CUSTOM);
                cropImageView.setCustomRatio(10,5);
            }
        }else if (getIntent().hasExtra("PROFILE"))
        {
            source = 102;
            File image = new File(getIntent().getStringExtra("PROFILE"));
            if (image.exists())
            {
                Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("PROFILE"));

//                cropImageView.setImageBitmap(bitmap);
                try {
                    cropImageView.setImageBitmap(modifyOrientation(bitmap,getIntent().getStringExtra("PROFILE")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
            }
        }
        else if (getIntent().hasExtra("SQUARE"))
        {
            source = 103;
            oldPath=getIntent().getStringExtra("SQUARE");
            File image = new File(oldPath);
            Log.d("ppppaathss",image.getPath()+"");
            if (image.exists())
            {

                Bitmap bitmap=BitmapFactory.decodeFile(oldPath);
                Log.d("bbiittmmaapp",bitmap+"");
                Log.d("bbiittmmaappoldPath",oldPath+"");
                Log.d("bbiittmmaapptoString",bitmap.toString());
                cropImageView.setImageBitmap(bitmap);

                cropImageView.setImageBitmap(bitmap);
                cropImageView.setHandleShowMode(CropImageView.ShowMode.SHOW_ALWAYS);
                cropImageView.setHandleSizeInDp((int) getResources().getDimension(R.dimen.margin_left_one));
                cropImageView.setCropMode(CropImageView.CropMode.FREE);
            }
        }
    }

    public void doneAction()
    {
        String path = saveToInternalStorage(cropImageView.getCroppedBitmap(),source);
        Log.d("Path",path);
        if (imagePath != null){
            Intent intent = new Intent();
            intent.putExtra("PATH",imagePath);
            setResult(1001,intent);
            finish();
        }
    }


    private String saveToInternalStorage(Bitmap bitmapImage, int source){
        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File mypath = null;
        if (source == 101){
            mypath=new File(dir, "tazmeniaCover.jpeg");
        }else if (source == 102){
            mypath=new File(dir,"tazprofile.jpg");
        }
        else if (source==103){
            mypath=new File(dir,"tazprofile.jpg");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("PATHA",mypath.getPath());
        imagePath = mypath.getPath();
        return dir.getAbsolutePath();
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}


