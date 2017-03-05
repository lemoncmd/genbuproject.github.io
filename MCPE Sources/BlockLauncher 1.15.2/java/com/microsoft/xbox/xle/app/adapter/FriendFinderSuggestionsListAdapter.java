package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderSuggestionModel;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.RecommendationType;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLERoundedUniversalImageView;
import com.microsoft.xbox.toolkit.ui.XLEUniversalImageView;
import com.microsoft.xbox.xle.app.FriendFinderSettings;
import com.microsoft.xbox.xle.app.FriendFinderSettings.IconImageSize;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xboxtcui.R;
import java.net.URI;

public class FriendFinderSuggestionsListAdapter extends ArrayAdapter<FriendFinderSuggestionModel> {
    private boolean containsHeader;
    private URI facebookImageUri;

    private static class ViewHolder {
        private CustomTypefaceTextView checkTextView;
        private XLERoundedUniversalImageView gamerpicImageView;
        private CustomTypefaceTextView gamertagTextView;
        private XLEUniversalImageView iconImageView;
        private CustomTypefaceTextView iconTextView;
        private CustomTypefaceTextView presenceTextView;
        private CustomTypefaceTextView realNameTextView;

        private ViewHolder() {
        }
    }

    public FriendFinderSuggestionsListAdapter(Context context, int i, boolean z) {
        super(context, i);
        this.containsHeader = z;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        boolean z = true;
        int i2 = 4;
        int i3 = 17170445;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.friendfinder_suggestions_list_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.gamerpicImageView = (XLERoundedUniversalImageView) view.findViewById(R.id.friendfinder_suggestions_item_image);
            viewHolder.iconImageView = (XLEUniversalImageView) view.findViewById(R.id.friendfinder_suggestions_item_icon_image);
            viewHolder.gamertagTextView = (CustomTypefaceTextView) view.findViewById(R.id.friendfinder_suggestions_item_gamertag);
            viewHolder.realNameTextView = (CustomTypefaceTextView) view.findViewById(R.id.friendfinder_suggestions_item_realname);
            viewHolder.iconTextView = (CustomTypefaceTextView) view.findViewById(R.id.friendfinder_suggestions_item_icon_text);
            viewHolder.presenceTextView = (CustomTypefaceTextView) view.findViewById(R.id.friendfinder_suggestions_item_presence);
            viewHolder.checkTextView = (CustomTypefaceTextView) view.findViewById(R.id.friendfinder_suggestions_item_check);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        FriendFinderSuggestionModel friendFinderSuggestionModel = (FriendFinderSuggestionModel) getItem(i);
        boolean isItemChecked = ((ListView) viewGroup).isItemChecked((this.containsHeader ? 1 : 0) + i);
        view.setBackgroundResource(isItemChecked ? R.color.white_15_percent : 17170445);
        CustomTypefaceTextView access$500 = viewHolder.iconTextView;
        if (!isItemChecked) {
            i3 = R.color.white_15_percent;
        }
        access$500.setBackgroundResource(i3);
        if (friendFinderSuggestionModel != null) {
            viewHolder.gamerpicImageView.setImageURI2(friendFinderSuggestionModel.imageUri, R.drawable.gamerpic_missing, R.drawable.gamerpic_missing);
            XLEUtil.updateTextAndVisibilityIfNotNull(viewHolder.gamertagTextView, friendFinderSuggestionModel.gamerTag, 0);
            XLEUtil.updateTextAndVisibilityIfNotNull(viewHolder.realNameTextView, friendFinderSuggestionModel.realName, 0);
            XLEUtil.updateTextAndVisibilityIfNotNull(viewHolder.presenceTextView, friendFinderSuggestionModel.presence, 0);
            if (friendFinderSuggestionModel.recommendationType != RecommendationType.FacebookFriend) {
                z = false;
            }
            viewHolder.iconImageView.setVisibility(z ? 0 : 4);
            viewHolder.iconTextView.setVisibility(z ? 4 : 0);
            if (z) {
                if (this.facebookImageUri == null) {
                    String iconBySize = FriendFinderSettings.getIconBySize(RecommendationType.FacebookFriend.name(), IconImageSize.MEDIUM);
                    if (!JavaUtil.isNullOrEmpty(iconBySize)) {
                        this.facebookImageUri = URI.create(iconBySize);
                    }
                }
                viewHolder.iconImageView.setImageURI2(this.facebookImageUri);
            }
            CustomTypefaceTextView access$700 = viewHolder.checkTextView;
            if (isItemChecked) {
                i2 = 0;
            }
            access$700.setVisibility(i2);
        }
        return view;
    }
}
