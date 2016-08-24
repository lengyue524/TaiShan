# TaiShan

Luban的重构版本，感谢Luban作者提供的算法，此项目中含有大量Luban的原始代码。

本人只做了整体架构的重构。

原地址：https://github.com/Curzibn/Luban

# 与Luban的差异

1. 抽离RxJava，方便非RxJava项目的使用。
2. 可直接压缩Bitmap，压缩过程中不产生临时文件。
3. 修复同时压缩多个图片，回调冲突bug。
4. 可自定义压缩算法。

# 使用方法

## 直接使用

```
compile 'com.github.lengyue524.TaiShan:taishan:0.1.1'
```

```java
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
            String path = MainActivity.this.getCacheDir() + File.separator + System.currentTimeMillis();
            File file = TaiShan.saveImage(path, bytes);// 保存文件
            Bitmap bitmap = TaiShan.toBitmap(bytes);// 转换为Bitmap
        }
    }
```

## RxJava

```
compile 'com.github.lengyue524.TaiShan:taishan:0.1.1'
compile 'com.github.lengyue524.TaiShan:rxtaishan:0.1.1'
```

### Rxjava Listener方式

```java
RxTaiShan.get()
    .load(File)                     //传人要压缩的图片
    .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
    .setCompressListener(new OnCompressListener() { //设置回调

        @Override
        public void onStart() {
            //TODO 压缩开始前调用，可以在方法内启动 loading UI
        }
        @Override
        public void onSuccess(bytes[] bytes) {
            //TODO 压缩成功后调用，返回压缩后的图片字节码
        }

        @Override
        public void onError(Throwable e) {
            //TODO 当压缩过去出现问题时调用
        }
    }).launch();    //启动压缩
```

### Rxjava Observable方式

```java
//直接压缩Bitmap
    RxTaiShan.get()
            .load(new BitmapInfo(bitmap))// 压缩Bitmap
            // .load(new FileInfo(file)) // 压缩文件
            .putGear(TaiShan.THIRD_GEAR)// 默认同Luban第三档
            //.putGear(new IGear)// 自定义压缩算法
            .asObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                }
            })
            .onErrorResumeNext(new Func1<Throwable, Observable<? extends byte[]>>() {
                @Override
                public Observable<? extends byte[]> call(Throwable throwable) {
                    return Observable.empty();
                }
            })
            .subscribe(new Action1<byte[]>() {
                @Override
                public void call(byte[] bytes) {
                  String path = MainActivity.this.getCacheDir() + File.separator + System.currentTimeMillis();
                  File file = TaiShan.saveImage(path, bytes);// 保存文件
                  Bitmap bitmap = TaiShan.toBitmap(bytes);// 转换为Bitmap
                }
            });
```
