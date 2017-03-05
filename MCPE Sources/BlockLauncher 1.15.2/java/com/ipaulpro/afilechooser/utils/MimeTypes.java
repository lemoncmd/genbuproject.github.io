package com.ipaulpro.afilechooser.utils;

import android.net.Uri;
import android.webkit.MimeTypeMap;
import com.ipaulpro.afilechooser.FileChooserActivity;
import java.util.HashMap;
import java.util.Map;

public class MimeTypes {
    private Map<String, String> mMimeTypes = new HashMap();

    public void put(String type, String extension) {
        this.mMimeTypes.put(type, extension.toLowerCase());
    }

    public String getMimeType(String filename) {
        String extension = FileUtils.getExtension(filename);
        if (extension.length() > 0) {
            String webkitMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
            if (webkitMimeType != null) {
                return webkitMimeType;
            }
        }
        String mimetype = (String) this.mMimeTypes.get(extension.toLowerCase());
        if (mimetype == null) {
            mimetype = FileChooserActivity.MIME_TYPE_ALL;
        }
        return mimetype;
    }

    public String getMimeType(Uri uri) {
        return getMimeType(FileUtils.getFile(uri).getName());
    }
}
