package com.stur.lib.constant;

import android.content.Context;

import com.stur.lib.SystemPropertiesProxy;

public class StConstant {
    public static final boolean TEST_TAG = true;

    /* UI */
    public static final String FILE_NAME="splash";
    public static final int APP_LIST_GRIDE_ROW = 2;
    public static final int APP_LIST_GRIDE_COLUMN = 4;
    public static final int MAX_APPS_PER_PAGE = APP_LIST_GRIDE_ROW * APP_LIST_GRIDE_COLUMN;
    public static final int PICTURE_LIST_GRIDE_COLUMN = 4;

    /* Communication */
    public static final int MAC_ADDR_LENGTH = 12;
    public static final String MAC_ADDR_HOME_PC = "408D5CC1DB5B";
    public static final String DEFAULT_SERVER = "10.66.128.27";
    public static final int DEFAULT_PORT = 6666;
    public static final int BUFSIZE = 128;
    public static final int SIM_ID_1 = 0;
    public static final int SIM_ID_2 = 1;

    /* Web Server */
    public static final int DEFAULT_WEB_SERVER_PORT = 8088;
    public static final String CONTROLLING_APK = "Controlling.apk";
    public static final String QRCODE_CONTENT = DEFAULT_SERVER + ":" + DEFAULT_WEB_SERVER_PORT;

    /* MultiMedia */

    /* File */
    public static final String FILE_PROVIDER_AUTH = "com.stur.chest.fileprovider";

    public static final String ROLE_DEFINITION = "persist.stur.role_definition";
    public static final String ROLE_SERVER     = "server";
    public static final String ROLE_CLIENT     = "client";
    public static final String ROLE_NONE    = "";
    public static String sRoleDef = "";

    public static final String SP_FILE_KEY_CONTROLLING_DATA = "controlling_data";
    public static final String SP_VALUE_KEY_RUN_MODE        = "runmode";
    /*public static final String SP_RUN_MODE_UNKNOWN        = "unknown";
    public static final String SP_RUN_MODE_STA              = "STA";
    public static final String SP_RUN_MODE_P2P              = "P2P";*/
    public static final String SP_FILE_KEY_CLIENT_LIST      = "client_list";
    public static final String SP_VALUE_KEY_CLIENT_LIST     = "client_list";

    /*SystemProperty*/
    static final String PROPERTY_OPERATORS_MODE = "persist.yulong.operators.mode";

    public static final String WIFI_P2P_PREFIX_DEVICE_NAME = "IVVI P1 ";

    //Wifi run mode
    public static final int WIFI_RM_UNKNOWN     = 0;
    public static final int WIFI_RM_STA         = 1;
    public static final int WIFI_RM_P2P         = 2;
    //public static final int WIFI_RM_STA_P2P     = 3;


    public static final int REQUEST_CODE_WIFI_SCAN = 1;

    public static void setRoleDefinition(String str) {
        sRoleDef = str;
    }

    public static String getRoleDefinition() {
        return sRoleDef;
    }

    public static boolean isRoleServer() {
        return ROLE_SERVER.equals(sRoleDef);
    }

    public static boolean isRoleClient() {
        return ROLE_CLIENT.equals(sRoleDef);
    }

    public static boolean isRoleServer(Context context) {
        return  ROLE_SERVER.equals(SystemPropertiesProxy.get(context, ROLE_DEFINITION));
    }

    public static boolean isRoleClient(Context context) {
        return  ROLE_CLIENT.equals(SystemPropertiesProxy.get(context, ROLE_DEFINITION));
    }

    public static boolean isRoleNone(Context context) {
        return  ROLE_NONE.equals(SystemPropertiesProxy.get(context, ROLE_DEFINITION));
    }

    //写这个属性只有rom软件有权限
    public static void setRoleDefinition(Context contex, String role) {
        SystemPropertiesProxy.set(contex, ROLE_DEFINITION, role);
    }

}
