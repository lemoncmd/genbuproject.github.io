package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.UTCDateConverterGson.UTCDateConverterJSONDeserializer;
import com.microsoft.xbox.toolkit.GsonUtil;
import com.microsoft.xbox.toolkit.XLEConstants;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

public interface IFollowerPresenceResult {

    public static class ActivityRecord {
        public BroadcastRecord broadcast;
        public String richPresence;
    }

    public static class BroadcastRecord {
        public String id;
        public String provider;
        public String session;
        public int viewers;
    }

    public static class DeviceRecord {
        public ArrayList<TitleRecord> titles;
        public String type;

        public boolean isXbox360() {
            return "Xbox360".equalsIgnoreCase(this.type);
        }

        public boolean isXboxOne() {
            return "XboxOne".equalsIgnoreCase(this.type);
        }
    }

    public static class FollowersPresenceResult {
        public ArrayList<UserPresence> userPresence;

        public static FollowersPresenceResult deserialize(InputStream inputStream) {
            UserPresence[] userPresenceArr = (UserPresence[]) GsonUtil.deserializeJson(inputStream, UserPresence[].class, (Type) Date.class, new UTCDateConverterJSONDeserializer());
            if (userPresenceArr == null) {
                return null;
            }
            FollowersPresenceResult followersPresenceResult = new FollowersPresenceResult();
            followersPresenceResult.userPresence = new ArrayList(Arrays.asList(userPresenceArr));
            return followersPresenceResult;
        }
    }

    public static class LastSeenRecord {
        public String deviceType;
        public String titleName;
    }

    public static class TitleRecord {
        public ActivityRecord activity;
        public long id;
        public Date lastModified;
        public String name;
        public String placement;

        public boolean isDash() {
            return this.id == XLEConstants.DASH_TITLE_ID;
        }

        public boolean isRunningInFullOrFill() {
            return "Full".equalsIgnoreCase(this.placement) || "Fill".equalsIgnoreCase(this.placement);
        }
    }

    public static class UserPresence {
        private BroadcastRecord broadcastRecord;
        private boolean broadcastRecordSet;
        public ArrayList<DeviceRecord> devices;
        public LastSeenRecord lastSeen;
        public String state;
        public String xuid;

        public BroadcastRecord getBroadcastRecord(long j) {
            if (!this.broadcastRecordSet) {
                if ("Online".equalsIgnoreCase(this.state)) {
                    Iterator it = this.devices.iterator();
                    loop0:
                    while (it.hasNext()) {
                        DeviceRecord deviceRecord = (DeviceRecord) it.next();
                        if (deviceRecord.isXboxOne()) {
                            Iterator it2 = deviceRecord.titles.iterator();
                            while (it2.hasNext()) {
                                TitleRecord titleRecord = (TitleRecord) it2.next();
                                if (titleRecord.id == j && titleRecord.isRunningInFullOrFill() && titleRecord.activity != null && titleRecord.activity.broadcast != null) {
                                    this.broadcastRecord = titleRecord.activity.broadcast;
                                    break loop0;
                                }
                            }
                            continue;
                        }
                    }
                }
                this.broadcastRecordSet = true;
            }
            return this.broadcastRecord;
        }

        public int getBroadcastingViewerCount(long j) {
            BroadcastRecord broadcastRecord = getBroadcastRecord(j);
            return broadcastRecord == null ? 0 : broadcastRecord.viewers;
        }

        public Date getXboxOneNowPlayingDate() {
            if (!"Online".equalsIgnoreCase(this.state)) {
                return null;
            }
            Iterator it = this.devices.iterator();
            Date date = null;
            while (it.hasNext()) {
                DeviceRecord deviceRecord = (DeviceRecord) it.next();
                if (deviceRecord.isXboxOne()) {
                    Iterator it2 = deviceRecord.titles.iterator();
                    while (it2.hasNext()) {
                        TitleRecord titleRecord = (TitleRecord) it2.next();
                        if (titleRecord.isRunningInFullOrFill()) {
                            date = titleRecord.lastModified;
                            break;
                        }
                    }
                }
            }
            return date;
        }

        public long getXboxOneNowPlayingTitleId() {
            if (!"Online".equalsIgnoreCase(this.state)) {
                return -1;
            }
            Iterator it = this.devices.iterator();
            long j = -1;
            while (it.hasNext()) {
                DeviceRecord deviceRecord = (DeviceRecord) it.next();
                if (deviceRecord.isXboxOne()) {
                    Iterator it2 = deviceRecord.titles.iterator();
                    while (it2.hasNext()) {
                        TitleRecord titleRecord = (TitleRecord) it2.next();
                        if (titleRecord.isRunningInFullOrFill()) {
                            j = titleRecord.id;
                            break;
                        }
                    }
                }
            }
            return j;
        }
    }
}
