package org.mozilla.javascript;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

public class SecurityUtilities {
    public static ProtectionDomain getProtectionDomain(final Class<?> cls) {
        return (ProtectionDomain) AccessController.doPrivileged(new PrivilegedAction<ProtectionDomain>() {
            public ProtectionDomain run() {
                return cls.getProtectionDomain();
            }
        });
    }

    public static ProtectionDomain getScriptProtectionDomain() {
        final SecurityManager securityManager = System.getSecurityManager();
        return securityManager instanceof RhinoSecurityManager ? (ProtectionDomain) AccessController.doPrivileged(new PrivilegedAction<ProtectionDomain>() {
            public ProtectionDomain run() {
                Class currentScriptClass = ((RhinoSecurityManager) securityManager).getCurrentScriptClass();
                return currentScriptClass == null ? null : currentScriptClass.getProtectionDomain();
            }
        }) : null;
    }

    public static String getSystemProperty(final String str) {
        return (String) AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty(str);
            }
        });
    }
}
