package com.lengyue524.taishan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public interface IImageInfo {
    int getImageSpinAngle();

    int getWidth();

    int getHeight();

    int getSize();

    Bitmap decode(BitmapFactory.Options options);

    byte[] getBytes();
}
