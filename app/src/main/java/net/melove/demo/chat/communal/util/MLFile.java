package net.melove.demo.chat.communal.util;


import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lzan13 on 2014/12/16.
 * this is my file utils
 */
public class MLFile {

    /**
     * 判断目录是否存在
     *
     * @param path
     * @return
     */
    public static boolean isDirExists(String path) {
        File dir = new File(path);
        return dir.exists();
    }

    /**
     * 创建目录，多层目录会递归创建
     *
     * @param path
     */
    public static boolean createDirectory(String path) {
        File dir = new File(path);
        if (!isDirExists(path)) {
            return dir.mkdirs();
        }
        return true;
    }

    /**
     * 创建新文件
     *
     * @param filepath
     * @return
     * @throws IOException
     */
    public static boolean createFile(String filepath) {
        boolean isSuccess = false;
        File file = new File(filepath);
        // 判断文件上层目录是否存在，不存在则首先创建目录
        if (!isDirExists(file.getParent())) {
            createDirectory(file.getParent());
        }
        if (!file.isFile()) {
            try {
                isSuccess = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    /**
     * 复制文件
     *
     * @param filepath1
     * @param filepath2
     * @return
     */
    public static boolean copyFile(String filepath1, String filepath2) {
        File file1 = new File(filepath1);
        if (!file1.exists()) {
            MLLog.e("源文件不存在，无法完成复制");
            return false;
        }
        File file2 = new File(filepath2);
        MLLog.i(file2.getParent());
        if (!isDirExists(file2.getParent())) {
            createDirectory(file2.getParent());
        }
        try {
            InputStream inputStream = new FileInputStream(file1);
            FileOutputStream outputStream = new FileOutputStream(filepath2);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
            }
            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            MLLog.e("拷贝文件出错：" + e);
            return false;
        }
        return true;
    }

    /**
     * 读取文件到 Bitmap
     *
     * @param filepath
     * @return
     */
    public static Bitmap fileToBitmap(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
            return bitmap;
        }
        return null;
    }

    public static Drawable fileToDrawable(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            Drawable drawable = Drawable.createFromPath(filepath);
            return drawable;
        }
        return null;
    }

    /**
     * 保存Bitmap到SD卡
     *
     * @param bitmap
     * @param path
     */
    public static void saveBitmapToSDCard(Bitmap bitmap, String path) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(path);
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据传入的路径，获取图片的宽高
     *
     * @param filepath
     * @return
     */
    public static String getImageSize(String filepath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设为true了
        // 这个参数的意义是仅仅解析边缘区域，从而可以得到图片的一些信息，比如大小，而不会整个解析图片，防止OOM
        options.inJustDecodeBounds = true;

        // 此时bitmap还是为空的
        Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);

        int actualWidth = options.outWidth;
        int actualHeight = options.outHeight;
        return actualWidth + "." + actualHeight;
    }


    /**
     * 删除文件
     *
     * @param filepath
     * @return
     */
    public static boolean deleteFile(String filepath) {
        MLLog.i("删除文件：" + filepath);
        File file = new File(filepath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 判断sdcard是否被挂载
     */
    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据 Uri 获取文件的真实路径
     *
     * @param context 上下文对象
     * @param uri     包含文件信息的 Uri
     * @return 返回文件真实路径
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        // 判断当前系统 API 4.4（19） 以上
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // File
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}

