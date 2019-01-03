package com.abhi.questaway.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;
import com.abhi.questaway.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class FilePickerActivity extends BaseActivity {

    private File mFileCaptured;
    private Uri mImageCaptureUri;
    private ImagePickerListener imagePickerListener;
    private String tag;
    private static final int CAMERA_REQUEST_CODE = 10000;
    private static final int GALLERY_REQUEST_CODE = 20000;

    private android.app.AlertDialog alertDialog;

    public interface ImagePickerListener {
        void onImagePicked(File imageFile, String tag);
    }

    public void selectImage(final Context context, final ImagePickerListener imagePickerListener,
                            final String tag) {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery" };

        this.imagePickerListener = imagePickerListener;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        View dialoglayout = inflater.inflate(R.layout.dialog_image_chooser, null);
        builder.setView(dialoglayout);
        TextView txtCamera = dialoglayout.findViewById(R.id.txtCamera);
        TextView txtGallery = dialoglayout.findViewById(R.id.txtGallery);
        alertDialog = builder.create();

        txtCamera.setOnClickListener(v -> {
            alertDialog.dismiss();
            requestPermissions(Manifest.permission.CAMERA, 4, new PermissionListener() {
                @Override public void onGranted(int requestCode) {
                    if (requestCode == 4) {
                        pickFromCamera((Activity) context, imagePickerListener, tag);
                    }
                }

                @Override public void onRejected(int requestCode) {
                    return;
                }
            });
        });

        txtGallery.setOnClickListener(v -> {
            pickFromGallery((Activity) context, imagePickerListener, tag);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    public void pickFromCamera(Activity context, ImagePickerListener imagePickerListener,
                               String TAG) {
        this.tag = TAG;
        this.imagePickerListener = imagePickerListener;
        requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, 5, new PermissionListener() {
            @Override public void onGranted(int requestCode) {
                if (requestCode == 5) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        String state = Environment.getExternalStorageState();
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                                String appDirPath =
                                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "questaway";
                                File appDir = new File(appDirPath);
                                if (!appDir.exists()) {
                                    appDir.mkdirs();
                                }

                                mFileCaptured = new File(appDir, tag + ".jpg");
                                mImageCaptureUri = FileProvider.getUriForFile(context,
                                        context.getApplicationContext().getPackageName() + ".provider", mFileCaptured);
                            } else {
                                mFileCaptured = new File(Environment.getExternalStorageDirectory(), tag + ".jpg");
                                mImageCaptureUri = Uri.fromFile(mFileCaptured);
                            }
                        }

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);
                        context.startActivityForResult(intent, 10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override public void onRejected(int requestCode) {
                return;
            }
        });
    }

    public void pickFromGallery(Activity context, ImagePickerListener imagePickerListener,
                                String tag) {
        this.tag = tag;
        this.imagePickerListener = imagePickerListener;

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        context.startActivityForResult(Intent.createChooser(intent, "Select File"), 20000);
    }

    protected void onImageResult(Context context, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            int picWidth = 300;
            int picHeight = 300;
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap bm = null;
                FileOutputStream outputStream = null;
                try {
                    bm = getBitmapFromReturnedImage(context, mImageCaptureUri, picWidth, picHeight);
                    bm = fixOrientation(bm, mImageCaptureUri.getPath());

                    //fileProfilePic = new File(context.getFilesDir(), tag+".jpg");
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        outputStream = new FileOutputStream(mFileCaptured);
                    } else {
                        mFileCaptured = new File(context.getFilesDir(), tag + ".jpg");
                        outputStream = new FileOutputStream(mFileCaptured);
                    }
                    bm.compress(Bitmap.CompressFormat.PNG, 90, outputStream);

                    imagePickerListener.onImagePicked(mFileCaptured, tag);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //ivPhoto.setImageBitmap(bm);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Bitmap bm;
                if (data != null && context.getContentResolver().getType(data.getData()) != null) {
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String type =
                            mime.getExtensionFromMimeType(context.getContentResolver().getType(data.getData()));
                    if (type.equalsIgnoreCase("jpg")
                            || type.equalsIgnoreCase("jpeg")
                            || type.equalsIgnoreCase("bmp")
                            || type.equalsIgnoreCase("png")) {
                        FileOutputStream outputStream = null;
                        try {
                            //String mime = context.getContentResolver().getType(data.getData());

                            // bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                            bm = getBitmapFromReturnedImage(context, data.getData(), picWidth, picHeight);
                            bm = fixOrientation(bm, data.getData().getPath());

                            File fileProfilePic = new File(context.getFilesDir(), tag + ".jpg");
                            outputStream = new FileOutputStream(fileProfilePic);
                            bm.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
                            outputStream.flush();
                            outputStream.close();

                            imagePickerListener.onImagePicked(fileProfilePic, tag);
                        } catch (IOException e) {
                            Toast.makeText(context, "File format not supported", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } finally {
                            if (outputStream != null) {
                                try {
                                    outputStream.flush();
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "File format not supported", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "File format not supported", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Bitmap fixOrientation(Bitmap bm, String filePath) {
        Bitmap bitmap;
        ExifInterface ei;
        try {
            ei = new ExifInterface(filePath);
            int orientation =
                    ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bm, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bm, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bm, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    bitmap = bm;
                    break;
                default:
                    bitmap = bm;
                    break;
            }
            if (bitmap != null) {
                return bitmap;
            } else {
                return bm;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return bm;
        }
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Bitmap getBitmapFromReturnedImage(Context context, Uri selectedImage, int reqWidth,
                                             int reqHeight) throws IOException {

        InputStream inputStream = context.getContentResolver().openInputStream(selectedImage);

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // close the input stream
        inputStream.close();

        // reopen the input stream
        inputStream = context.getContentResolver().openInputStream(selectedImage);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
