package com.microsoft.xbox.service.network.managers;

import java.util.ArrayList;
import java.util.Iterator;

public final class NeverListResultContainer {

    public static class NeverListResult {
        public ArrayList<NeverUser> users = new ArrayList();

        public void add(String str) {
            this.users.add(new NeverUser(str));
        }

        public boolean contains(String str) {
            Iterator it = this.users.iterator();
            while (it.hasNext()) {
                if (((NeverUser) it.next()).xuid.equalsIgnoreCase(str)) {
                    return true;
                }
            }
            return false;
        }

        public NeverUser remove(String str) {
            Iterator it = this.users.iterator();
            while (it.hasNext()) {
                NeverUser neverUser = (NeverUser) it.next();
                if (neverUser.xuid.equalsIgnoreCase(str)) {
                    this.users.remove(neverUser);
                    return neverUser;
                }
            }
            return null;
        }
    }

    public static class NeverUser {
        public String xuid;

        public NeverUser(String str) {
            this.xuid = str;
        }
    }
}
