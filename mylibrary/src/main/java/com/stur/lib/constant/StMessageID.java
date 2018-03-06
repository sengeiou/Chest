package com.stur.lib.constant;

/**
 * Created by Administrator on 2016/3/2.
 */
public class StMessageID {
    /* for ControlledService, ControllingAsyncChannel and ControlledAsyncChannel EVENT*/
    public static final int REQ_IS_IDLE                   = 0;
    public static final int RSP_IS_IDLE                   = 1;
    public static final int EVENT_P2P_CONNECTED          = 2;
    public static final int EVENT_P2P_DISCONNECTED       = 3;
    public static final int EVENT_AP_CONNECTED           = 4;
    public static final int EVENT_AP_DISCONNECTED        = 5;
    public static final int EVENT_HEARDBEATING_RCVED     = 6;
    public static final int EVENT_HEARDBEATING_LOST      = 7;
    public static final int EVENT_RECOVERED               = 8;
    public static final int EVENT_SELECT_RUN_MODE_DONE   = 9;
    public static final int EVENT_AP_CONFIG_RCVED         = 10;
    /* WiFiP2PAdmin */
    public static final int EVENT_P2P_PEER_SELECT         = 11;
    /* ControlledService Message */
    public static final int EVENT_IND_EXAMPLE             = 12;
    public static final int EVENT_TEST                     = 13;

    public static boolean isStateMachineEvent(int event) {
        if(event == EVENT_P2P_CONNECTED ||
                event == EVENT_AP_CONNECTED ||
                event == EVENT_P2P_DISCONNECTED ||
                event == EVENT_AP_DISCONNECTED ||
                event == EVENT_AP_CONFIG_RCVED) {
            return true;
        }
        return false;
    }


    public static final int EVENT_TEXT_PRINT = 10000;
}
