package com.microsoft.xbox.xle.anim;

import android.view.View;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.MAASAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class XLEMAASAnimationPackageDirection extends MAASAnimation {
    @Element(required = false)
    public XLEMAASAnimation inAnimation;
    @Element(required = false)
    public XLEMAASAnimation outAnimation;

    public XLEAnimation compile(MAASAnimationType mAASAnimationType, View view) {
        XLEMAASAnimation xLEMAASAnimation = mAASAnimationType == MAASAnimationType.ANIMATE_IN ? this.inAnimation : this.outAnimation;
        return xLEMAASAnimation == null ? null : xLEMAASAnimation.compile(view);
    }
}
