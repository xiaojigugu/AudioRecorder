package com.junt.recorder.core;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class AudioRecorder {

    static {
        System.loadLibrary("native-lib");
    }

    public static final int SAMPLE_RATE = 44100;
    private final int CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
    private final int ENCODING_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private OnRecordListener listener;

    private RecordTask recordTask;

    public AudioRecorder(OnRecordListener listener) {
        this.listener = listener;
    }


    public void startRecording(String outDir, int max_duration_ms) {
        if (recordTask != null && recordTask.isRecording) {
            return;
        }
        initTask(outDir, max_duration_ms);
        recordTask.execute();
    }

    private void initTask(String outDir, int max_duration_ms) {
        if (recordTask != null) {
            recordTask.cancel(true);
        }
        File file = new File(outDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        recordTask = new RecordTask(outDir, max_duration_ms);
    }

    public boolean isRecording() {
        return recordTask != null && recordTask.isRecording;
    }

    public void stop() {
        recordTask.stop();
    }

    public void pause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recordTask.pause();
        }
    }

    public void resume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recordTask.resume();
        }
    }


    class RecordTask extends AsyncTask<Object, Integer, Boolean> {

        private AudioRecord recorder;
        private boolean isRecording = false, isPause = false;
        private File pcmFile;
        private final int minBufferSize;
        private int max_duration_ms;
        private Handler handler;

        public RecordTask(String outPutPath, int max_duration_ms) {
            minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING_FORMAT);
            recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                    SAMPLE_RATE,
                    CHANNEL,
                    ENCODING_FORMAT,
                    minBufferSize);
            pcmFile = new File(outPutPath, "record.pcm");
            this.max_duration_ms = max_duration_ms;
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                recorder.startRecording();//开始录制
                FileOutputStream fileOutputStream;
                try {
                    fileOutputStream = new FileOutputStream(pcmFile);
                    byte[] bytes = new byte[minBufferSize];

                    while (isRecording) {
                        if (isPause) {
                            //暂停
                            continue;
                        }
                        recorder.read(bytes, 0, bytes.length);//读取流
                        if (listener != null) {
                            listener.onRecording(bytes);
                        }
                        fileOutputStream.write(bytes);
                    }
                    recorder.stop();//停止录制
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    //添加音频头部信息并且转成wav格式
                    return true;
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRecording = true;
            isPause = false;
            if (listener != null) {
                listener.onStart();
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stop();
                }
            }, max_duration_ms);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                pcmToMp3();
            }
        }

        public void stop() {
            isRecording = false;
        }

        public void pause() {
            isPause = true;
        }

        public void resume() {
            isPause = false;
        }

        private void pcmToMp3() {
            String mp3Path = pcmFile.getParent() + File.separator + "record.mp3";
            //调用native进行转码
            convertNative(pcmFile.getAbsolutePath(), mp3Path);
        }
    }

    /**
     * 供native调用
     *
     * @param progress 转码进度
     */
    public void onConverting(int progress) {
        if (listener != null) {
            listener.onConverting(progress);
        }
    }

    /**
     * 供native调用

     */
    public void onComplete(String mp3Path) {
        if (listener != null) {
            listener.onComplete(mp3Path);
        }
    }

    /**
     * 将PCM转为mp3 native
     *
     * @param inputFilePath  pcm文件绝对路径
     * @param outPutFilePath 输出mp3文件绝对路径
     */
    private native void convertNative(String inputFilePath, String outPutFilePath);
}
