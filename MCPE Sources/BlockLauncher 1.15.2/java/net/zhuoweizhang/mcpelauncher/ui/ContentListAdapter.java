package net.zhuoweizhang.mcpelauncher.ui;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class ContentListAdapter extends ArrayAdapter<ContentListItem> {
    private Resources mResources;

    public ContentListAdapter(Context context, int layout, List<ContentListItem> items) {
        super(context, layout, items);
        this.mResources = context.getResources();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        ContentListItem item = (ContentListItem) getItem(position);
        view.setText(item.toString(this.mResources));
        view.setTypeface(null, item.enabled ? 1 : 0);
        return view;
    }
}
