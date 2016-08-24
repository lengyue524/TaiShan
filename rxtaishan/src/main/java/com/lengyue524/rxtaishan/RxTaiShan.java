package com.lengyue524.rxtaishan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.lengyue524.taishan.IGear;
import com.lengyue524.taishan.IImageInfo;
import com.lengyue524.taishan.TaiShan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lihang1@yy.com on 2016/8/15.
 */
public class RxTaiShan {
    private TaiShan mTaiShan;

    private OnCompressListener compressListener;

    public RxTaiShan() {
        mTaiShan = new TaiShan();
    }

    public static RxTaiShan get() {
        return new RxTaiShan();
    }

    public RxTaiShan load(IImageInfo imageInfo) {
        mTaiShan.load(imageInfo);
        return this;
    }

    public RxTaiShan putGear(int gear) {
        mTaiShan.putGear(gear);
        return this;
    }

    public RxTaiShan putGear(IGear gear) {
        mTaiShan.putGear(gear);
        return this;
    }

    public void setCompressListener(OnCompressListener compressListener) {
        this.compressListener = compressListener;
    }

    public void launch() {
        if (compressListener != null) {
            compressListener.onStart();
        }
        asObservable().observeOn(Schedulers.computation())
                .subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<byte[]>() {
            @Override
            public void call(byte[] bytes) {
                if (compressListener != null) {
                    compressListener.onSuccess(bytes);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (compressListener != null) {
                    compressListener.onError(throwable);
                }
            }
        });
    }

    public Observable<byte[]> asObservable() {
        return Observable.create(new Observable.OnSubscribe<byte[]>() {
            @Override
            public void call(Subscriber<? super byte[]> subscriber) {
                subscriber.onNext(mTaiShan.launch());
            }
        });
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
