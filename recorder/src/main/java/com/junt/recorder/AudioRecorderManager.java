package com.junt.recorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.junt.recorder.view.RecordActivity;

import java.io.File;

import androidx.annotation.Nullable;

public class AudioRecorderManager {

    public static final int REQUEST_CODE = 99;

    /**
     * 跳转录制页面
     *
     * @param context     activity
     * @param outDir      输出路径，不包含文件名
     * @param max_time_ms 最大录制时长
     */
    public static void record(Activity context, String outDir, int max_time_ms) {
        Intent intent = new Intent(context, RecordActivity.class);
        intent.putExtra("out_dir", outDir);
        intent.putExtra("max_time", max_time_ms);
        context.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * 跳转录制页面
     *
     * @param context activity
     */
    public static void record(Activity context) {
        Intent intent = new Intent(context, RecordActivity.class);
        intent.putExtra("out_dir", context.getFilesDir() + File.separator + "audio_record");
        intent.putExtra("max_time", 6_000);
        context.startActivityForResult(intent, REQUEST_CODE);
    }

    public static String obtainResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            return data.getStringExtra("result");
        }
        return "";
    }
}
