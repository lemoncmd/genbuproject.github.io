package com.microsoft.xbox.service.model.friendfinder;

public class FriendFinderState {

    public static class FriendsFinderStateResult {
        public String facebookOptInStatus;
        public String facebookTokenStatus;
        public String phoneOptInStatus;

        public LinkedAccountOptInStatus getLinkedAccountOptInStatus() {
            try {
                return LinkedAccountOptInStatus.getOptedInStatus(this.facebookOptInStatus);
            } catch (IllegalArgumentException e) {
                return LinkedAccountOptInStatus.Unknown;
            }
        }

        public LinkedAccountTokenStatus getLinkedAccountTokenStatus() {
            try {
                return LinkedAccountTokenStatus.getTokenStatus(this.facebookTokenStatus);
            } catch (IllegalArgumentException e) {
                return LinkedAccountTokenStatus.Unknown;
            }
        }

        public LinkedAccountOptInStatus getPhoneAccountOptInStatus() {
            try {
                return LinkedAccountOptInStatus.getOptedInStatus(this.phoneOptInStatus);
            } catch (IllegalArgumentException e) {
                return LinkedAccountOptInStatus.Unknown;
            }
        }

        public boolean isFacebookStateChanged(FriendsFinderStateResult friendsFinderStateResult) {
            return (getLinkedAccountOptInStatus() == friendsFinderStateResult.getLinkedAccountOptInStatus() && getLinkedAccountTokenStatus() == friendsFinderStateResult.getLinkedAccountTokenStatus()) ? false : true;
        }

        public boolean isPhoneStateChanged(FriendsFinderStateResult friendsFinderStateResult) {
            return getPhoneAccountOptInStatus() != friendsFinderStateResult.getPhoneAccountOptInStatus();
        }
    }

    public enum LinkedAccountOptInStatus {
        Unknown,
        Unset,
        Excluded,
        NotShown,
        ShowPrompt,
        OptedIn,
        OptedOut;

        public static LinkedAccountOptInStatus getOptedInStatus(String str) {
            for (LinkedAccountOptInStatus linkedAccountOptInStatus : values()) {
                if (linkedAccountOptInStatus.name().equalsIgnoreCase(str)) {
                    return linkedAccountOptInStatus;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    public enum LinkedAccountTokenStatus {
        Unknown,
        Unset,
        OK,
        TokenRenewalRequired;

        public static LinkedAccountTokenStatus getTokenStatus(String str) {
            for (LinkedAccountTokenStatus linkedAccountTokenStatus : values()) {
                if (linkedAccountTokenStatus.name().equalsIgnoreCase(str)) {
                    return linkedAccountTokenStatus;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
