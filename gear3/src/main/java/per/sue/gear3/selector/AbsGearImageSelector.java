package per.sue.gear3.selector;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import per.sue.gear3.R;
import per.sue.gear3.manager.StorageManager;
import per.sue.gear3.manager.ToastManager;
import per.sue.gear3.net.progress.ProgressRequestListener;
import per.sue.gear3.presenter.GearFileUploadPresenter;
import per.sue.gear3.utils.GearLog;

/**
 * Created by sure on 2017/12/4.
 */

public abstract class AbsGearImageSelector implements IGearImageSelector {
     int SDK_VERSION = android.os.Build.VERSION.SDK_INT;


     //"com.thinkingark.hyj.fileprovider"
    String fileProviderKey = ".fileprovider";

    public static final int REQUEST_CODE_CAMERA = 13563;
    public static final int REQUEST_CODE_GALLERY = 13564;
    public static final int REQUEST_CODE_CHOOSE = 13565;
    public static final int PHOTO_REQUEST_CROP = 13826;

    public boolean isCrop;
    public int cropOutPutX = 200;
    public int cropOutPutY = 200;
    public  int maxSelectableCount = 1;
    int maxCompress =  100;//这个数字不准的，

    protected File outFile;

    OnImageSelectListener onImageSelectListener;
    GearFileUploadPresenter gearFileUploadPresenter;
    private  WeakReference<Activity> activity;
    private  WeakReference<Fragment> fragment;

    public AbsGearImageSelector(Activity  activity) {
        this.activity =  new WeakReference<>(activity);;
        this.fileProviderKey = activity.getPackageName() + ".fileprovider";
    }

    public AbsGearImageSelector(Fragment fragment) {
        this.fragment =  new WeakReference<>(fragment);;
        Activity activity = fragment.getActivity();
        this.fileProviderKey = activity.getPackageName() + ".fileprovider";
        this.activity =new WeakReference<>(activity);;
    }

    public Uri imageContentUri24(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = activity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return activity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    protected Uri imageContentUri(File file){
        Uri uri;
        if(SDK_VERSION < 24){
            uri = Uri.fromFile(file);
        }else{
            uri = imageContentUri24(file);
        }
        return uri;
    }

    protected void toCrop(File  file) {
        Uri uri = imageContentUri(file);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        if(0 != cropOutPutX && 0 != cropOutPutY){
            intent.putExtra("aspectX", cropOutPutX);  //裁剪的尺寸比例
            intent.putExtra("aspectY", cropOutPutY);
            intent.putExtra("outputX",  cropOutPutX);      // outputX,outputY 是剪裁图片的宽高
            intent.putExtra("outputY",  cropOutPutY);
        }
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,  Uri.fromFile(outFile));
        intent.putExtra("return-data", false);//设置为不返回数据 , 因为如果输出尺寸太大会闪退
        if(null != fragment()){
            fragment().startActivityForResult(intent, PHOTO_REQUEST_CROP);
        }else{
            activity().startActivityForResult(intent, PHOTO_REQUEST_CROP);
        }
    }

    public interface IImageSelectListener{
        public void onSuccessGetImage(List<String> list);
        public void onSuccessGetImage(String path);
        public void onSuccessUploadImage(String result);
        public void onError(String message);
    }

    public abstract static class OnImageSelectListener implements IImageSelectListener{
        public void onSuccessGetImage(List<String> list){
        }

        public void onSuccessGetImage(String path){

        }

        @Override
        public void onError(String message) {
            ToastManager.instance().error(message);
        }
    }

    public AbsGearImageSelector imageSelectListener(OnImageSelectListener onImageSelectListener){
        this.onImageSelectListener = onImageSelectListener;
        return this;
    }

    public AbsGearImageSelector cropOutSize(int x, int y){
        this.cropOutPutX = x;
        this.cropOutPutY = y;
        return  this;
    }

    public AbsGearImageSelector maxSelectableCount(int maxSelectableCount) {
        this.maxSelectableCount = maxSelectableCount;
        return  this;
    }

    public AbsGearImageSelector isCrop(boolean isCrop){
        this.isCrop = isCrop;
        return  this;
    }


    public File compressImage(File file) {
        long length = file.length();
        Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 80;
        while (baos.toByteArray().length / 1024 > maxCompress && options >  10) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            length = baos.toByteArray().length;
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            length = baos.toByteArray().length;
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        File fileTmp = StorageManager.getInstance().createImgFile(activity());
       ; try {
            FileOutputStream fileOutputStream = null;

            fileOutputStream = new FileOutputStream(fileTmp);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //file.delete();
         length = fileTmp.length();
        return  fileTmp;
    }

    public AbsGearImageSelector uploadAPI(GearFileUploadPresenter.IFileUploadApi iFileUploadApi){
        this.gearFileUploadPresenter = new GearFileUploadPresenter(iFileUploadApi);
        this.gearFileUploadPresenter.setView(new GearFileUploadPresenter.View() {
            @Override
            public void onErrorUploadFile(int code, String message) {
                if(null != gearCustomDialog){
                    gearCustomDialog.dismiss();
                }
                if(null != onImageSelectListener){
                    onImageSelectListener.onError(message);
                }
            }

            @Override
            public void onSuccessUploadFile(String result) {
                if(null != gearCustomDialog){
                    gearCustomDialog.dismiss();
                }
                if(null != onImageSelectListener){
                    onImageSelectListener.onSuccessUploadImage(result);
                }
            }
        });
        return this;
    }

    NumberProgressBar gearProgressBar;
    AlertDialog gearCustomDialog;
    protected void showUploadProgressDialog() {
        View view = LayoutInflater.from(activity()).inflate(R.layout.view_progressbar, null);
        gearProgressBar = (NumberProgressBar) view.findViewById(R.id.number_progress_bar);
        gearCustomDialog = new AlertDialog.Builder(activity())
                .setTitle("正在上传")
                .setView(view)
                .create();

        gearCustomDialog.show();
        gearCustomDialog.setCanceledOnTouchOutside(false);
    }

    ProgressRequestListener progressRequestListener = new ProgressRequestListener() {
        @Override
        public void onRequestProgress(final long bytesWritten, final long contentLength, final boolean done) {
            activity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GearLog.e("onResponseProgress", "pro=" + bytesWritten + "   max =" + contentLength);
                    if (null != gearProgressBar) {
                        int baifen = (int) ((bytesWritten * 100) / contentLength);
                        GearLog.e("onResponseProgress", "baifen=" + baifen);
                        if (baifen > 0){
                            gearProgressBar.setProgress(baifen);
                        }

                    }
                }
            });

        }
    };

    public Activity activity(){
        return  activity.get();
    };
    public Fragment fragment(){
        return  null == fragment? null : fragment.get();
    }

    //BitmapUtils.decodeSampledBitmapFromBitmap(bitmap, sizeX, sizeY);
}
