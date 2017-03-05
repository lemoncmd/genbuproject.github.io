package com.ipaulpro.afilechooser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

public class FileListAdapter extends BaseAdapter {
    private static final int ICON_FILE = R.drawable.ic_file;
    private static final int ICON_FOLDER = R.drawable.ic_folder;
    private ArrayList<File> mFiles = new ArrayList();
    private LayoutInflater mInflater;

    static class ViewHolder {
        ImageView iconView;
        TextView nameView;

        ViewHolder(View row) {
            this.nameView = (TextView) row.findViewById(R.id.file_name);
            this.iconView = (ImageView) row.findViewById(R.id.file_icon);
        }
    }

    public FileListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void setListItems(ArrayList<File> files) {
        this.mFiles = files;
    }

    public int getCount() {
        return this.mFiles.size();
    }

    public void add(File file) {
        this.mFiles.add(file);
    }

    public void clear() {
        this.mFiles.clear();
    }

    public Object getItem(int position) {
        return this.mFiles.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;
        if (row == null) {
            row = this.mInflater.inflate(R.layout.file, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        File file = (File) getItem(position);
        holder.nameView.setText(file.getName());
        holder.iconView.setImageResource(file.isDirectory() ? ICON_FOLDER : ICON_FILE);
        return row;
    }
}
