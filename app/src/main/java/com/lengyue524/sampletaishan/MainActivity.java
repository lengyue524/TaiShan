package com.lengyue524.sampletaishan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lengyue524.rxtaishan.RxTaiShan;
import com.lengyue524.taishan.BitmapInfo;
import com.lengyue524.taishan.FileInfo;
import com.lengyue524.taishan.TaiShan;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.functions.Function;
import me.iwf.photopicker.PhotoPicker;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    private TextView fileSize;
    private TextView imageSize;
    private TextView thumbFileSize;
    private TextView thumbImageSize;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fileSize = (TextView) findViewById(R.id.file_size);
        imageSize = (TextView) findViewById(R.id.image_size);
        thumbFileSize = (TextView) findViewById(R.id.thumb_file_size);
        thumbImageSize = (TextView) findViewById(R.id.thumb_image_size);
        image = (ImageView) findViewById(R.id.image);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(MainActivity.this, PhotoPicker.REQUEST_CODE);

            }
        });
    }

    /**
     * 压缩单张图片 AsyncTask 方式
     */
    private void compressWithAT(Bitmap bitmap) {
        CompressTask task = new CompressTask();
        task.execute(bitmap);
    }

    private class CompressTask extends AsyncTask<Bitmap, Object, byte[]> {

        @Override
        protected byte[] doInBackground(Bitmap... bitmaps) {
            return TaiShan.get().load(new BitmapInfo(bitmaps[0])).launch();
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            showResult(bytes);
        }
    }

    /**
     * 压缩单张图片 RxJava 方式
     */
    private void compressWithRx(File file) {
        RxTaiShan.get()
                .load(new FileInfo(file))
                .putGear(TaiShan.THIRD_GEAR)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .onErrorResumeNext(new Function<Throwable, Observable<? extends byte[]>>() {
                    @Override
                    public Observable<? extends byte[]> apply(Throwable throwable) {
                        return Observable.empty();
                    }
                })
                .subscribe(new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] bytes) {
                        showResult(bytes);
                    }
                });
    }

    /**
     * 压缩单张图片 RxJava 方式
     */
    private void compressWithRx(Bitmap bitmap) {
        RxTaiShan.get()
                .load(new BitmapInfo(bitmap))
                .putGear(TaiShan.THIRD_GEAR)
                .asObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .onErrorResumeNext(new Function<Throwable, Observable<? extends byte[]>>() {
                    @Override
                    public Observable<? extends byte[]> apply(Throwable throwable) {
                        return Observable.empty();
                    }
                })
                .subscribe(new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] bytes) {
                        showResult(bytes);
                    }
                });
    }

    private void showResult(byte[] bytes) {
        String path = MainActivity.this.getCacheDir() + File.separator + System.currentTimeMillis();
        File file = TaiShan.saveImage(path, bytes);
        Glide.with(MainActivity.this).load(file).into(image);

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        MainActivity.this.sendBroadcast(intent);

        thumbFileSize.setText(file.length() / 1024 + "k");
        FileInfo fileInfo = new FileInfo(file);
        thumbImageSize.setText(fileInfo.getWidth() + " * " + fileInfo.getHeight());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);

                File imgFile = new File(photos.get(0));
                fileSize.setText(imgFile.length() / 1024 + "k");
                FileInfo fileinfo = new FileInfo(imgFile);
                imageSize.setText(fileinfo.getWidth() + " * " + fileinfo.getHeight());

                //compressWithRx(new File(photos.get(0)));
                compressWithRx(BitmapFactory.decodeFile(photos.get(0)));
//                compressWithAT(BitmapFactory.decodeFile(photos.get(0)));
            }
        }
    }
}
