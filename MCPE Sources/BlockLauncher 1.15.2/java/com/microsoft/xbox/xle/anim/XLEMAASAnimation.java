package com.microsoft.xbox.xle.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.anim.MAASAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationAbsListView;
import com.microsoft.xbox.toolkit.anim.XLEAnimationView;
import java.util.ArrayList;
import java.util.Iterator;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class XLEMAASAnimation extends MAASAnimation {
    @ElementList(required = false)
    public ArrayList<XLEAnimationDefinition> animations;
    @Attribute(required = false)
    public boolean fillAfter = true;
    @Attribute(required = false)
    public int offsetMs;
    @Attribute(required = false)
    public TargetType target = TargetType.View;
    @Attribute(required = false)
    public String targetId = null;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$xle$anim$XLEMAASAnimation$TargetType = new int[TargetType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$xle$anim$XLEMAASAnimation$TargetType[TargetType.View.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$anim$XLEMAASAnimation$TargetType[TargetType.ListView.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$anim$XLEMAASAnimation$TargetType[TargetType.GridView.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public enum TargetType {
        View,
        ListView,
        GridView
    }

    public XLEAnimation compile() {
        return compile(XLERValueHelper.findViewByString(this.targetId));
    }

    public XLEAnimation compile(View view) {
        XLEAnimation xLEAnimationView;
        Animation animation = null;
        if (this.animations != null && this.animations.size() > 0) {
            Animation animationSet = new AnimationSet(false);
            Iterator it = this.animations.iterator();
            while (it.hasNext()) {
                animation = ((XLEAnimationDefinition) it.next()).getAnimation();
                if (animation != null) {
                    animationSet.addAnimation(animation);
                }
            }
            animation = animationSet;
        }
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$xle$anim$XLEMAASAnimation$TargetType[this.target.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                XLEAssert.assertNotNull(animation);
                xLEAnimationView = new XLEAnimationView(animation);
                ((XLEAnimationView) xLEAnimationView).setFillAfter(this.fillAfter);
                break;
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                XLEAssert.assertNotNull(animation);
                xLEAnimationView = new XLEAnimationAbsListView(new LayoutAnimationController(animation, ((float) this.offsetMs) / 1000.0f));
                break;
            default:
                throw new UnsupportedOperationException();
        }
        xLEAnimationView.setTargetView(view);
        return xLEAnimationView;
    }

    public XLEAnimation compileWithRoot(View view) {
        return compile(view.findViewById(XLERValueHelper.getIdRValue(this.targetId)));
    }
}
