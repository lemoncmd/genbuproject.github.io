package com.microsoft.xbox.service.model;

import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.Search.TrieSearch;
import java.util.ArrayList;
import java.util.List;
import net.hockeyapp.android.BuildConfig;

public class SearchResultPerson {
    public String GamertagAfter;
    public String GamertagBefore;
    public String GamertagMatch;
    public String RealNameAfter;
    public String RealNameBefore;
    public String RealNameMatch;
    public String SearchText;
    public String StatusAfter;
    public String StatusBefore;
    public String StatusMatch;

    public SearchResultPerson(FollowersData followersData, String str) {
        if (isNullOrWhitespace(str)) {
            throw new IllegalArgumentException(str);
        }
        this.SearchText = str;
        setInlineRuns(followersData);
    }

    private static List<String> getRuns(String str, String str2) {
        List<String> arrayList = new ArrayList(3);
        int findWordIndex = TrieSearch.findWordIndex(str, str2);
        int length = str2.length() + findWordIndex;
        if (findWordIndex != -1) {
            arrayList.add(str.substring(0, findWordIndex));
            arrayList.add(str.substring(findWordIndex, str2.length() + findWordIndex));
            arrayList.add(str.substring(length, str.length()));
        } else {
            arrayList.add(str);
            arrayList.add(BuildConfig.FLAVOR);
            arrayList.add(BuildConfig.FLAVOR);
        }
        return arrayList;
    }

    private static boolean isNullOrWhitespace(String str) {
        return JavaUtil.isNullOrEmpty(str) || str.trim().isEmpty();
    }

    private void setInlineRuns(FollowersData followersData) {
        List runs = getRuns(followersData.getGamertag(), this.SearchText);
        if (runs.size() == 3) {
            this.GamertagBefore = (String) runs.get(0);
            this.GamertagMatch = (String) runs.get(1);
            this.GamertagAfter = (String) runs.get(2);
        }
        runs = getRuns(followersData.getGamerRealName(), this.SearchText);
        if (runs.size() == 3) {
            this.RealNameBefore = (String) runs.get(0);
            this.RealNameMatch = (String) runs.get(1);
            this.RealNameAfter = (String) runs.get(2);
        }
        runs = getRuns(followersData.presenceString, this.SearchText);
        if (runs.size() == 3) {
            this.StatusBefore = (String) runs.get(0);
            this.StatusMatch = (String) runs.get(1);
            this.StatusAfter = (String) runs.get(2);
        }
    }
}
