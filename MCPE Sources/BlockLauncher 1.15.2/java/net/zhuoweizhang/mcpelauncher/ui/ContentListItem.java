package net.zhuoweizhang.mcpelauncher.ui;

import android.content.res.Resources;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.R;

public class ContentListItem {
    public final String displayName;
    public boolean enabled = true;
    public String extraData = null;
    public final File file;

    public static final class ContentListComparator implements Comparator<ContentListItem> {
        public int compare(ContentListItem a, ContentListItem b) {
            return a.displayName.toLowerCase().compareTo(b.displayName.toLowerCase());
        }

        public boolean equals(ContentListItem a, ContentListItem b) {
            return a.displayName.toLowerCase().equals(b.displayName.toLowerCase());
        }
    }

    public ContentListItem(File file, boolean enabled) {
        this.file = file;
        this.displayName = file.getName();
        this.enabled = enabled;
    }

    public String toString() {
        return this.displayName + (this.extraData != null ? " " + this.extraData + " " : BuildConfig.FLAVOR) + (this.enabled ? BuildConfig.FLAVOR : " ".concat("(disabled)"));
    }

    public String toString(Resources res) {
        return this.displayName + (this.extraData != null ? " " + this.extraData + " " : BuildConfig.FLAVOR) + (this.enabled ? BuildConfig.FLAVOR : " ".concat(res.getString(R.string.manage_patches_disabled)));
    }

    public static void sort(List<ContentListItem> list) {
        Collections.sort(list, new ContentListComparator());
    }
}
