package com.junt.recorder.core;

public interface OnRecordListener {

    void onStart();

    void onError(Exception e);

    void onRecording(byte[] data);

    void onConverting(int progress);

    void onComplete(String filePath);
}
