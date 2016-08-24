package com.lengyue524.taishan;

import android.graphics.Bitmap;

/**
 * Created by lihang1@yy.com on 2016/8/15.
 */
public class ThirdCompress extends LubanCompress implements IGear {
    @Override
    public byte[] compress(IImageInfo imageInfo) {
        double size;

        int angle = imageInfo.getImageSpinAngle();
        int width = imageInfo.getWidth();
        int height = imageInfo.getHeight();
        int thumbW = width % 2 == 1 ? width + 1 : width;
        int thumbH = height % 2 == 1 ? height + 1 : height;

        int maxBorder = Math.max(width, height);//使用最大边进行比例缩放计算
        int minBorder = Math.min(width, height);

        double scale = ((double) minBorder / maxBorder);

        if (scale <= 1 && scale > 0.5625) {
            if (maxBorder < 1664) {
                if (imageInfo.getSize() / 1024 < 150) return imageInfo.getBytes();
                size = (width * height) / Math.pow(1664, 2) * 150;
                size = size < 60 ? 60 : size;
            } else if (maxBorder >= 1664 && maxBorder < 4990) {
                thumbW = width / 2;
                thumbH = height / 2;
                size = (thumbW * thumbH) / Math.pow(2495, 2) * 300;
                size = size < 60 ? 60 : size;
            } else if (maxBorder >= 4990 && maxBorder < 10240) {
                thumbW = width / 4;
                thumbH = height / 4;
                size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
                size = size < 100 ? 100 : size;
            } else {
                int multiple = maxBorder / 1280 == 0 ? 1 : maxBorder / 1280;
                thumbW = width / multiple;
                thumbH = height / multiple;
                size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
                size = size < 100 ? 100 : size;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            if (maxBorder < 1280 && imageInfo.getSize() / 1024 < 200) return imageInfo.getBytes();

            int multiple = maxBorder / 1280 == 0 ? 1 : maxBorder / 1280;
            thumbW = width / multiple;
            thumbH = height / multiple;
            size = (thumbW * thumbH) / (1440.0 * 2560.0) * 400;
            size = size < 100 ? 100 : size;
        } else {
            int multiple = (int) Math.ceil(maxBorder / (1280.0 / scale));
            thumbW = width / multiple;
            thumbH = height / multiple;
            size = ((thumbW * thumbH) / (1280.0 * (1280 / scale))) * 500;
            size = size < 100 ? 100 : size;
        }
        Bitmap bitmap = compressImage(imageInfo, thumbW, thumbH);
        return toByte(bitmap, angle, (long) size);
    }
}
