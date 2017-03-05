package net.zhuoweizhang.mcpelauncher.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import net.zhuoweizhang.mcpelauncher.R;

public class HoverCar extends PopupWindow {
    public ImageButton mainButton = ((ImageButton) getContentView().findViewById(R.id.hovercar_main_button));
    private Context theContext;

    public HoverCar(Context con, boolean safeMode) {
        Drawable colorDrawable;
        super(con);
        this.theContext = con;
        setContentView(((LayoutInflater) con.getSystemService("layout_inflater")).inflate(R.layout.hovercar, null));
        float myWidth = con.getResources().getDimension(R.dimen.hovercar_width);
        float myHeight = con.getResources().getDimension(R.dimen.hovercar_height);
        setWidth((int) myWidth);
        setHeight((int) myHeight);
        if (safeMode) {
            colorDrawable = new ColorDrawable(-2130771968);
        } else {
            colorDrawable = null;
        }
        setBackgroundDrawable(colorDrawable);
    }

    public void show(View parentView) {
        showAtLocation(parentView, 49, (int) (((double) this.theContext.getResources().getDimension(R.dimen.hovercar_width)) * 1.5d), 0);
    }

    public void setVisible(boolean visible) {
        this.mainButton.setAlpha(visible ? 255 : 0);
        update();
    }
}
