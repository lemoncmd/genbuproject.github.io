package com.ipaulpro.afilechooser.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.R;
import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public class FileUtils {
    private static final boolean DEBUG = false;
    public static final String EXTRA_MIME_TYPES = "net.zhuoweizhang.afilechooser.extra.MIME_TYPES";
    public static final String EXTRA_SORT_METHOD = "net.zhuoweizhang.afilechooser.extra.SORT_METHOD";
    private static final String HIDDEN_PREFIX = ".";
    public static final String MIME_TYPE_APP = "application/*";
    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String SORT_LAST_MODIFIED = "net.zhuoweizhang.afilechooser.extra.SORT_LAST_MODIFIED";
    static final String TAG = "FileUtils";
    private static Comparator<File> mComparator = new Comparator<File>() {
        public int compare(File f1, File f2) {
            return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
        }
    };
    private static FileFilter mDirFilter = new FileFilter() {
        public boolean accept(File file) {
            return (!file.isDirectory() || file.getName().startsWith(FileUtils.HIDDEN_PREFIX)) ? FileUtils.DEBUG : true;
        }
    };
    private static FileFilter mFileFilter = new FileFilter() {
        public boolean accept(File file) {
            return (!file.isFile() || file.getName().startsWith(FileUtils.HIDDEN_PREFIX)) ? FileUtils.DEBUG : true;
        }
    };

    public static boolean isLocal(String uri) {
        if (uri == null || uri.startsWith("http://")) {
            return DEBUG;
        }
        return true;
    }

    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }
        int dot = uri.lastIndexOf(HIDDEN_PREFIX);
        if (dot >= 0) {
            return uri.substring(dot);
        }
        return BuildConfig.FLAVOR;
    }

    public static boolean isMediaUri(Uri uri) {
        String uriString = uri.toString();
        if (uriString.startsWith(Media.INTERNAL_CONTENT_URI.toString()) || uriString.startsWith(Media.EXTERNAL_CONTENT_URI.toString()) || uriString.startsWith(Video.Media.INTERNAL_CONTENT_URI.toString()) || uriString.startsWith(Video.Media.EXTERNAL_CONTENT_URI.toString())) {
            return true;
        }
        return DEBUG;
    }

    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static File getFile(Uri uri) {
        if (uri != null) {
            String filepath = uri.getPath();
            if (filepath != null) {
                return new File(filepath);
            }
        }
        return null;
    }

    public static File getPathWithoutFilename(File file) {
        if (file == null) {
            return null;
        }
        if (file.isDirectory()) {
            return file;
        }
        String filename = file.getName();
        String filepath = file.getAbsolutePath();
        String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
        if (pathwithoutname.endsWith("/")) {
            pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
        }
        return new File(pathwithoutname);
    }

    public static File getFile(String curdir, String file) {
        String separator = "/";
        if (curdir.endsWith("/")) {
            separator = BuildConfig.FLAVOR;
        }
        return new File(curdir + separator + file);
    }

    public static File getFile(File curdir, String file) {
        return getFile(curdir.getAbsolutePath(), file);
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            try {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getReadableFileSize(int size) {
        DecimalFormat dec = new DecimalFormat("###.#");
        String KILOBYTES = " KB";
        String MEGABYTES = " MB";
        String GIGABYTES = " GB";
        float fileSize = 0.0f;
        String suffix = " KB";
        if (size > EnchantType.pickaxe) {
            fileSize = (float) (size / EnchantType.pickaxe);
            if (fileSize > 1024.0f) {
                fileSize /= 1024.0f;
                if (fileSize > 1024.0f) {
                    fileSize /= 1024.0f;
                    suffix = " GB";
                } else {
                    suffix = " MB";
                }
            }
        }
        return String.valueOf(dec.format((double) fileSize) + suffix);
    }

    private static MimeTypes getMimeTypes(Context context) {
        MimeTypes mimeTypes = null;
        try {
            mimeTypes = new MimeTypeParser().fromXmlResource(context.getResources().getXml(R.xml.mimetypes));
        } catch (Exception e) {
        }
        return mimeTypes;
    }

    public static String getMimeType(Context context, File file) {
        MimeTypes mimeTypes = getMimeTypes(context);
        if (file != null) {
            return mimeTypes.getMimeType(file.getName());
        }
        return null;
    }

    public static Bitmap getThumbnail(Context context, File file) {
        return getThumbnail(context, getUri(file), getMimeType(context, file));
    }

    public static Bitmap getThumbnail(Context context, Uri uri) {
        return getThumbnail(context, uri, getMimeType(context, getFile(uri)));
    }

    public static Bitmap getThumbnail(Context context, Uri uri, String mimeType) {
        Bitmap bitmap = null;
        if (isMediaUri(uri)) {
            Log.e(TAG, "You can only retrieve thumbnails for images and videos.");
        } else {
            bitmap = null;
            if (uri != null) {
                ContentResolver resolver = context.getContentResolver();
                Cursor cursor = null;
                try {
                    cursor = resolver.query(uri, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        int id = cursor.getInt(0);
                        if (mimeType.contains("video")) {
                            bitmap = Thumbnails.getThumbnail(resolver, (long) id, 1, null);
                        } else if (mimeType.contains(MIME_TYPE_IMAGE)) {
                            bitmap = Images.Thumbnails.getThumbnail(resolver, (long) id, 1, null);
                        }
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception e) {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Throwable th) {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
        return bitmap;
    }

    public static List<File> getFileList(String path) {
        ArrayList<File> list = new ArrayList();
        File pathDir = new File(path);
        File[] dirs = pathDir.listFiles(mDirFilter);
        if (dirs != null) {
            Arrays.sort(dirs, mComparator);
            for (File dir : dirs) {
                list.add(dir);
            }
        }
        File[] files = pathDir.listFiles(mFileFilter);
        if (files != null) {
            Arrays.sort(files, mComparator);
            for (File file : files) {
                list.add(file);
            }
        }
        return list;
    }

    public static Intent createGetContentIntent() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType(FileChooserActivity.MIME_TYPE_ALL);
        intent.addCategory("android.intent.category.OPENABLE");
        return intent;
    }
}
