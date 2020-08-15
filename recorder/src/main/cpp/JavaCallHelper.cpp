//
// Created by Administrator on 2020/7/17.
//

#include "JavaCallHelper.h"

JavaCallHelper::JavaCallHelper(JavaVM *javaVm, JNIEnv *env, jobject instance) {

    this->javaVM = javaVm;
    this->env = env;

    this->instance = env->NewGlobalRef(instance);

    jclass jclazz = env->GetObjectClass(instance);
    jvm_onConverting = env->GetMethodID(jclazz, "onConverting", "(I)V");
    jvm_onComplete = env->GetMethodID(jclazz, "onComplete", "(Ljava/lang/String;)V");
}

JavaCallHelper::~JavaCallHelper() {
    javaVM = nullptr;
    env->DeleteGlobalRef(instance);
    instance = nullptr;
}

void JavaCallHelper::onComplete(char *path) {
    JNIEnv *env_child;
    javaVM->AttachCurrentThread(&env_child, nullptr);
    jstring str = env_child->NewStringUTF(path);
    env_child->CallVoidMethod(instance, jvm_onComplete, str);
    env_child->ReleaseStringUTFChars(str,path);
    javaVM->DetachCurrentThread();
}

void JavaCallHelper::onConverting(int progress) {
    JNIEnv *env_child;
    javaVM->AttachCurrentThread(&env_child, nullptr);
    env_child->CallVoidMethod(instance, jvm_onConverting, progress);
    javaVM->DetachCurrentThread();
}
