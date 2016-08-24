package com.lengyue524.taishan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;

/**
 * Created by lihang1@yy.com on 2016/8/8.
 */
public class FileInfo implements IImageInfo {
    private File mFile;
    private int width;
    private int height;

    public FileInfo(String filePath) {
        mFile = new File(filePath);
        init();
    }

    public FileInfo(File file) {
        mFile = file;
        init();
    }

    @Override
    public int getImageSpinAngle() {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(mFile.getAbsolutePath());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getSize() {
        return (int) mFile.length();
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
    }

    @Override
    public byte[] getBytes() {
        return TaiShan.toByte(decode(new BitmapFactory.Options()), getImageSpinAngle(), Long.MAX_VALUE);
    }

    private void init() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);

        width = options.outWidth;
        height = options.outHeight;
    }
}
