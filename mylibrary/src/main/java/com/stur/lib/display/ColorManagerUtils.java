package com.stur.lib.display;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.qti.snapdragon.sdk.display.ColorManager;
import com.qti.snapdragon.sdk.display.ColorManager.ColorManagerListener;
import com.qti.snapdragon.sdk.display.ColorManager.DCM_DISPLAY_TYPE;
import com.qti.snapdragon.sdk.display.ColorManager.DCM_FEATURE;
import com.qti.snapdragon.sdk.display.ColorManager.MODE_TYPE;
import com.qti.snapdragon.sdk.display.ModeInfo;
import com.qti.snapdragon.sdk.display.PictureAdjustmentConfig;
import com.stur.lib.Log;

import java.util.ArrayList;
import java.util.EnumSet;

import static io.ganguo.app.gcache.disk.DiskBasedCache.TAG;

public class ColorManagerUtils {
    public static final int CE_BLACKWHITE = 0;
    public static final int CE_STANDARD = 1;
    public static final int CE_BRIGHTCOLOR = 2;
    public static final int CE_USERDEFINE = 1000;

    public ArrayList<ModeInfoWrapper> modeList;
    public static ColorManager cMgr;
    private int defaultModeID = -1;
    public ModeInfoWrapper activeModeWrapper = null;
    private Context mContext;

    public static final int mDefaultSaturation = 75;// 150;
    public static final int mDefaultContrast = 100;// 200;
    private int pasatthresh = 0;
    private int pahue = 0;
    private int pasaturation = 0;
    private int paintensity = 0;
    private int pacontrast = 0;
    public static final String KEY_SATURATION = "saturation";
    public static final String KEY_CONTRAST = "contrast";

    public ColorManagerUtils(Context context) {
        mContext = context;
        initColorManager();
    }

    public void initColorManager() {
        try {
            ColorManagerListener colorinterface = new ColorManagerListener() {
                @Override
                public void onConnected() {
                    Log.d(this, "onConnected");
                    getColorManagerInstance();
                }
            };
            int retVal = ColorManager.connect(mContext, colorinterface);
            if (retVal != ColorManager.RET_SUCCESS) {
                Log.e(this, "ColorManager connect failue !");
            }
        } catch (Exception ex) {
            Log.e(this, "init ColorManager failue !" + ex.toString());
        }
    }

    private void getColorManagerInstance() {
        Log.d(this, "Display ColorManager registered..");
        if (cMgr == null) {
            cMgr = ColorManager.getInstance(
                    (Application) mContext.getApplicationContext(), mContext,
                    DCM_DISPLAY_TYPE.DISP_PRIMARY);
            if (cMgr == null) {
                throw new RuntimeException(
                        "Failed to get ColorManager instance.");
            }
        }

        if (cMgr != null) {
            boolean isSupported = cMgr
                    .isFeatureSupported(DCM_FEATURE.FEATURE_COLOR_MODE_SELECTION);
            if (isSupported) {
                boolean populated = populateModesOnScreen();
                if (!populated) {
                    Log.d(TAG, "populateModesOnScreen is false.");
                }
            } else {
                Log.d(TAG, "the device is not support color mode selection.");
            }
        }
    }

    public boolean populateModesOnScreen() {
        ModeInfo[] modeDataArray = null;
        if (cMgr == null) {
            Log.e(TAG, "populateModesOnScreen(): Display SDK manager is null!");
            return false;
        }
        modeDataArray = cMgr.getModes(MODE_TYPE.MODE_ALL);
        activeModeWrapper = null;

        // Clear the current list of modes from the UI
        if (modeDataArray != null) {
            createModeList(modeDataArray);
            int[] activeMode = cMgr.getActiveMode();
            Log.d(TAG, "Currently Active modeID is " + activeMode[0]);
            defaultModeID = cMgr.getDefaultMode();
            Log.d(TAG, "Default mode is " + defaultModeID);
            for (ModeInfoWrapper mode : modeList) {

                //whether has extra user defined mode "test"
                if (mode.modename.equals("test")) {
                    activeModeWrapper = mode;
                    return true;
                }
            }

        }
        return false;
    }

    private void createModeList(ModeInfo[] pa) {
        modeList = new ArrayList<ModeInfoWrapper>();
        for (ModeInfo i : pa)
            modeList.add(new ModeInfoWrapper(i));
    }

    public void setDefaultMode(int mode) {
        int retValue = -1;
        if (null != cMgr) {
            if (modeList != null && modeList.size() > 0) {
                retValue = cMgr.setDefaultMode(modeList.get(mode).modeID);
                if (retValue < 0) {
                    Log.d(TAG, "set the default mode is failed. ");
                }
            }
        } else {
            Log.d(TAG, "setDefaultMode:the cMgr is null.");
        }
    }

    //for user defined mode
    public void setDefaultMode() {
        int retValue = -1;
        if (null != cMgr) {
            ModeInfoWrapper aMode = activeModeWrapper;
            if (null != aMode) {
                Log.d(TAG, "set the " + aMode.modename + " is default mode");
                cMgr.setActiveMode(aMode.modeID);
                retValue = cMgr.setDefaultMode(aMode.modeID);
                if (retValue < 0) {
                    Log.d(TAG, "set the " + aMode.modename + " is failed.");
                } else {
                    defaultModeID = cMgr.getDefaultMode();
                    boolean populated = populateModesOnScreen();
                    if (!populated) {
                        Log.d(TAG, "setDefaultMode:populated=false");
                    }
                }

            } else {
                Log.d(TAG, "the mode value is false.");
                return;
            }

        } else {
            Log.d(TAG, "the cMgr is null.");
        }
    }

    public void setDisplayMode(int id) {
        if (null != cMgr) {
            if (modeList != null && modeList.size() > 0) {
                int retVal = cMgr.setActiveMode(modeList.get(id).modeID);
                Log.e(TAG, "retVal=" + retVal);
                if (retVal >= 0) {
                    populateModesOnScreen();
                } else {
                    Log.d(TAG, "set the id = " + id + " mode failed.");
                }
            }
        }
    }

    public void activateDefineMode() {
        setDisplayMode(1);
        initLocalVariables();
        setPaDisplayParams();
        boolean isExict = populateModesOnScreen();
        if (isExict) {
            int modeId = activeModeWrapper.modeID;
            createMode(modeId, null);
        } else {
            createMode(-1, null);
            populateModesOnScreen();
        }
        setDefaultMode();
    }

    public void initLocalVariables() {
        if (cMgr != null) {
            PictureAdjustmentConfig paValues = cMgr
                    .getPictureAdjustmentParams();
            SharedPreferences sharedPrefs = mContext.getSharedPreferences(
                    "com.android.settings_preferences", 0);
            pasaturation = sharedPrefs.getInt(KEY_SATURATION,
                    mDefaultSaturation);
            pacontrast = sharedPrefs.getInt(KEY_CONTRAST, mDefaultContrast);
            if (null != paValues) {
                pahue = paValues.getHue();
                paintensity = paValues.getIntensity();
                pasatthresh = paValues.getSaturationThreshold();
                // pasaturation = paValues.getSaturation();
                // pacontrast = paValues.getContrast();
            } else {
                Log.e(TAG,
                        "getPictureAdjustmentParams returned null during init");
            }
        }
    }

    public void setPaDisplayParams() {
        PictureAdjustmentConfig newPaConfig = new PictureAdjustmentConfig(
                EnumSet.allOf(PictureAdjustmentConfig.PICTURE_ADJUSTMENT_PARAMS.class),
                pahue, pasaturation, paintensity, pacontrast, pasatthresh);
        cMgr.setPictureAdjustmentParams(newPaConfig);
    }

    //for user defined mode
    public void createMode(int modeID, String modeName) {
        if (-1 == modeID) {
            int retVal = cMgr.createNewMode("test");
            if (retVal < 0) {
                Log.d(TAG, "create the test modeid is failed.");
            }
        } else {
            /* yulong begin, add */
            /* the modifyMode api is not effect, so fisrt deleteMode, then createNewMode(xiejinyang), yanglun, 2016.03.17 */
            int value = cMgr.deleteMode(activeModeWrapper.modeID);
            if(value < 0){
                Log.d(TAG, "delete the test mode is failed.");
            }
            //int retVal = cMgr.modifyMode(modeID, "test");
            int retVal = cMgr.createNewMode("test");
            if (retVal < 0) {
                Log.d(TAG, "modify the test mode is failed.");
            }
            /* yulong end */
        }
    }

    public class ModeInfoWrapper {
        public ModeInfo mode;
        public String modename;
        public int modeID;

        ModeInfoWrapper(ModeInfo displayMode) {
            mode = displayMode;
            modename = displayMode.getName();
            modeID = displayMode.getId();
        }

        @Override
        public String toString() {
            return modename;
        }

        public void resetName() {
            modename = mode.getName();
        }
    }
}
