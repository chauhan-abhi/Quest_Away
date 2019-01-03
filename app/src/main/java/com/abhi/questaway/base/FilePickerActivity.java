package com.abhi.questaway.base;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private AlertDialog alertDialog;

    public interface ImagePickerListener {
        void onImagePicked(File imageFile, Bitmap bm, String tag);
    }

    public void selectImage(final ImagePickerListener imagePickerListener, final String tag) {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery" };

        this.imagePickerListener = imagePickerListener;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialoglayout = inflater.inflate(R.layout.dialog_image_chooser, null);
        builder.setView(dialoglayout);
        TextView txtCamera = dialoglayout.findViewById(R.id.txtCamera);
        TextView txtGallery;
        txtGallery = dialoglayout.findViewById(R.id.txtGallery);
        alertDialog = builder.create();

        txtCamera.setOnClickListener(v -> {
            alertDialog.dismiss();
            requestPermissions(Manifest.permission.CAMERA, 4, new PermissionListener() {
                @Override public void onGranted(int requestCode) {
                    if (requestCode == 4) {
                        pickFromCamera(imagePickerListener, tag);
                    }
                }

                @Override public void onRejected(int requestCode) {
                    return;
                }
            });
        });

        txtGallery.setOnClickListener(v -> {
            pickFromGallery(imagePickerListener, tag);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    public void pickFromCamera(ImagePickerListener imagePickerListener, String TAG) {
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
                                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "MarsPlay";
                                File appDir = new File(appDirPath);
                                if (!appDir.exists()) {
                                    appDir.mkdirs();
                                }

                                mFileCaptured = new File(appDir, tag + ".jpg");
                                mImageCaptureUri = FileProvider.getUriForFile(FilePickerActivity.this,
                                        getApplicationContext().getPackageName() + ".provider", mFileCaptured);
                            } else {
                                mFileCaptured = new File(Environment.getExternalStorageDirectory(), tag + ".jpg");
                                mImageCaptureUri = Uri.fromFile(mFileCaptured);
                            }
                        }

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, 10000);
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

    public void pickFromGallery(ImagePickerListener imagePickerListener, String tag) {
        this.tag = tag;
        this.imagePickerListener = imagePickerListener;

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 20000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap bm = null;
                FileOutputStream outputStream = null;
                try {
                    bm = getBitmapFromReturnedImage(mImageCaptureUri);

                    //fileProfilePic = new File(context.getFilesDir(), tag+".jpg");
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                        outputStream = new FileOutputStream(mFileCaptured);
                    } else {
                        mFileCaptured = new File(getFilesDir(), tag + ".jpg");
                        outputStream = new FileOutputStream(mFileCaptured);
                    }
                    bm.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                    imagePickerListener.onImagePicked(mFileCaptured, bm, tag);
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
                if (data != null && getContentResolver().getType(data.getData()) != null) {
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String type = mime.getExtensionFromMimeType(getContentResolver().getType(data.getData()));
                    if (type.equalsIgnoreCase("jpg")
                            || type.equalsIgnoreCase("jpeg")
                            || type.equalsIgnoreCase("bmp")
                            || type.equalsIgnoreCase("png")) {
                        FileOutputStream outputStream = null;
                        try {
                            //String mime = context.getContentResolver().getType(data.getData());

                            // bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                            bm = getBitmapFromReturnedImage(data.getData());

                            File fileProfilePic = new File(getFilesDir(), tag + ".jpg");
                            outputStream = new FileOutputStream(fileProfilePic);
                            bm.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                            outputStream.flush();
                            outputStream.close();

                            imagePickerListener.onImagePicked(fileProfilePic, bm, tag);
                        } catch (IOException e) {
                            Toast.makeText(this, "File format not supported", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, "File format not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "File format not supported", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Bitmap getBitmapFromReturnedImage(Uri selectedImage) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(selectedImage);

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);

        // close the input stream
        inputStream.close();

        // reopen the input stream
        inputStream = getContentResolver().openInputStream(selectedImage);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(inputStream, null, options);
    }
}