LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

TARGET_PLATFORM := android-3
LOCAL_MODULE    := heapdumptest
LOCAL_SRC_FILES := heapdumptest.c
LOCAL_LDLIBS    := -llog

include $(BUILD_EXECUTABLE)

