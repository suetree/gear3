package per.sue.gear3.selector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import java.io.File;

import per.sue.gear3.R;
import per.sue.gear3.manager.StorageManager;

/**

 */
public class GearImageDefaultSelector extends AbsGearImageSelector {


    public GearImageDefaultSelector(Activity activity) {
        super(activity);
    }

    public GearImageDefaultSelector(Fragment fragment) {
        super(fragment);
    }

    public void toCamera(){
        outFile = StorageManager.getInstance().createImgFile(activity());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(SDK_VERSION < 24){
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));
        }else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile( activity(), fileProviderKey, outFile));
        }
        if(null != this.fragment()){
            fragment().startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }else{
            activity().startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }

    }

    public void toGallery(){
        outFile = StorageManager.getInstance().createImgFile(activity());
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageContentUri(outFile));
        if(null != this.fragment()){
            fragment().startActivityForResult(intent, REQUEST_CODE_GALLERY);
        }else{
            activity().startActivityForResult(intent, REQUEST_CODE_GALLERY);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {//拍照返回
                if (isCrop) {
                    File file = outFile;
                    outFile  = StorageManager.getInstance().createImgFile(activity());
                    toCrop(file);
                } else {
                    result();
                }
            } else if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {//相册返回
                Uri selectedImage = data.getData();
                String scheme = selectedImage.getScheme();
                String picturePath;
                if ("content".equals(scheme)) {
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = this.activity().getContentResolver().query(selectedImage, filePathColumns,  MediaStore.Images.Media.SIZE + ">=30720", null, null);//查找出尺寸大于30kb的图片
                if(c.getCount() > 0) {
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                     picturePath = c.getString(columnIndex);
                    c.close();
                }else{
                    picturePath = selectedImage.getPath();
                }
                  Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    if(null != bitmap){
                        if (isCrop) {
                            toCrop(new File(picturePath));
                        } else {
                            outFile = new File(picturePath);
                            result();
                        }
                    }else {
                        onImageSelectListener.onError("该图片获取失败, 请尝试其他图片");
                    }
                }else{
                    onImageSelectListener.onError("选择图片尺寸不能小于30kb!");
                }
            } else if (requestCode == PHOTO_REQUEST_CROP && resultCode == Activity.RESULT_OK) {
                //截图返回
                result();
            }
    }

    private void result(){
        if(null != gearFileUploadPresenter){
            showUploadProgressDialog();
            gearFileUploadPresenter.uploadFile(outFile, progressRequestListener);
        }else{
            if(null != onImageSelectListener){
                onImageSelectListener.onSuccessGetImage(outFile.getPath() );
            }
        }
    }




    @Override
    public void selectorImage() {
        LayoutInflater inflater = LayoutInflater.from(activity());
        View view = inflater.inflate(R.layout.view_dialog_avatr, null);
        final Dialog dialog = new  AlertDialog.Builder(activity())
                .setView(view)
               // .setBottomUp(true)
                .create();
        view.findViewById(R.id.ViewDialogCamera).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
                toCamera();
            }
        });
        view.findViewById(R.id.ViewDialogGallery).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                toGallery();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.ViewDialogDismiss).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }




/*
    public static ImageCutBean getImageCutBeanByProportion(Activity activity,  int proportionWidth, int proportionHeight ){
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        double proportion = ((double)proportionHeight)/proportionWidth;
        ImageCutBean  imageCutBean = new ImageCutBean(metric.widthPixels, (int)(metric.widthPixels * proportion));
        return imageCutBean;
    }*/




}
