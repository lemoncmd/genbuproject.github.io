package net.hockeyapp.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.views.FeedbackMessageView;

public class MessagesAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<FeedbackMessage> mMessagesList;

    public MessagesAdapter(Context context, ArrayList<FeedbackMessage> arrayList) {
        this.mContext = context;
        this.mMessagesList = arrayList;
    }

    public void add(FeedbackMessage feedbackMessage) {
        if (feedbackMessage != null && this.mMessagesList != null) {
            this.mMessagesList.add(feedbackMessage);
        }
    }

    public void clear() {
        if (this.mMessagesList != null) {
            this.mMessagesList.clear();
        }
    }

    public int getCount() {
        return this.mMessagesList.size();
    }

    public Object getItem(int i) {
        return this.mMessagesList.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        FeedbackMessage feedbackMessage = (FeedbackMessage) this.mMessagesList.get(i);
        if (view == null) {
            view = new FeedbackMessageView(this.mContext, null);
        } else {
            FeedbackMessageView feedbackMessageView = (FeedbackMessageView) view;
        }
        if (feedbackMessage != null) {
            view.setFeedbackMessage(feedbackMessage);
        }
        view.setIndex(i);
        return view;
    }
}
