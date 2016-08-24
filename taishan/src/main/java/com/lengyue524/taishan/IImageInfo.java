package com.lengyue524.taishan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by lihang1@yy.com on 2016/8/8.
 */
public interface IImageInfo {
    int getImageSpinAngle();

    int getWidth();

    int getHeight();

    int getSize();

    Bitmap decode(BitmapFactory.Options options);

    byte[] getBytes();
}
