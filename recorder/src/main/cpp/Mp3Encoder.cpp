//
// Created by Administrator on 2020/7/16.
//

#include "Mp3Encoder.h"

pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

Mp3Encoder::Mp3Encoder(JavaVM *javaVm, JNIEnv *env, jobject pJobject) {
    LOGE("lame_version:%s", get_lame_version());
    this->javaCallHelper = new JavaCallHelper(javaVm, env, pJobject);
}

/**
 * 进行转码
 * @param mp3Encoder
 */
void encode_(Mp3Encoder *mp3Encoder) {
    pthread_mutex_lock(&mutex);
    FILE *file_pcm = fopen(mp3Encoder->in_path, "rb");
    FILE *file_mp3 = fopen(mp3Encoder->out_path, "wb");

    short int pcm_buffer[8192 * 2];
    unsigned char mp3_buffer[8192];

    //1.初始化lame的编码器
    lame_t lame = lame_init();
    //2. 设置lame mp3编码的采样率
    lame_set_in_samplerate(lame, 44100);
    lame_set_num_channels(lame, 2);
    // 3. 设置MP3的编码方式
    lame_set_VBR(lame, vbr_default);
    lame_init_params(lame);
    LOGI("lame init finish");
    int read;
    int write; //代表读了多少个次 和写了多少次
    int total = 0; // 当前读的wav文件的byte数目
    do {
        read = fread(pcm_buffer, sizeof(short int) * 2, 8192, file_pcm);
        total += read * sizeof(short int) * 2;
        LOGI("converting ....%d", total);
        mp3Encoder->getJavaCallHelper()->onConverting(total);
        // 调用java代码 完成进度条的更新
        if (read != 0) {
            write = lame_encode_buffer_interleaved(lame, pcm_buffer, read, mp3_buffer, 8192);
            //把转化后的mp3数据写到文件里
            fwrite(mp3_buffer, sizeof(unsigned char), write, file_mp3);
        }
        if (read == 0) {
            lame_encode_flush(lame, mp3_buffer, 8192);
        }
    } while (read != 0);
    lame_close(lame);
    fclose(file_pcm);
    fclose(file_mp3);
    LOGI("convert  finish");
    pthread_mutex_unlock(&mutex);
}

/**
 * 开启线程执行转码
 * @param agrs 当前类对象
 * @return  0
 */
void *task_encode(void *agrs) {
    Mp3Encoder *mp3Encoder = static_cast<Mp3Encoder *>(agrs);
    encode_(mp3Encoder);
    return 0;
}

/**
 *
 * @param inPath
 * @param outPath
 */
void Mp3Encoder::encode(char *inPath, char *outPath) {
    this->in_path = inPath;
    this->out_path = outPath;
    pthread_create(&pid_encode, 0, task_encode, this);
    pthread_join(pid_encode, 0);
    LOGI("convert  finish -> call java method");
    javaCallHelper->onComplete(outPath);
}

/**
 * 析构函数
 */
Mp3Encoder::~Mp3Encoder() {
    DELETE(in_path);
    DELETE(out_path);
    DELETE(javaCallHelper);
}