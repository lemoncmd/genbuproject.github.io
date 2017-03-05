package com.microsoft.xbox.xle.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.telemetry.helpers.UTCEventTracker.UTCEventDelegate;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.ChangeRelationship;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView;
import net.hockeyapp.android.BuildConfig;

public class UTCChangeRelationship {
    private static CharSequence currentActivityTitle = BuildConfig.FLAVOR;
    private static String currentXUID = BuildConfig.FLAVOR;

    public enum FavoriteStatus {
        UNKNOWN(0),
        FAVORITED(1),
        UNFAVORITED(2),
        NOTFAVORITED(3),
        EXISTINGFAVORITE(4),
        EXISTINGNOTFAVORITED(5);
        
        private int value;

        private FavoriteStatus(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum GamerType {
        UNKNOWN(0),
        NORMAL(1),
        FACEBOOK(2),
        SUGGESTED(3);
        
        private int value;

        private GamerType(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum RealNameStatus {
        UNKNOWN(0),
        SHARINGON(1),
        SHARINGOFF(2),
        EXISTINGSHARED(3),
        EXISTINGNOTSHARED(4);
        
        private int value;

        private RealNameStatus(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum Relationship {
        UNKNOWN(0),
        ADDFRIEND(1),
        REMOVEFRIEND(2),
        EXISTINGFRIEND(3),
        NOTCHANGED(4);
        
        private int value;

        private Relationship(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    private static UTCAdditionalInfoModel getBaseInfoModel(String str) {
        UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
        uTCAdditionalInfoModel.addValue(UTCDeepLink.TARGET_XUID_KEY, "x:" + str);
        return uTCAdditionalInfoModel;
    }

    public static void trackChangeRelationshipAction(final CharSequence charSequence, final String str, final boolean z, final boolean z2) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCAdditionalInfoModel access$200 = UTCChangeRelationship.getBaseInfoModel(str);
                access$200.addValue("relationship", Integer.valueOf(z ? Relationship.EXISTINGFRIEND.getValue() : Relationship.ADDFRIEND.getValue()));
                UTCPageAction.track(ChangeRelationship.Action, charSequence, access$200);
                if (z2) {
                    UTCChangeRelationship.trackChangeRelationshipDone(charSequence, str, Relationship.ADDFRIEND, RealNameStatus.SHARINGON, FavoriteStatus.NOTFAVORITED, GamerType.FACEBOOK);
                }
            }
        });
    }

    public static void trackChangeRelationshipAction(boolean z, boolean z2) {
        verifyTrackedDefaults();
        trackChangeRelationshipAction(currentActivityTitle, currentXUID, z, z2);
    }

    public static void trackChangeRelationshipDone(Relationship relationship, RealNameStatus realNameStatus, FavoriteStatus favoriteStatus, GamerType gamerType) {
        verifyTrackedDefaults();
        trackChangeRelationshipDone(currentActivityTitle, currentXUID, relationship, realNameStatus, favoriteStatus, gamerType);
    }

    public static void trackChangeRelationshipDone(CharSequence charSequence, String str, Relationship relationship, RealNameStatus realNameStatus, FavoriteStatus favoriteStatus, GamerType gamerType) {
        final String str2 = str;
        final Relationship relationship2 = relationship;
        final FavoriteStatus favoriteStatus2 = favoriteStatus;
        final RealNameStatus realNameStatus2 = realNameStatus;
        final GamerType gamerType2 = gamerType;
        final CharSequence charSequence2 = charSequence;
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCAdditionalInfoModel access$200 = UTCChangeRelationship.getBaseInfoModel(str2);
                access$200.addValue("relationship", Integer.valueOf(relationship2.getValue()));
                access$200.addValue("favorite", Integer.valueOf(favoriteStatus2.getValue()));
                access$200.addValue("realname", Integer.valueOf(realNameStatus2.getValue()));
                access$200.addValue("gamertype", Integer.valueOf(gamerType2.getValue()));
                UTCPageAction.track(ChangeRelationship.Done, charSequence2, access$200);
            }
        });
    }

    public static void trackChangeRelationshipRemoveFriend() {
        verifyTrackedDefaults();
        trackChangeRelationshipRemoveFriend(currentActivityTitle, currentXUID);
    }

    public static void trackChangeRelationshipRemoveFriend(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCAdditionalInfoModel access$200 = UTCChangeRelationship.getBaseInfoModel(str);
                access$200.addValue("relationship", Relationship.REMOVEFRIEND);
                UTCPageAction.track(ChangeRelationship.Action, charSequence, access$200);
            }
        });
    }

    public static void trackChangeRelationshipView(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCChangeRelationship.currentActivityTitle = charSequence;
                UTCChangeRelationship.currentXUID = str;
                UTCPageView.track(PageView.ChangeRelationship.ChangeRelationshipView, UTCChangeRelationship.currentActivityTitle, UTCChangeRelationship.getBaseInfoModel(UTCChangeRelationship.currentXUID));
            }
        });
    }

    private static void verifyTrackedDefaults() {
        XLEAssert.assertFalse("Called trackPeopleHubView without set currentXUID", currentXUID.equals(BuildConfig.FLAVOR));
        XLEAssert.assertFalse("Called trackPeopleHubView without set activityTitle", currentActivityTitle.toString().equals(BuildConfig.FLAVOR));
    }
}
