package com.westernsydneyunipx.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Constants {

    public static File makeFile(Bitmap bitmap, String name) {

        File file;
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        file = new File(dir, name);
        if (file.exists()) {
            file.delete();
        }
        file = new File(dir, name);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file.getPath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 00, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return file;
    }

    public static File compressFile(File file, String name) {

        Bitmap bitmap = shrinkBitmap(file.getPath(), 400, 400);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        file = new File(dir, name);
        if (file.exists()) {
            file.delete();
        }
        file = new File(dir, name);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file.getPath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return file;
    }
    public static Bitmap shrinkBitmap(String file, int width, int height) {

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }
}
