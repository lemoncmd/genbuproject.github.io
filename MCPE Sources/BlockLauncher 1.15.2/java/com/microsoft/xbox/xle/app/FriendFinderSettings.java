package com.microsoft.xbox.xle.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.xbox.service.model.friendfinder.RecommendationTypeIcon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class FriendFinderSettings {
    private static HashMap<String, RecommendationTypeIcon> icons;
    public String ICONS;

    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$xle$app$FriendFinderSettings$IconImageSize = new int[IconImageSize.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$FriendFinderSettings$IconImageSize[IconImageSize.SMALL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$FriendFinderSettings$IconImageSize[IconImageSize.MEDIUM.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$xle$app$FriendFinderSettings$IconImageSize[IconImageSize.LARGE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public enum IconImageSize {
        UNKNOWN,
        SMALL,
        MEDIUM,
        LARGE
    }

    public static String getIconBySize(String str, IconImageSize iconImageSize) {
        if (icons != null && icons.size() > 0) {
            RecommendationTypeIcon recommendationTypeIcon = (RecommendationTypeIcon) icons.get(str.toLowerCase());
            if (recommendationTypeIcon != null) {
                switch (AnonymousClass2.$SwitchMap$com$microsoft$xbox$xle$app$FriendFinderSettings$IconImageSize[iconImageSize.ordinal()]) {
                    case NativeRegExp.MATCH /*1*/:
                        return recommendationTypeIcon.small;
                    case NativeRegExp.PREFIX /*2*/:
                        return recommendationTypeIcon.medium;
                    case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                        return recommendationTypeIcon.large;
                }
            }
        }
        return null;
    }

    public void getIconsFromJson(String str) {
        icons = new HashMap();
        try {
            ArrayList arrayList = (ArrayList) new Gson().fromJson(str, new TypeToken<ArrayList<RecommendationTypeIcon>>() {
            }.getType());
            if (arrayList != null) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    RecommendationTypeIcon recommendationTypeIcon = (RecommendationTypeIcon) it.next();
                    icons.put(recommendationTypeIcon.type.toLowerCase(), recommendationTypeIcon);
                }
            }
        } catch (Exception e) {
        }
    }
}
