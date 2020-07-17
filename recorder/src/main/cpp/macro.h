//
// Created by Administrator on 2020/7/16.
//

#ifndef AUDIORECORDER_MACRO_H
#define AUDIORECORDER_MACRO_H

#define DELETE(object) if(object){delete object;object=0;}

//自定义日志打印
#define  LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"Mp3Encoder",__VA_ARGS__)

//自定义日志打印
#define  LOGI(...) __android_log_print(ANDROID_LOG_INFO,"Mp3Encoder",__VA_ARGS__)

#endif //AUDIORECORDER_MACRO_H
