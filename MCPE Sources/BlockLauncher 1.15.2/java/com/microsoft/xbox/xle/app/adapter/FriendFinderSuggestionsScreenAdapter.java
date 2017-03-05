package com.microsoft.xbox.xle.app.adapter;

import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLEListView;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderSuggestionsScreenViewModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;

public class FriendFinderSuggestionsScreenAdapter extends AdapterBase {
    private XLEButton advanceButton = ((XLEButton) findViewById(R.id.friendfinder_suggestions_button));
    private ViewGroup emptyListHeaderContainer = ((ViewGroup) findViewById(R.id.friendfinder_suggestions_empty_header_container));
    private FrameLayout listHeaderContainer;
    private ViewGroup listHeaderGroup = ((ViewGroup) findViewById(R.id.friendfinder_suggestions_header));
    private FrameLayout loadingOverlay = ((FrameLayout) findViewById(R.id.friendfinder_suggestions_loading));
    private CustomTypefaceTextView subtitleTextView = ((CustomTypefaceTextView) findViewById(R.id.friendfinder_suggestions_subtitle));
    private FriendFinderSuggestionsListAdapter suggestionsListAdapter;
    private XLEListView suggestionsListView = ((XLEListView) findViewById(R.id.friendfinder_suggestions_list));
    private CustomTypefaceTextView titleTextView = ((CustomTypefaceTextView) findViewById(R.id.friendfinder_suggestions_title));
    private FriendFinderSuggestionsScreenViewModel viewModel;

    public FriendFinderSuggestionsScreenAdapter(FriendFinderSuggestionsScreenViewModel friendFinderSuggestionsScreenViewModel) {
        super(friendFinderSuggestionsScreenViewModel);
        this.viewModel = friendFinderSuggestionsScreenViewModel;
        XLEAssert.assertNotNull(this.titleTextView);
        XLEAssert.assertNotNull(this.subtitleTextView);
        XLEAssert.assertNotNull(this.suggestionsListView);
        XLEAssert.assertNotNull(this.emptyListHeaderContainer);
        XLEAssert.assertNotNull(this.listHeaderGroup);
        XLEAssert.assertNotNull(this.advanceButton);
        XLEAssert.assertNotNull(this.loadingOverlay);
        this.listHeaderContainer = new FrameLayout(XboxTcuiSdk.getActivity());
        this.suggestionsListView.addHeaderView(this.listHeaderContainer, null, false);
        this.suggestionsListView.setChoiceMode(2);
    }

    public void onStart() {
        super.onStart();
        this.suggestionsListAdapter = new FriendFinderSuggestionsListAdapter(XboxTcuiSdk.getActivity(), R.layout.friendfinder_suggestions_list_item, true);
        this.suggestionsListView.setAdapter(this.suggestionsListAdapter);
        this.suggestionsListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                adapterView.getAdapter().getView(i, view, adapterView);
                int checkedItemCount = FriendFinderSuggestionsScreenAdapter.this.suggestionsListView.getCheckedItemCount();
                if (checkedItemCount == 0) {
                    FriendFinderSuggestionsScreenAdapter.this.advanceButton.setText(R.string.FriendFinder_Phone_Next_ButtonText);
                } else if (checkedItemCount == 1) {
                    FriendFinderSuggestionsScreenAdapter.this.advanceButton.setText(R.string.Profile_Profile_AddFriend);
                } else {
                    FriendFinderSuggestionsScreenAdapter.this.advanceButton.setText(R.string.FriendFinder_AddFriends);
                }
            }
        });
        this.advanceButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SparseBooleanArray checkedItemPositions = FriendFinderSuggestionsScreenAdapter.this.suggestionsListView.getCheckedItemPositions();
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < FriendFinderSuggestionsScreenAdapter.this.suggestionsListAdapter.getCount() + 1; i++) {
                    if (checkedItemPositions.get(i)) {
                        arrayList.add(Integer.valueOf(i - 1));
                    }
                }
                if (arrayList.size() > 0) {
                    FriendFinderSuggestionsScreenAdapter.this.viewModel.addSuggestions(arrayList);
                } else {
                    FriendFinderSuggestionsScreenAdapter.this.viewModel.navigateToSkip();
                }
            }
        });
    }

    protected void updateViewOverride() {
        this.loadingOverlay.setVisibility(this.viewModel.isBusy() ? 0 : 8);
        this.titleTextView.setText(this.viewModel.getTitle());
        this.subtitleTextView.setText(this.viewModel.getSubtitle());
        this.suggestionsListAdapter.clear();
        this.suggestionsListAdapter.addAll(this.viewModel.getSuggestions());
        this.suggestionsListAdapter.notifyDataSetChanged();
        this.emptyListHeaderContainer.removeAllViews();
        this.listHeaderContainer.removeAllViews();
        if (this.suggestionsListAdapter.getCount() > 0) {
            this.listHeaderContainer.addView(this.listHeaderGroup);
        } else {
            this.emptyListHeaderContainer.addView(this.listHeaderGroup);
        }
    }
}
