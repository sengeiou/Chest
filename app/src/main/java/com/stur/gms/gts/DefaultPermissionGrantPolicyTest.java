package com.stur.gms.gts;

public final class DefaultPermissionGrantPolicyTest extends BusinessLogicTestCase
{
    /*private static final String ACTION_EMERGENCY_ASSISTANCE = "android.telephony.action.EMERGENCY_ASSISTANCE";
    private static final String AUDIO_MIME_TYPE = "audio/mpeg";
    private static final String BRAND_PROPERTY = "ro.product.brand";
    private static final Set<String> CALENDAR_PERMISSIONS;
    private static final Set<String> CAMERA_PERMISSIONS;
    private static final Set<String> CONTACTS_PERMISSIONS;
    private static final char[] HEX_ARRAY;
    private static final Set<String> LOCATION_PERMISSIONS;
    private static final String LOCATION_PROVIDER_PACKAGE = "com.google.android.gms";
    private static final String LOG_TAG = "DefaultPermissionGrantPolicyTest";
    private static final Set<String> MICROPHONE_PERMISSIONS;
    private static final String PACKAGE_MIME_TYPE = "application/vnd.android.package-archive";
    private static final Set<String> PHONE_PERMISSIONS;
    private static final String PLATFORM_PACKAGE_NAME = "android";
    private static final Set<String> RESTRICTED_LOCATION_PERMISSIONS;
    private static final Set<String> SENSORS_PERMISSIONS;
    private static final Set<String> SMS_PERMISSIONS;
    private static final Set<String> STORAGE_PERMISSIONS;
    private Set<DefaultPermissionGrantException> mRemoteExceptions;

    static {
        HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        (PHONE_PERMISSIONS = (Set)new ArraySet()).add("android.permission.READ_PHONE_STATE");
        DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS.add("android.permission.CALL_PHONE");
        DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS.add("android.permission.READ_CALL_LOG");
        DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS.add("android.permission.WRITE_CALL_LOG");
        DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS.add("com.android.voicemail.permission.ADD_VOICEMAIL");
        DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS.add("android.permission.USE_SIP");
        DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS.add("android.permission.PROCESS_OUTGOING_CALLS");
        (CONTACTS_PERMISSIONS = (Set)new ArraySet()).add("android.permission.READ_CONTACTS");
        DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS.add("android.permission.WRITE_CONTACTS");
        DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS.add("android.permission.GET_ACCOUNTS");
        (LOCATION_PERMISSIONS = (Set)new ArraySet()).add("android.permission.ACCESS_FINE_LOCATION");
        DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS.add("android.permission.ACCESS_COARSE_LOCATION");
        (RESTRICTED_LOCATION_PERMISSIONS = (Set)new ArraySet()).add("android.permission.ACCESS_COARSE_LOCATION");
        (CALENDAR_PERMISSIONS = (Set)new ArraySet()).add("android.permission.READ_CALENDAR");
        DefaultPermissionGrantPolicyTest.CALENDAR_PERMISSIONS.add("android.permission.WRITE_CALENDAR");
        (SMS_PERMISSIONS = (Set)new ArraySet()).add("android.permission.SEND_SMS");
        DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS.add("android.permission.RECEIVE_SMS");
        DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS.add("android.permission.READ_SMS");
        DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS.add("android.permission.RECEIVE_WAP_PUSH");
        DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS.add("android.permission.RECEIVE_MMS");
        DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS.add("android.permission.READ_CELL_BROADCASTS");
        (MICROPHONE_PERMISSIONS = (Set)new ArraySet()).add("android.permission.RECORD_AUDIO");
        (CAMERA_PERMISSIONS = (Set)new ArraySet()).add("android.permission.CAMERA");
        (SENSORS_PERMISSIONS = (Set)new ArraySet()).add("android.permission.BODY_SENSORS");
        (STORAGE_PERMISSIONS = (Set)new ArraySet()).add("android.permission.READ_EXTERNAL_STORAGE");
        DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS.add("android.permission.WRITE_EXTERNAL_STORAGE");
    }

    public DefaultPermissionGrantPolicyTest() {
        this.mRemoteExceptions = new HashSet<DefaultPermissionGrantException>();
    }

    private void addBrowserDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final PackageManager packageManager = BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager();
        final int userId = BusinessLogicTestCase.getInstrumentation().getContext().getUserId();
        String s;
        if (ApiLevelUtil.isAtLeast(24)) {
            s = packageManager.getDefaultBrowserPackageNameAsUser(userId);
        }
        else {
            s = (String)PackageManager.class.getMethod("getDefaultBrowserPackageName", Integer.TYPE).invoke(packageManager, userId);
        }
        if (s == null) {
            final Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.APP_BROWSER");
            s = this.getDefaultSystemHandlerActivityPackageName(intent);
        }
        if (s != null) {
            final PackageInfo packageInfo = map.get(s);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addCalendarDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_CALENDAR");
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(intent);
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CALENDAR_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addCalendarProviderDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultProviderAuthorityPackageName = this.getDefaultProviderAuthorityPackageName("com.android.calendar");
        if (defaultProviderAuthorityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultProviderAuthorityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.CALENDAR_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addCalendarProviderSyncAdaptersDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String[] headlessSyncAdapterPackagesForAuthority = this.getHeadlessSyncAdapterPackagesForAuthority("com.android.calendar");
        if (headlessSyncAdapterPackagesForAuthority != null) {
            for (int length = headlessSyncAdapterPackagesForAuthority.length, i = 0; i < length; ++i) {
                final PackageInfo packageInfo = map.get(headlessSyncAdapterPackagesForAuthority[i]);
                if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                    appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CALENDAR_PERMISSIONS, sparseArray);
                }
            }
        }
    }

    private void addCameraDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(new Intent("android.media.action.IMAGE_CAPTURE"));
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CAMERA_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.MICROPHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addCarrierAppsDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final IPackageManager interface1 = IPackageManager$Stub.asInterface(ServiceManager.getService("package"));
        final Context context = BusinessLogicTestCase.getInstrumentation().getContext();
        final List defaultCarrierApps = CarrierAppUtils.getDefaultCarrierApps(interface1, (TelephonyManager)context.getSystemService((Class)TelephonyManager.class), context.getUserId());
        if (defaultCarrierApps != null) {
            final Iterator<ApplicationInfo> iterator = defaultCarrierApps.iterator();
            while (iterator.hasNext()) {
                final PackageInfo packageInfo = map.get(iterator.next().packageName);
                if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                    appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
                    appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
                    appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS, sparseArray);
                }
            }
        }
    }

    private void addCarrierProvServiceDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultSystemHandlerServicePackageName = this.getDefaultSystemHandlerServicePackageName(new Intent("android.provider.Telephony.SMS_CARRIER_PROVISION"));
        if (defaultSystemHandlerServicePackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerServicePackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addCellBroadcastReceiverDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(new Intent("android.provider.Telephony.SMS_CB_RECEIVED"));
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addCertInstallerDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(new Intent("android.credentials.INSTALL"));
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addCompanioonDeviceManagerDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final PackageInfo packageInfo = map.get("com.android.companiondevicemanager");
        if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
            appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
        }
    }

    private void addContactsDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_CONTACTS");
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(intent);
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addContactsProviderProviderDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultProviderAuthorityPackageName = this.getDefaultProviderAuthorityPackageName("com.android.contacts");
        if (defaultProviderAuthorityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultProviderAuthorityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addContactsProviderSyncAdaptersDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        String[] array;
        if (BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager().hasSystemFeature("android.hardware.type.watch")) {
            array = this.getHeadlessSyncAdapterPackages(new String[] { "com.google.android.syncadapters.contacts" }, 512);
        }
        else {
            array = this.getHeadlessSyncAdapterPackagesForAuthority("com.android.contacts");
        }
        if (array != null) {
            for (int length = array.length, i = 0; i < length; ++i) {
                final PackageInfo packageInfo = map.get(array[i]);
                if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                    appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
                }
            }
        }
    }

    private void addContainerDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final PackageInfo packageInfo = map.get("com.android.defcontainer");
        if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
            appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
        }
    }

    private void addDefaultDialerDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        String s = DefaultDialerManager.getDefaultDialerApplication(BusinessLogicTestCase.getInstrumentation().getContext());
        if (s == null) {
            s = this.getDefaultSystemHandlerActivityPackageName(new Intent("android.intent.action.DIAL"));
        }
        if (s != null) {
            final PackageInfo packageInfo = map.get(s);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager().hasSystemFeature("android.hardware.type.watch"), DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.MICROPHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CAMERA_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addDefaultSmsDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final ComponentName defaultMmsApplication = SmsApplication.getDefaultMmsApplication(BusinessLogicTestCase.getInstrumentation().getContext(), false);
        String s = null;
        if (defaultMmsApplication != null) {
            s = defaultMmsApplication.getPackageName();
        }
        if (s == null) {
            final Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.APP_MESSAGING");
            s = this.getDefaultSystemHandlerActivityPackageName(intent);
        }
        if (s != null) {
            final PackageInfo packageInfo = map.get(s);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.MICROPHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CAMERA_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addDefaultUseOpenWifiDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String string = Settings$Global.getString(BusinessLogicTestCase.getInstrumentation().getContext().getContentResolver(), "use_open_wifi_package");
        if (!TextUtils.isEmpty((CharSequence)string)) {
            final PackageInfo packageInfo = map.get(string);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.RESTRICTED_LOCATION_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addDeviceProvisioningDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(new Intent("android.app.action.PROVISION_MANAGED_DEVICE"));
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addDownloadsProviderDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultProviderAuthorityPackageName = this.getDefaultProviderAuthorityPackageName("downloads");
        if (defaultProviderAuthorityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultProviderAuthorityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addDownloadsUiDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(new Intent("android.intent.action.VIEW_DOWNLOADS"));
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addEmailDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_EMAIL");
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(intent);
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CALENDAR_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addEmergencyInfoDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(new Intent("android.telephony.action.EMERGENCY_ASSISTANCE"));
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addException(final DefaultPermissionGrantException ex, final Set<String> set, final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) {
        final String pkg = ex.pkg;
        final PackageInfo packageInfo = map.get(pkg);
        if (packageInfo == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Trying to add exception to missing package:");
            sb.append(pkg);
            Log.w("DefaultPermissionGrantPolicyTest", sb.toString());
            return;
        }
        if (!packageInfo.applicationInfo.isSystemApp()) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("Cannot pregrant permissions to non-system package:");
            sb2.append(pkg);
            Log.w("DefaultPermissionGrantPolicyTest", sb2.toString());
            return;
        }
        if (ex.sha256 != null && !ex.hasBrand) {
            final String lowerCase = ex.sha256.replace(":", "").toLowerCase();
            final String computePackageCertDigest = computePackageCertDigest(packageInfo.signatures[0]);
            if (PropertyUtil.isUserBuild() && !lowerCase.equalsIgnoreCase(computePackageCertDigest)) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append("SHA256 cert digest does not match for package: ");
                sb3.append(pkg);
                Log.w("DefaultPermissionGrantPolicyTest", sb3.toString());
                return;
            }
        }
        else {
            if (!ex.hasBrand) {
                final StringBuilder sb4 = new StringBuilder();
                sb4.append("Attribute sha256-cert-digest or brand must be provided for package: ");
                sb4.append(pkg);
                Log.w("DefaultPermissionGrantPolicyTest", sb4.toString());
                return;
            }
            final String sha256 = ex.sha256;
            if (!sha256.equalsIgnoreCase(PropertyUtil.getProperty("ro.product.brand"))) {
                Log.w("DefaultPermissionGrantPolicyTest", String.format("Brand %s does not match for package: %s", sha256, pkg));
                return;
            }
        }
        for (final Map.Entry<String, Boolean> entry : ex.permissions.entrySet()) {
            final String s = entry.getKey();
            final Boolean b = entry.getValue();
            if (!ArrayUtils.contains((Object[])packageInfo.requestedPermissions, (Object)s)) {
                final StringBuilder sb5 = new StringBuilder();
                sb5.append("Permission ");
                sb5.append(s);
                sb5.append(" not requested by: ");
                sb5.append(pkg);
                Log.w("DefaultPermissionGrantPolicyTest", sb5.toString());
            }
            else if (!set.contains(s)) {
                final StringBuilder sb6 = new StringBuilder();
                sb6.append("Permission:");
                sb6.append(s);
                sb6.append(" in not runtime");
                Log.w("DefaultPermissionGrantPolicyTest", sb6.toString());
            }
            else {
                final int uid = packageInfo.applicationInfo.uid;
                UidState uidState = (UidState)sparseArray.get(uid);
                if (uidState == null) {
                    uidState = new UidState();
                    sparseArray.put(uid, (Object)uidState);
                }
                uidState.addGrantedPermission(s, b);
            }
        }
    }

    private void addExceptionsDefaultPermissions(final Map<String, PackageInfo> map, final Set<String> set, final SparseArray<UidState> sparseArray) throws Exception {
        if (!this.mRemoteExceptions.isEmpty()) {
            Log.i("DefaultPermissionGrantPolicyTest", String.format("Found %d remote exceptions", this.mRemoteExceptions.size()));
            final Iterator<DefaultPermissionGrantException> iterator = this.mRemoteExceptions.iterator();
            while (iterator.hasNext()) {
                this.addException(iterator.next(), set, map, sparseArray);
            }
        }
        else {
            Log.w("DefaultPermissionGrantPolicyTest", "Failed to retrieve remote default permission grant exceptions.");
        }
    }

    private void addGalleryDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_GALLERY");
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(intent);
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addGlobalSearchDefaultPermissionsLowram(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        if (!BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager().hasSystemFeature("android.hardware.ram.low")) {
            return;
        }
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(new Intent("android.search.action.GLOBAL_SEARCH"));
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.MICROPHONE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addHomeDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.LAUNCHER_APP");
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(intent);
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addImeDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        if (ApiLevelUtil.isAtMost(23)) {
            final Iterator<InputMethodInfo> iterator = ((InputMethodManager)BusinessLogicTestCase.getInstrumentation().getContext().getSystemService((Class)InputMethodManager.class)).getInputMethodList().iterator();
            while (iterator.hasNext()) {
                final PackageInfo packageInfo = map.get(iterator.next().getPackageName());
                if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                    appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
                }
            }
        }
    }

    private void addImsServiceAppsDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final Iterator<String> iterator = this.getDefaultSystemHandlerServicePackageNames(new Intent("android.telephony.ims.ImsService")).iterator();
        while (iterator.hasNext()) {
            final PackageInfo packageInfo = map.get(iterator.next());
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.MICROPHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CAMERA_PERMISSIONS, sparseArray);
                if (!ApiLevelUtil.isAtLeast(28)) {
                    continue;
                }
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addInstallerDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String installerPackageName = getInstallerPackageName(BusinessLogicTestCase.getInstrumentation().getContext());
        if (installerPackageName != null) {
            final PackageInfo packageInfo = map.get(installerPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addLocationDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final PackageInfo packageInfo = map.get("com.google.android.gms");
        if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
            appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CALENDAR_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.MICROPHONE_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CAMERA_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.SENSORS_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
        }
    }

    private void addMapsDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MAPS");
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(intent);
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addMediaProviderDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultProviderAuthorityPackageName = this.getDefaultProviderAuthorityPackageName("media");
        if (defaultProviderAuthorityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultProviderAuthorityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addMusicDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(new File("foo.mp3")), "audio/mpeg");
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(intent);
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addNfcTagAppDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final Intent intent = new Intent("android.intent.action.VIEW");
        intent.setType("vnd.android.cursor.item/ndef_msg");
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(intent);
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addPixelMigrateDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        if (ApiLevelUtil.isAtLeast(26)) {
            final PackageInfo packageInfo = map.get("com.google.android.apps.pixelmigrate");
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addPrintSpoolerDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final PackageInfo packageInfo = map.get("com.android.printspooler");
        if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
            appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
        }
    }

    private void addRingtonePickerDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(new Intent("android.intent.action.RINGTONE_PICKER"));
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addSetupWizardDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.SETUP_WIZARD");
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(intent, 512);
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CAMERA_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addSharedStorageBackupDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final PackageInfo packageInfo = map.get("com.android.sharedstoragebackup");
        if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
            appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
        }
    }

    private void addSimCallManagerInstallerDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final PhoneAccountHandle simCallManager = ((TelecomManager)BusinessLogicTestCase.getInstrumentation().getContext().getSystemService((Class)TelecomManager.class)).getSimCallManager();
        if (simCallManager != null) {
            final PackageInfo packageInfo = map.get(simCallManager.getComponentName().getPackageName());
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.MICROPHONE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addStorageManagerDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(new Intent("android.os.storage.action.MANAGE_STORAGE"));
        if (defaultSystemHandlerActivityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addStorageProviderDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String defaultProviderAuthorityPackageName = this.getDefaultProviderAuthorityPackageName("com.android.externalstorage.documents");
        if (defaultProviderAuthorityPackageName != null) {
            final PackageInfo packageInfo = map.get(defaultProviderAuthorityPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addSysComponentsAndPrivAppsDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        for (final PackageInfo packageInfo : map.values()) {
            if (this.isSysComponentOrPersistentPlatformSignedPrivApp(packageInfo) && doesPackageSupportingRuntimePermissions(packageInfo) && packageInfo.requestedPermissions != null) {
                for (final String s : packageInfo.requestedPermissions) {
                    if (DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS.contains(s) || DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS.contains(s) || DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS.contains(s) || DefaultPermissionGrantPolicyTest.CALENDAR_PERMISSIONS.contains(s) || DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS.contains(s) || DefaultPermissionGrantPolicyTest.MICROPHONE_PERMISSIONS.contains(s) || DefaultPermissionGrantPolicyTest.CAMERA_PERMISSIONS.contains(s) || DefaultPermissionGrantPolicyTest.SENSORS_PERMISSIONS.contains(s) || DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS.contains(s)) {
                        final int uid = packageInfo.applicationInfo.uid;
                        UidState uidState = (UidState)sparseArray.get(uid);
                        if (uidState == null) {
                            uidState = new UidState();
                            sparseArray.put(uid, (Object)uidState);
                        }
                        uidState.addGrantedPermission(s, Boolean.TRUE);
                        packageInfo.requestedPermissions = ArrayUtils.removeString(packageInfo.requestedPermissions, s);
                    }
                }
            }
        }
    }

    private void addTextClassifierDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        if (!ApiLevelUtil.isAtLeast(28)) {
            return;
        }
        final PackageInfo packageInfo = map.get(BusinessLogicTestCase.getContext().getPackageManager().getSystemTextClassifierPackageName());
        if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
            appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.CALENDAR_PERMISSIONS, sparseArray);
            appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS, sparseArray);
        }
    }

    private void addVerifierDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String verifierPackageName = getVerifierPackageName(BusinessLogicTestCase.getInstrumentation().getContext());
        if (verifierPackageName != null) {
            final PackageInfo packageInfo = map.get(verifierPackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, true, DefaultPermissionGrantPolicyTest.STORAGE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addVoiceInteractionDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final String string = Settings$Secure.getString(BusinessLogicTestCase.getInstrumentation().getContext().getContentResolver(), "voice_interaction_service");
        if (TextUtils.isEmpty((CharSequence)string)) {
            return;
        }
        final ComponentName unflattenFromString = ComponentName.unflattenFromString(string);
        if (unflattenFromString == null) {
            return;
        }
        final String packageName = unflattenFromString.getPackageName();
        if (packageName != null) {
            final PackageInfo packageInfo = map.get(packageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CONTACTS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.CALENDAR_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.MICROPHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS, sparseArray);
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addVoiceRecognitionDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final Intent intent = new Intent("android.speech.RecognitionService");
        intent.addCategory("android.intent.category.DEFAULT");
        final String defaultSystemHandlerServicePackageName = this.getDefaultSystemHandlerServicePackageName(intent);
        if (defaultSystemHandlerServicePackageName != null) {
            final PackageInfo packageInfo = map.get(defaultSystemHandlerServicePackageName);
            if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.MICROPHONE_PERMISSIONS, sparseArray);
            }
        }
    }

    private void addVrServiceDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        final PackageManager packageManager = BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager();
        if (packageManager.hasSystemFeature("android.hardware.vr.high_performance")) {
            final String string = Settings$Secure.getString(BusinessLogicTestCase.getInstrumentation().getContext().getContentResolver(), "enabled_vr_listeners");
            if (TextUtils.isEmpty((CharSequence)string)) {
                return;
            }
            final ArraySet set = new ArraySet();
            final String[] split = string.split(":");
            for (int length = split.length, i = 0; i < length; ++i) {
                final String packageName = ComponentName.unflattenFromString(split[i]).getPackageName();
                ApplicationInfo applicationInfo = null;
                try {
                    applicationInfo = packageManager.getApplicationInfo(packageName, 128);
                }
                catch (PackageManager$NameNotFoundException ex) {}
                if (applicationInfo != null && (0x1 & applicationInfo.flags) != 0x0) {
                    set.add((Object)packageName);
                }
            }
            final Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                final PackageInfo packageInfo = map.get(iterator.next());
                if (packageInfo != null) {
                    appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.RESTRICTED_LOCATION_PERMISSIONS, sparseArray);
                }
            }
        }
    }

    private void addWearFitnessDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        if (BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager().hasSystemFeature("android.hardware.type.watch")) {
            final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(new Intent("com.android.fitness.TRACK"), 512);
            if (defaultSystemHandlerActivityPackageName != null) {
                final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
                if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                    appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.SENSORS_PERMISSIONS, sparseArray);
                    appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.LOCATION_PERMISSIONS, sparseArray);
                }
            }
        }
    }

    private void addWearTwinningDefaultPermissions(final Map<String, PackageInfo> map, final SparseArray<UidState> sparseArray) throws Exception {
        if (BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager().hasSystemFeature("android.hardware.type.watch")) {
            final String defaultSystemHandlerActivityPackageName = this.getDefaultSystemHandlerActivityPackageName(new Intent("com.google.android.clockwork.intent.TWINNING_SETTINGS"));
            if (defaultSystemHandlerActivityPackageName != null) {
                final PackageInfo packageInfo = map.get(defaultSystemHandlerActivityPackageName);
                if (packageInfo != null && doesPackageSupportingRuntimePermissions(packageInfo)) {
                    appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.PHONE_PERMISSIONS, sparseArray);
                    appendPackagePregrantedPerms(packageInfo, false, DefaultPermissionGrantPolicyTest.SMS_PERMISSIONS, sparseArray);
                }
            }
        }
    }

    private static void appendPackagePregrantedPerms(final PackageInfo packageInfo, final boolean b, final Set<String> set, final SparseArray<UidState> sparseArray) {
        final int uid = packageInfo.applicationInfo.uid;
        UidState uidState = (UidState)sparseArray.get(uid);
        if (uidState == null) {
            uidState = new UidState();
            sparseArray.put(uid, (Object)uidState);
        }
        for (final String s : packageInfo.requestedPermissions) {
            if (set.contains(s)) {
                uidState.addGrantedPermission(s, b);
            }
        }
    }

    private static String computePackageCertDigest(final Signature signature) {
        try {
            final MessageDigest instance = MessageDigest.getInstance("SHA256");
            instance.update(signature.toByteArray());
            final byte[] digest = instance.digest();
            final int length = digest.length;
            final char[] array = new char[2 * length];
            for (int i = 0; i < length; ++i) {
                final int n = 0xFF & digest[i];
                array[i * 2] = DefaultPermissionGrantPolicyTest.HEX_ARRAY[n >>> 4];
                array[1 + i * 2] = DefaultPermissionGrantPolicyTest.HEX_ARRAY[n & 0xF];
            }
            return new String(array);
        }
        catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }

    private static boolean doesPackageSupportingRuntimePermissions(final PackageInfo packageInfo) {
        return (0x1 & packageInfo.applicationInfo.flags) != 0x0;
    }

    private List<PackageInfo> getAllPackages() {
        return (List<PackageInfo>)BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager().getInstalledPackages(12352);
    }

    private String getDefaultProviderAuthorityPackageName(final String s) {
        final ProviderInfo resolveContentProvider = BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager().resolveContentProvider(s, 0);
        if (resolveContentProvider != null && (0x1 & resolveContentProvider.applicationInfo.flags) != 0x0) {
            return resolveContentProvider.packageName;
        }
        return null;
    }

    private String getDefaultSystemHandlerActivityPackageName(final Intent intent) {
        return this.getDefaultSystemHandlerActivityPackageName(intent, 0);
    }

    private String getDefaultSystemHandlerActivityPackageName(final Intent intent, final int n) {
        final ResolveInfo resolveActivity = BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager().resolveActivity(intent, n);
        if (resolveActivity == null) {
            return null;
        }
        if ((0x1 & resolveActivity.activityInfo.applicationInfo.flags) != 0x0) {
            return resolveActivity.activityInfo.packageName;
        }
        return null;
    }

    private String getDefaultSystemHandlerServicePackageName(final Intent intent) {
        final List<String> defaultSystemHandlerServicePackageNames = this.getDefaultSystemHandlerServicePackageNames(intent);
        if (defaultSystemHandlerServicePackageNames.isEmpty()) {
            return null;
        }
        return defaultSystemHandlerServicePackageNames.get(0);
    }

    private List<String> getDefaultSystemHandlerServicePackageNames(final Intent intent) {
        final ArrayList<String> list = new ArrayList<String>();
        final List queryIntentServices = BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager().queryIntentServices(intent, 512);
        if (queryIntentServices == null) {
            return list;
        }
        for (final ResolveInfo resolveInfo : queryIntentServices) {
            if ((0x1 & resolveInfo.serviceInfo.applicationInfo.flags) != 0x0) {
                list.add(resolveInfo.serviceInfo.packageName);
            }
        }
        return list;
    }

    private String[] getHeadlessSyncAdapterPackages(final String[] array, final int n) {
        final PackageManager packageManager = BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager();
        final ArrayList<String> list = new ArrayList<String>();
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        for (final String package1 : array) {
            try {
                if ((0x1 & packageManager.getPackageInfo(package1, n).applicationInfo.flags) != 0x0) {
                    intent.setPackage(package1);
                    if (packageManager.queryIntentActivities(intent, 0).isEmpty()) {
                        list.add(package1);
                    }
                }
            }
            catch (PackageManager$NameNotFoundException ex) {}
        }
        final String[] array2 = new String[list.size()];
        list.toArray(array2);
        return array2;
    }

    private String[] getHeadlessSyncAdapterPackagesForAuthority(final String s) {
        return this.getHeadlessSyncAdapterPackages(ContentResolver.getSyncAdapterPackagesForAuthorityAsUser(s, BusinessLogicTestCase.getInstrumentation().getContext().getUserId()), 0);
    }

    private static String getInstallerPackageName(final Context context) throws Exception {
        final Intent intent = new Intent("android.intent.action.INSTALL_PACKAGE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(new File("foo.apk")), "application/vnd.android.package-archive");
        final List queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
        String packageName = null;
        for (final ResolveInfo resolveInfo : queryIntentActivities) {
            if ((0x1 & resolveInfo.activityInfo.applicationInfo.flags) == 0x0) {
                continue;
            }
            if (packageName != null) {
                Assert.fail("There must be one required installer");
            }
            packageName = resolveInfo.activityInfo.packageName;
        }
        if (packageName == null) {
            Assert.fail("There must be one required installer");
        }
        return packageName;
    }

    private static ArrayMap<String, PackageInfo> getMsdkTargetingPackagesUsingRuntimePerms(final List<PackageInfo> list, final Set<String> set) {
        final ArrayMap arrayMap = new ArrayMap();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final PackageInfo packageInfo = list.get(i);
            if (packageInfo.requestedPermissions != null) {
                if (packageInfo.applicationInfo.targetSdkVersion >= 23) {
                    final String[] requestedPermissions = packageInfo.requestedPermissions;
                    for (int length = requestedPermissions.length, j = 0; j < length; ++j) {
                        if (set.contains(requestedPermissions[j])) {
                            arrayMap.put((Object)packageInfo.packageName, (Object)packageInfo);
                            break;
                        }
                    }
                }
            }
        }
        return (ArrayMap<String, PackageInfo>)arrayMap;
    }

    private static Set<String> getRuntimePermissionNames(final List<PackageInfo> list) {
        final ArraySet set = new ArraySet();
        for (final PackageInfo packageInfo : list) {
            if (packageInfo.permissions == null) {
                continue;
            }
            for (final PermissionInfo permissionInfo : packageInfo.permissions) {
                if ((0x1 & permissionInfo.protectionLevel) != 0x0 && (0x40000000 & permissionInfo.flags) != 0x0 && permissionInfo.packageName.equals("android")) {
                    ((Set<String>)set).add(permissionInfo.name);
                }
            }
        }
        return (Set<String>)set;
    }

    private static String getVerifierPackageName(final Context context) throws Exception {
        final Intent intent = new Intent("android.intent.action.PACKAGE_NEEDS_VERIFICATION");
        intent.setType("application/vnd.android.package-archive");
        final List queryBroadcastReceivers = context.getPackageManager().queryBroadcastReceivers(intent, 512);
        String s = null;
        final Iterator<ResolveInfo> iterator = queryBroadcastReceivers.iterator();
        while (iterator.hasNext()) {
            final String packageName = iterator.next().activityInfo.packageName;
            if (context.getPackageManager().checkPermission("android.permission.PACKAGE_VERIFICATION_AGENT", packageName) != 0) {
                continue;
            }
            if (s != null) {
                throw new RuntimeException("There can be only one required verifier");
            }
            s = packageName;
        }
        return s;
    }

    private String getVoiceInteractionServicePackageName() {
        final String string = Settings$Secure.getString(BusinessLogicTestCase.getInstrumentation().getContext().getContentResolver(), "voice_interaction_service");
        if (TextUtils.isEmpty((CharSequence)string)) {
            return null;
        }
        final ComponentName unflattenFromString = ComponentName.unflattenFromString(string);
        if (unflattenFromString == null) {
            return null;
        }
        return unflattenFromString.getPackageName();
    }

    private boolean isSysComponentOrPersistentPlatformSignedPrivApp(final PackageInfo packageInfo) {
        return UserHandle.getAppId(packageInfo.applicationInfo.uid) < 10000 || (packageInfo.applicationInfo.isPrivilegedApp() && (0x8 & packageInfo.applicationInfo.flags) != 0x0 && BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager().checkSignatures(packageInfo.packageName, "android") == 0);
    }

    private void setPermissionGrantState(final String s, final String s2, final boolean b) {
        Label_0022: {
            if (!b) {
                break Label_0022;
            }
            try {
                BusinessLogicTestCase.getInstrumentation().getUiAutomation().grantRuntimePermission(s, s2, Process.myUserHandle());
                return;
                BusinessLogicTestCase.getInstrumentation().getUiAutomation().revokeRuntimePermission(s, s2, Process.myUserHandle());
            }
            catch (Exception ex) {}
        }
    }

    public void setException(final String s, final String s2, final String... array) {
        final HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
        for (final String s3 : array) {
            final String[] split = s3.trim().split("\\s+");
            if (split.length != 2) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unable to parse remote exception permission: ");
                sb.append(s3);
                Log.e("DefaultPermissionGrantPolicyTest", sb.toString());
                return;
            }
            hashMap.put(split[0], Boolean.valueOf(split[1]));
        }
        this.mRemoteExceptions.add(new DefaultPermissionGrantException(s, s2, hashMap));
    }

    public void testDefaultGrantsWithRemoteExceptions() throws Exception {
        List<PackageInfo> allPackages = this.getAllPackages();
        final Set<String> runtimePermissionNames = getRuntimePermissionNames(allPackages);
        final ArrayMap<String, PackageInfo> msdkTargetingPackagesUsingRuntimePerms = getMsdkTargetingPackagesUsingRuntimePerms(allPackages, runtimePermissionNames);
        final SparseArray sparseArray = new SparseArray();
        this.addSysComponentsAndPrivAppsDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addInstallerDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addVerifierDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addSetupWizardDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addCameraDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addMediaProviderDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addDownloadsProviderDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addDownloadsUiDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addStorageProviderDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addContainerDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addCertInstallerDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addDefaultDialerDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addSimCallManagerInstallerDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addDefaultSmsDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addDefaultUseOpenWifiDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addCellBroadcastReceiverDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addCarrierProvServiceDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addCalendarDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addCalendarProviderDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addCalendarProviderSyncAdaptersDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addContactsDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addContactsProviderSyncAdaptersDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addContactsProviderProviderDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addDeviceProvisioningDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addMapsDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addGalleryDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addEmailDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addBrowserDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addImeDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addVoiceInteractionDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addGlobalSearchDefaultPermissionsLowram((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addVoiceRecognitionDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addLocationDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addMusicDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addHomeDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addPrintSpoolerDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addCompanioonDeviceManagerDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addEmergencyInfoDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addCarrierAppsDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addImsServiceAppsDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addWearTwinningDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addWearFitnessDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addNfcTagAppDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addVrServiceDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addStorageManagerDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addPixelMigrateDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addRingtonePickerDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addSharedStorageBackupDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addTextClassifierDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, (SparseArray<UidState>)sparseArray);
        this.addExceptionsDefaultPermissions((Map<String, PackageInfo>)msdkTargetingPackagesUsingRuntimePerms, runtimePermissionNames, (SparseArray<UidState>)sparseArray);
        final StringBuilder sb = new StringBuilder();
        final PackageManager packageManager = BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager();
        Iterator<PackageInfo> iterator = msdkTargetingPackagesUsingRuntimePerms.values().iterator();
        while (iterator.hasNext()) {
            final PackageInfo packageInfo = iterator.next();
            final UidState uidState = (UidState)sparseArray.get(packageInfo.applicationInfo.uid);
            if (uidState == null) {
                continue;
            }
            List<PackageInfo> list;
            Iterator<PackageInfo> iterator2;
            for (int size = uidState.grantedPermissions.size(), i = 0; i < size; ++i, allPackages = list, iterator = iterator2) {
                final String s = (String)uidState.grantedPermissions.keyAt(i);
                if (ArrayUtils.contains((Object[])packageInfo.requestedPermissions, (Object)s) && packageManager.checkPermission(s, packageInfo.packageName) == 0) {
                    boolean b = false;
                    if (s.equals("android.permission.ACCESS_COARSE_LOCATION")) {
                        list = allPackages;
                        if (packageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION", packageInfo.packageName) == 0) {
                            final String packageName = packageInfo.packageName;
                            iterator2 = iterator;
                            this.setPermissionGrantState(packageName, "android.permission.ACCESS_FINE_LOCATION", false);
                            b = true;
                        }
                        else {
                            iterator2 = iterator;
                            b = false;
                        }
                    }
                    else {
                        list = allPackages;
                        iterator2 = iterator;
                    }
                    this.setPermissionGrantState(packageInfo.packageName, s, false);
                    if (!(boolean)uidState.grantedPermissions.valueAt(i) && packageManager.checkPermission(s, packageInfo.packageName) == 0) {
                        sb.append("Permission:");
                        sb.append(s);
                        sb.append(" grated by default to package:");
                        sb.append(packageInfo.packageName);
                        sb.append(" should be revocable");
                        sb.append('\n');
                    }
                    this.setPermissionGrantState(packageInfo.packageName, s, true);
                    if (b) {
                        this.setPermissionGrantState(packageInfo.packageName, "android.permission.ACCESS_FINE_LOCATION", true);
                    }
                    packageInfo.requestedPermissions = ArrayUtils.removeString(packageInfo.requestedPermissions, s);
                }
                else {
                    list = allPackages;
                    iterator2 = iterator;
                }
            }
        }
        for (final PackageInfo packageInfo2 : msdkTargetingPackagesUsingRuntimePerms.values()) {
            final int uid = packageInfo2.applicationInfo.uid;
            for (final String s2 : packageInfo2.requestedPermissions) {
                final UidState uidState2 = (UidState)sparseArray.get(uid);
                if (uidState2 == null || uidState2.grantedPermissions.indexOfKey((Object)s2) < 0) {
                    if (runtimePermissionNames.contains(s2) && packageManager.checkPermission(s2, packageInfo2.packageName) == 0) {
                        sb.append("Permission:");
                        sb.append(s2);
                        sb.append(" cannot be granted by default to package:");
                        sb.append(packageInfo2.packageName);
                        sb.append('\n');
                    }
                }
            }
        }
        if (sb.length() > 0) {
            Assert.fail(sb.toString());
        }
    }

    public void testLowRamVerifyDisabled() throws Exception {
        if (!BusinessLogicTestCase.getInstrumentation().getContext().getPackageManager().hasSystemFeature("android.hardware.ram.low")) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        final String voiceInteractionServicePackageName = this.getVoiceInteractionServicePackageName();
        if (voiceInteractionServicePackageName != null) {
            sb.append("voiceInteractPackageName set to ");
            sb.append(voiceInteractionServicePackageName);
            sb.append(" but should be disabled on low-ram devices\n");
        }
        if (sb.length() > 0) {
            Assert.fail(sb.toString());
        }
    }

    private static class DefaultPermissionGrantException
    {
        public boolean hasBrand;
        public Map<String, Boolean> permissions;
        public String pkg;
        public String sha256;

        DefaultPermissionGrantException(final String pkg, final String sha256, final Map<String, Boolean> permissions) {
            this.permissions = new HashMap<String, Boolean>();
            this.pkg = pkg;
            this.sha256 = sha256;
            if (!sha256.contains(":")) {
                this.hasBrand = true;
            }
            this.permissions = permissions;
        }
    }

    private static class UidState
    {
        private ArrayMap<String, Boolean> grantedPermissions;

        private UidState() {
            this.grantedPermissions = (ArrayMap<String, Boolean>)new ArrayMap();
        }

        public void addGrantedPermission(final String s, final Boolean b) {
            final Boolean b2 = (Boolean)this.grantedPermissions.get((Object)s);
            if (b2 == null || (b2 == Boolean.TRUE && b == Boolean.FALSE)) {
                this.grantedPermissions.put((Object)s, (Object)b);
            }
        }
    }*/
}
