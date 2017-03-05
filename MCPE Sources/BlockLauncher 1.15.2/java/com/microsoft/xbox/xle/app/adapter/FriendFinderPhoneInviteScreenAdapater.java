package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderPhoneInviteScreenViewModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;

public class FriendFinderPhoneInviteScreenAdapater extends AdapterBase {
    private FriendFinderPhoneInviteListAdapter contactsListAdapter;
    private ListView contactsListView = ((ListView) findViewById(R.id.friendfinder_suggestions_list));
    private FrameLayout loadingLayout = ((FrameLayout) findViewById(R.id.friendfinder_suggestions_loading));
    private CustomTypefaceTextView subtitleTextView = ((CustomTypefaceTextView) findViewById(R.id.friendfinder_suggestions_subtitle));
    private CustomTypefaceTextView titleTextView = ((CustomTypefaceTextView) findViewById(R.id.friendfinder_suggestions_title));
    private FriendFinderPhoneInviteScreenViewModel viewModel;

    public FriendFinderPhoneInviteScreenAdapater(FriendFinderPhoneInviteScreenViewModel friendFinderPhoneInviteScreenViewModel) {
        super(friendFinderPhoneInviteScreenViewModel);
        XLEAssert.fail("This isn't supported yet.");
        this.viewModel = friendFinderPhoneInviteScreenViewModel;
        XLEAssert.assertNotNull(this.titleTextView);
        XLEAssert.assertNotNull(this.subtitleTextView);
        XLEAssert.assertNotNull(this.contactsListView);
        XLEAssert.assertNotNull(this.loadingLayout);
        this.titleTextView.setText(R.string.FriendFinder_PhoneInviteFriends_Dialog_Title);
        this.subtitleTextView.setText(XboxTcuiSdk.getResources().getString(R.string.FriendFinder_PhoneInviteFriends_Dialog_Text).replace("-", "\n\n"));
        this.contactsListView.setChoiceMode(2);
    }

    public void onStart() {
        super.onStart();
        this.contactsListAdapter = new FriendFinderPhoneInviteListAdapter(XboxTcuiSdk.getActivity(), R.layout.friendfinder_phone_invite_list_item);
        this.contactsListView.setAdapter(this.contactsListAdapter);
        this.contactsListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                adapterView.getAdapter().getView(i, view, adapterView);
            }
        });
    }

    protected void updateViewOverride() {
        this.loadingLayout.setVisibility(this.viewModel.isBusy() ? 0 : 8);
        this.contactsListAdapter.clear();
        this.contactsListAdapter.addAll(this.viewModel.getContacts());
        this.contactsListAdapter.notifyDataSetChanged();
    }
}
