LOCAL_PATH := $(call my-dir)/src/main/cpp

include $(CLEAR_VARS)

LOCAL_MODULE := triedictionary-lib
LOCAL_SRC_FILES := triedictionary-lib.cpp

include $(BUILD_SHARED_LIBRARY)
