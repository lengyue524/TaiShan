package com.lengyue524.taishan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TaiShan {
    public static final int FIRST_GEAR = 1;
    public static final int THIRD_GEAR = 3;
    private int mGearNum;
    private IGear mGear = new ThirdCompress();
    private IImageInfo mImageInfo;

    public static TaiShan get() {
        return new TaiShan();
    }

    public TaiShan load(IImageInfo imageInfo) {
        mImageInfo = imageInfo;
        return this;
    }

    public TaiShan putGear(int gear) {
        mGearNum = gear;
        return this;
    }

    public TaiShan putGear(IGear gear) {
        mGear = gear;
        return this;
    }

    private IGear getGear() {
        if (FIRST_GEAR == mGearNum) {
            return new FirstCompress();
        }
        if (THIRD_GEAR == mGearNum) {
            return new ThirdCompress();
        }
        if (mGear == null) {
            return new ThirdCompress();
        }
        return mGear;
    }

    public byte[] launch(){
        return getGear().compress(mImageInfo);
    }

    /**
     * 旋转图片
     * rotate the image with specified angle
     *
     * @param angle  the angle will be rotating 旋转的角度
     * @param bitmap target image               目标图片
     */
    private static Bitmap rotatingImage(int angle, Bitmap bitmap) {
        //rotate image
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Bitmap转换为字节数组
     *
     * @param bitmap the image what be save   目标图片
     * @param angle  rotation angle of thumbnail
     * @param size   the file size of image   期望大小
     * @return
     */
    public static byte[] toByte(Bitmap bitmap, int angle, long size) {
        bitmap = rotatingImage(angle, bitmap);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);

        while (stream.toByteArray().length / 1024 > size && options > 6) {
            stream.reset();
            options -= 6;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream);
        }
        return stream.toByteArray();
    }

    /**
     * 保存图片到指定路径
     * Save image with specified size
     *
     * @param bytes 图片字节数组
     */
    public static File saveImage(String path, byte[] bytes) {
        File result = new File(path.substring(0, path.lastIndexOf("/")));
        if (!result.exists() && !result.mkdirs()) return null;

        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(path);
    }

    public static Bitmap toBitmap(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return BitmapFactory.decodeStream(byteArrayInputStream);
    }
}
