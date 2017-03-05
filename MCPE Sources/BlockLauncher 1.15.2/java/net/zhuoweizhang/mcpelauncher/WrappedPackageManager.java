package net.zhuoweizhang.mcpelauncher;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import java.util.List;

public class WrappedPackageManager extends PackageManager {
    protected PackageManager wrapped;

    public WrappedPackageManager(PackageManager wrapped) {
        this.wrapped = wrapped;
    }

    public PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException {
        return this.wrapped.getPackageInfo(packageName, flags);
    }

    public String[] currentToCanonicalPackageNames(String[] names) {
        return this.wrapped.currentToCanonicalPackageNames(names);
    }

    public String[] canonicalToCurrentPackageNames(String[] names) {
        return this.wrapped.canonicalToCurrentPackageNames(names);
    }

    public Intent getLaunchIntentForPackage(String packageName) {
        return this.wrapped.getLaunchIntentForPackage(packageName);
    }

    public int[] getPackageGids(String packageName) throws NameNotFoundException {
        return this.wrapped.getPackageGids(packageName);
    }

    public PermissionInfo getPermissionInfo(String name, int flags) throws NameNotFoundException {
        return this.wrapped.getPermissionInfo(name, flags);
    }

    public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) throws NameNotFoundException {
        return this.wrapped.queryPermissionsByGroup(group, flags);
    }

    public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws NameNotFoundException {
        return this.wrapped.getPermissionGroupInfo(name, flags);
    }

    public List<PermissionGroupInfo> getAllPermissionGroups(int flags) {
        return this.wrapped.getAllPermissionGroups(flags);
    }

    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws NameNotFoundException {
        return this.wrapped.getApplicationInfo(packageName, flags);
    }

    public ActivityInfo getActivityInfo(ComponentName className, int flags) throws NameNotFoundException {
        return this.wrapped.getActivityInfo(className, flags);
    }

    public ActivityInfo getReceiverInfo(ComponentName className, int flags) throws NameNotFoundException {
        return this.wrapped.getReceiverInfo(className, flags);
    }

    public ServiceInfo getServiceInfo(ComponentName className, int flags) throws NameNotFoundException {
        return this.wrapped.getServiceInfo(className, flags);
    }

    public ProviderInfo getProviderInfo(ComponentName className, int flags) throws NameNotFoundException {
        return this.wrapped.getProviderInfo(className, flags);
    }

    public List<PackageInfo> getInstalledPackages(int flags) {
        return this.wrapped.getInstalledPackages(flags);
    }

    public int checkPermission(String permName, String pkgName) {
        return this.wrapped.checkPermission(permName, pkgName);
    }

    public boolean addPermission(PermissionInfo info) {
        return this.wrapped.addPermission(info);
    }

    public boolean addPermissionAsync(PermissionInfo info) {
        return this.wrapped.addPermissionAsync(info);
    }

    public void removePermission(String name) {
        this.wrapped.removePermission(name);
    }

    public int checkSignatures(String pkg1, String pkg2) {
        return this.wrapped.checkSignatures(pkg1, pkg2);
    }

    public int checkSignatures(int uid1, int uid2) {
        return this.wrapped.checkSignatures(uid1, uid2);
    }

    public String[] getPackagesForUid(int uid) {
        return this.wrapped.getPackagesForUid(uid);
    }

    public String getNameForUid(int uid) {
        return this.wrapped.getNameForUid(uid);
    }

    public List<ApplicationInfo> getInstalledApplications(int flags) {
        return this.wrapped.getInstalledApplications(flags);
    }

    public ResolveInfo resolveActivity(Intent intent, int flags) {
        return this.wrapped.resolveActivity(intent, flags);
    }

    public List<ResolveInfo> queryIntentActivities(Intent intent, int flags) {
        return this.wrapped.queryIntentActivities(intent, flags);
    }

    public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller, Intent[] specifics, Intent intent, int flags) {
        return this.wrapped.queryIntentActivityOptions(caller, specifics, intent, flags);
    }

    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags) {
        return this.wrapped.queryBroadcastReceivers(intent, flags);
    }

    public ResolveInfo resolveService(Intent intent, int flags) {
        return this.wrapped.resolveService(intent, flags);
    }

    public List<ResolveInfo> queryIntentServices(Intent intent, int flags) {
        return this.wrapped.queryIntentServices(intent, flags);
    }

    public ProviderInfo resolveContentProvider(String name, int flags) {
        return this.wrapped.resolveContentProvider(name, flags);
    }

    public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags) {
        return this.wrapped.queryContentProviders(processName, uid, flags);
    }

    public InstrumentationInfo getInstrumentationInfo(ComponentName className, int flags) throws NameNotFoundException {
        return this.wrapped.getInstrumentationInfo(className, flags);
    }

    public List<InstrumentationInfo> queryInstrumentation(String targetPackage, int flags) {
        return this.wrapped.queryInstrumentation(targetPackage, flags);
    }

    public Drawable getDrawable(String packageName, int resid, ApplicationInfo appInfo) {
        return this.wrapped.getDrawable(packageName, resid, appInfo);
    }

    public Drawable getActivityIcon(ComponentName activityName) throws NameNotFoundException {
        return this.wrapped.getActivityIcon(activityName);
    }

    public Drawable getActivityIcon(Intent intent) throws NameNotFoundException {
        return this.wrapped.getActivityIcon(intent);
    }

    public Drawable getDefaultActivityIcon() {
        return this.wrapped.getDefaultActivityIcon();
    }

    public Drawable getApplicationIcon(ApplicationInfo info) {
        return this.wrapped.getApplicationIcon(info);
    }

    public Drawable getApplicationIcon(String packageName) throws NameNotFoundException {
        return this.wrapped.getApplicationIcon(packageName);
    }

    public Drawable getActivityLogo(ComponentName activityName) throws NameNotFoundException {
        return this.wrapped.getActivityLogo(activityName);
    }

    public Drawable getActivityLogo(Intent intent) throws NameNotFoundException {
        return this.wrapped.getActivityLogo(intent);
    }

    public Drawable getApplicationLogo(ApplicationInfo info) {
        return this.wrapped.getApplicationLogo(info);
    }

    public Drawable getApplicationLogo(String packageName) throws NameNotFoundException {
        return this.wrapped.getApplicationLogo(packageName);
    }

    public CharSequence getText(String packageName, int resid, ApplicationInfo appInfo) {
        return this.wrapped.getText(packageName, resid, appInfo);
    }

    public XmlResourceParser getXml(String packageName, int resid, ApplicationInfo appInfo) {
        return this.wrapped.getXml(packageName, resid, appInfo);
    }

    public CharSequence getApplicationLabel(ApplicationInfo info) {
        return this.wrapped.getApplicationLabel(info);
    }

    public Resources getResourcesForActivity(ComponentName activityName) throws NameNotFoundException {
        return this.wrapped.getResourcesForActivity(activityName);
    }

    public Resources getResourcesForApplication(ApplicationInfo app) throws NameNotFoundException {
        return this.wrapped.getResourcesForApplication(app);
    }

    public Resources getResourcesForApplication(String appPackageName) throws NameNotFoundException {
        return this.wrapped.getResourcesForApplication(appPackageName);
    }

    public PackageInfo getPackageArchiveInfo(String archiveFilePath, int flags) {
        return this.wrapped.getPackageArchiveInfo(archiveFilePath, flags);
    }

    public String getInstallerPackageName(String packageName) {
        return this.wrapped.getInstallerPackageName(packageName);
    }

    public void addPackageToPreferred(String packageName) {
        this.wrapped.addPackageToPreferred(packageName);
    }

    public void removePackageFromPreferred(String packageName) {
        this.wrapped.removePackageFromPreferred(packageName);
    }

    public List<PackageInfo> getPreferredPackages(int flags) {
        return this.wrapped.getPreferredPackages(flags);
    }

    public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags) {
        this.wrapped.setComponentEnabledSetting(componentName, newState, flags);
    }

    public int getComponentEnabledSetting(ComponentName componentName) {
        return this.wrapped.getComponentEnabledSetting(componentName);
    }

    public void setApplicationEnabledSetting(String packageName, int newState, int flags) {
        this.wrapped.setApplicationEnabledSetting(packageName, newState, flags);
    }

    public int getApplicationEnabledSetting(String packageName) {
        return this.wrapped.getApplicationEnabledSetting(packageName);
    }

    public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity) {
        this.wrapped.addPreferredActivity(filter, match, set, activity);
    }

    public void clearPackagePreferredActivities(String packageName) {
        this.wrapped.clearPackagePreferredActivities(packageName);
    }

    public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) {
        return this.wrapped.getPreferredActivities(outFilters, outActivities, packageName);
    }

    public String[] getSystemSharedLibraryNames() {
        return this.wrapped.getSystemSharedLibraryNames();
    }

    public FeatureInfo[] getSystemAvailableFeatures() {
        return this.wrapped.getSystemAvailableFeatures();
    }

    public boolean hasSystemFeature(String name) {
        return this.wrapped.hasSystemFeature(name);
    }

    public boolean isSafeMode() {
        return this.wrapped.isSafeMode();
    }

    public List<PackageInfo> getPackagesHoldingPermissions(String[] permissions, int flags) {
        return this.wrapped.getPackagesHoldingPermissions(permissions, flags);
    }

    public List<ResolveInfo> queryIntentContentProviders(Intent intent, int flags) {
        return this.wrapped.queryIntentContentProviders(intent, flags);
    }

    public void verifyPendingInstall(int id, int verificationCode) {
        this.wrapped.verifyPendingInstall(id, verificationCode);
    }

    public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) {
        this.wrapped.extendVerificationTimeout(id, verificationCodeAtTimeout, millisecondsToDelay);
    }

    public void setInstallerPackageName(String targetPackage, String installerPackageName) {
        this.wrapped.setInstallerPackageName(targetPackage, installerPackageName);
    }

    public Drawable getActivityBanner(ComponentName activityName) throws NameNotFoundException {
        return this.wrapped.getActivityBanner(activityName);
    }

    public Drawable getActivityBanner(Intent intent) throws NameNotFoundException {
        return this.wrapped.getActivityBanner(intent);
    }

    public Drawable getApplicationBanner(ApplicationInfo info) {
        return this.wrapped.getApplicationBanner(info);
    }

    public Drawable getApplicationBanner(String packageName) throws NameNotFoundException {
        return this.wrapped.getApplicationBanner(packageName);
    }

    public Intent getLeanbackLaunchIntentForPackage(String packageName) {
        return this.wrapped.getLeanbackLaunchIntentForPackage(packageName);
    }

    public PackageInstaller getPackageInstaller() {
        return this.wrapped.getPackageInstaller();
    }

    public Drawable getUserBadgedIcon(Drawable icon, UserHandle user) {
        return this.wrapped.getUserBadgedIcon(icon, user);
    }

    public Drawable getUserBadgedDrawableForDensity(Drawable drawable, UserHandle user, Rect badgeLocation, int badgeDensity) {
        return this.wrapped.getUserBadgedDrawableForDensity(drawable, user, badgeLocation, badgeDensity);
    }

    public CharSequence getUserBadgedLabel(CharSequence label, UserHandle user) {
        return this.wrapped.getUserBadgedLabel(label, user);
    }

    public int getSystemFeatureLevel(String feature) {
        try {
            return ((Integer) this.wrapped.getClass().getMethod("getSystemFeatureLevel", new Class[]{String.class}).invoke(this.wrapped, new Object[]{feature})).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
