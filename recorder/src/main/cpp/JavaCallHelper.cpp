//
// Created by Administrator on 2020/7/17.
//

#include "JavaCallHelper.h"

JavaCallHelper::JavaCallHelper(JavaVM *javaVm, JNIEnv *env, jobject instance){

    this->javaVM = javaVm;
    this->env = env;

    this->instance = env->NewGlobalRef(instance);

    jclass jclazz = env->GetObjectClass(instance);
    jvm_onConverting = env->GetMethodID(jclazz, "onConverting", "(I)V");
    jvm_onComplete = env->GetMethodID(jclazz, "onComplete", "(Ljava/lang/String;)V");
}

JavaCallHelper::~JavaCallHelper() {
    javaVM = 0;
    env->DeleteGlobalRef(instance);
    instance = 0;
}

void JavaCallHelper::onComplete(char *path) {
    jstring str=env->NewStringUTF(path);
    env->CallVoidMethod(instance, jvm_onComplete,str);
}

void JavaCallHelper::onConverting(int progress) {
    JNIEnv *env_child;
    javaVM->AttachCurrentThread(&env_child, NULL);
    env_child->CallVoidMethod(instance, jvm_onConverting, progress);
    javaVM->DetachCurrentThread();
}
