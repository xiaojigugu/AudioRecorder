#include <jni.h>
#include <string>
#include <android/log.h>
#include "Mp3Encoder.h"

JavaVM *javaVM = 0;

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_junt_recorder_core_AudioRecorder_convertNative(JNIEnv *env, jobject thiz, jstring in_Path,
                                                        jstring out_path) {
    const char *in = env->GetStringUTFChars(in_Path, 0);
    const char *out = env->GetStringUTFChars(out_path, 0);
    Mp3Encoder *mp3Encoder = new Mp3Encoder(javaVM, env, thiz);
    mp3Encoder->encode(const_cast<char *>(in), const_cast<char *>(out));

    env->ReleaseStringUTFChars(in_Path, in);
    env->ReleaseStringUTFChars(out_path, out);
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_junt_recorder_core_AudioRecorder_getLameVersion(JNIEnv *env, jobject thiz) {
    return env->NewStringUTF(get_lame_version());
}