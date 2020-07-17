//
// Created by Administrator on 2020/7/17.
//

#ifndef AUDIORECORDER_JAVACALLHELPER_H
#define AUDIORECORDER_JAVACALLHELPER_H


#include <jni.h>

class JavaCallHelper {
private:
    JavaVM *javaVM;
    JNIEnv *env;
    jobject instance;
    jmethodID jvm_onConverting,jvm_onComplete;

public:
    JavaCallHelper(JavaVM *javaVm, JNIEnv *env, jobject instance);

    ~JavaCallHelper();

    void onConverting(int progress);

    void onComplete(char *path);
};


#endif //AUDIORECORDER_JAVACALLHELPER_H
