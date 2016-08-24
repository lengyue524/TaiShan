package com.lengyue524.taishan;

import android.graphics.Bitmap;

public class FirstCompress extends LubanCompress implements IGear {
    @Override
    public byte[] compress(IImageInfo imageInfo) {
        int minSize = 60;
        int longSide = 720;
        int shortSide = 1280;

        long size = 0;
        long maxSize = imageInfo.getSize() / 5;

        int angle = imageInfo.getImageSpinAngle();
        int imageWidth = imageInfo.getWidth();
        int imgHeight = imageInfo.getHeight();
        int width = 0, height = 0;
        if (imageWidth <= imgHeight) {
            double scale = (double) imageWidth / (double) imgHeight;
            if (scale <= 1.0 && scale > 0.5625) {
                width = imageWidth > shortSide ? shortSide : imageWidth;
                height = width * imgHeight / imageWidth;
                size = minSize;
            } else if (scale <= 0.5625) {
                height = imgHeight > longSide ? longSide : imgHeight;
                width = height * imageWidth / imgHeight;
                size = maxSize;
            }
        } else {
            double scale = (double) imgHeight / (double) imageWidth;
            if (scale <= 1.0 && scale > 0.5625) {
                height = imgHeight > shortSide ? shortSide : imgHeight;
                width = height * imageWidth / imgHeight;
                size = minSize;
            } else if (scale <= 0.5625) {
                width = imageWidth > longSide ? longSide : imageWidth;
                height = width * imgHeight / imageWidth;
                size = maxSize;
            }
        }
        Bitmap bitmap = compressImage(imageInfo, width, height);
        return toByte(bitmap, angle, size);
    }
}
