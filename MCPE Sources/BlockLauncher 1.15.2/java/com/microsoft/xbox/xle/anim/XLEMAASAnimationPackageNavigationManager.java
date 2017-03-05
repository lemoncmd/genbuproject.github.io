package com.microsoft.xbox.xle.anim;

import android.view.View;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.MAASAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class XLEMAASAnimationPackageNavigationManager extends MAASAnimation {
    @Element(required = false)
    public XLEMAASAnimationPackageDirection backward;
    @Element(required = false)
    public XLEMAASAnimationPackageDirection forward;

    public XLEAnimation compile(MAASAnimationType mAASAnimationType, boolean z, View view) {
        XLEMAASAnimationPackageDirection xLEMAASAnimationPackageDirection = z ? this.backward : this.forward;
        return xLEMAASAnimationPackageDirection == null ? null : xLEMAASAnimationPackageDirection.compile(mAASAnimationType, view);
    }
}
