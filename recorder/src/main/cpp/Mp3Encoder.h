//
// Created by Administrator on 2020/7/16.
//

#ifndef AUDIORECORDER_MP3ENCODER_H
#define AUDIORECORDER_MP3ENCODER_H

#include "pthread.h"
#include "macro.h"
#include "JavaCallHelper.h"
#include "jni.h"
#include "include/lame.h"
#include <android/log.h>

class Mp3Encoder {
private:
    pthread_t pid_encode;
    JavaCallHelper *javaCallHelper = 0;
public:
    char *in_path;
    char *out_path;

    Mp3Encoder(JavaVM *javaVm, JNIEnv *env, jobject pJobject);

    JavaCallHelper* getJavaCallHelper(){
        return javaCallHelper;
    };

    virtual ~Mp3Encoder();

    void encode(char *inPath, char *outPath);
};


#endif //AUDIORECORDER_MP3ENCODER_H
