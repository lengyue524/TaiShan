package com.lengyue524.taishan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by lihang1@yy.com on 2016/8/8.
 */
public class BitmapInfo implements IImageInfo {
    private Bitmap mBitmap;

    public BitmapInfo(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    @Override
    public int getImageSpinAngle() {
        return 0;
    }

    @Override
    public int getWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return mBitmap.getHeight();
    }

    @Override
    public int getSize() {
        return mBitmap.getByteCount();
    }

    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        return BitmapFactory.decodeStream(is, null, options);
    }

    @Override
    public byte[] getBytes() {
        return TaiShan.toByte(mBitmap, 0, Long.MAX_VALUE);
    }
}
