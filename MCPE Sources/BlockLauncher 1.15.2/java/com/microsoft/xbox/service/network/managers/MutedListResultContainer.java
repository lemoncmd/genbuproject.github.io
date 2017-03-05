package com.microsoft.xbox.service.network.managers;

import java.util.ArrayList;
import java.util.Iterator;

public final class MutedListResultContainer {

    public static class MutedListResult {
        public ArrayList<MutedUser> users = new ArrayList();

        public void add(String str) {
            this.users.add(new MutedUser(str));
        }

        public boolean contains(String str) {
            Iterator it = this.users.iterator();
            while (it.hasNext()) {
                if (((MutedUser) it.next()).xuid.equalsIgnoreCase(str)) {
                    return true;
                }
            }
            return false;
        }

        public MutedUser remove(String str) {
            Iterator it = this.users.iterator();
            while (it.hasNext()) {
                MutedUser mutedUser = (MutedUser) it.next();
                if (mutedUser.xuid.equalsIgnoreCase(str)) {
                    this.users.remove(mutedUser);
                    return mutedUser;
                }
            }
            return null;
        }
    }

    public static class MutedUser {
        public String xuid;

        public MutedUser(String str) {
            this.xuid = str;
        }
    }
}
