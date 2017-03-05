package com.microsoft.xbox.service.model.friendfinder;

import com.microsoft.xbox.service.network.managers.IPeopleHubResult.PeopleHubPersonSummary;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.RecommendationType;
import java.net.URI;

public class FriendFinderSuggestionModel {
    public String gamerTag;
    public URI imageUri;
    public String presence;
    public String realName;
    public RecommendationType recommendationType;

    public static FriendFinderSuggestionModel fromPeopleHubSummary(PeopleHubPersonSummary peopleHubPersonSummary) {
        FriendFinderSuggestionModel friendFinderSuggestionModel = new FriendFinderSuggestionModel();
        friendFinderSuggestionModel.imageUri = URI.create(peopleHubPersonSummary.displayPicRaw);
        friendFinderSuggestionModel.gamerTag = peopleHubPersonSummary.gamertag;
        friendFinderSuggestionModel.realName = peopleHubPersonSummary.realName;
        friendFinderSuggestionModel.recommendationType = peopleHubPersonSummary.recommendation != null ? peopleHubPersonSummary.recommendation.getRecommendationType() : RecommendationType.Unknown;
        friendFinderSuggestionModel.presence = peopleHubPersonSummary.presenceText;
        return friendFinderSuggestionModel;
    }
}
