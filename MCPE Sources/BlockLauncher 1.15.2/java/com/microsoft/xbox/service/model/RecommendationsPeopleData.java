package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.FollowersData.DummyType;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.PeopleHubPersonSummary;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.PeopleHubRecommendation;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.RecommendationType;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.app.XLEUtil;
import net.hockeyapp.android.BuildConfig;

public class RecommendationsPeopleData extends FollowersData {
    private PeopleHubRecommendation recommendationInfo;

    public RecommendationsPeopleData(PeopleHubPersonSummary peopleHubPersonSummary) {
        super(peopleHubPersonSummary);
        XLEAssert.assertNotNull(peopleHubPersonSummary.recommendation);
        this.recommendationInfo = peopleHubPersonSummary.recommendation;
    }

    public RecommendationsPeopleData(boolean z, DummyType dummyType) {
        super(z, dummyType);
    }

    public boolean getIsFacebookFriend() {
        return this.recommendationInfo.getRecommendationType() == RecommendationType.FacebookFriend;
    }

    public String getRecommendationFirstReason() {
        return XLEUtil.isNullOrEmpty(this.recommendationInfo.Reasons) ? BuildConfig.FLAVOR : (String) this.recommendationInfo.Reasons.get(0);
    }

    public RecommendationType getRecommendationType() {
        return this.recommendationInfo.getRecommendationType();
    }
}
