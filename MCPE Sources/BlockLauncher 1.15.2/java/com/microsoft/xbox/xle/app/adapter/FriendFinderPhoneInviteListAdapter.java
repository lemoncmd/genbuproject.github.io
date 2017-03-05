package com.microsoft.xbox.xle.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo.Contact;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xboxtcui.R;

public class FriendFinderPhoneInviteListAdapter extends ArrayAdapter<Contact> {

    private static class ViewHolder {
        private CustomTypefaceTextView checkTextView;
        private CustomTypefaceTextView contactNameTextView;
        private CustomTypefaceTextView onXboxTextView;

        private ViewHolder() {
        }
    }

    public FriendFinderPhoneInviteListAdapter(Context context, int i) {
        super(context, i);
        XLEAssert.fail("This isn't supported yet.");
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        int i2 = 0;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.friendfinder_phone_invite_list_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.contactNameTextView = (CustomTypefaceTextView) view.findViewById(R.id.friendfinder_phone_invite_name);
            viewHolder.onXboxTextView = (CustomTypefaceTextView) view.findViewById(R.id.friendfinder_phone_invite_name_onxbox);
            viewHolder.checkTextView = (CustomTypefaceTextView) view.findViewById(R.id.friendfinder_phone_invite_checkbox);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Contact contact = (Contact) getItem(i);
        boolean isItemChecked = ((ListView) viewGroup).isItemChecked(i);
        view.setBackgroundResource(isItemChecked ? R.color.XboxOneGreen : 17170445);
        if (contact != null) {
            XLEUtil.updateTextAndVisibilityIfNotNull(viewHolder.contactNameTextView, contact.displayName, 0);
            viewHolder.onXboxTextView.setVisibility(contact.isOnXbox ? 0 : 8);
            CustomTypefaceTextView access$300 = viewHolder.checkTextView;
            if (!isItemChecked) {
                i2 = 4;
            }
            access$300.setVisibility(i2);
        }
        return view;
    }
}
