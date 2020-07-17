package com.junt.recorder.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.junt.recorder.AudioRecorderManager;
import com.junt.recorder.core.AudioRecorder;
import com.junt.recorder.core.OnRecordListener;
import com.junt.recorder.R;
import com.junt.recorder.utils.FftFactory;

import java.io.File;
public class RecordActivity extends AppCompatActivity {

    private final String TAG = "Audio_RecordActivity";

    private AudioRecorder audioRecorder;
    private AudioView audioView;
    private FftFactory fftFactory;
    private long downTime;
    private boolean isRecordInvalid;

    private String outDir;
    private int max_time_ms;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        init();

        final Button btnRecord = findViewById(R.id.btnRecord);

        btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showRecordView(event, btnRecord);
                return true;
            }
        });
    }

    private void showRecordView(MotionEvent event, Button btnRecord) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            btnRecord.setText("松开停止");
            isRecordInvalid = true;
            downTime = System.currentTimeMillis();
            btnRecord.setPressed(true);
            audioRecorder.startRecording(outDir, max_time_ms);
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                || event.getAction() == MotionEvent.ACTION_UP) {
            btnRecord.setText("按下录音");
            if (System.currentTimeMillis() - downTime <= 2_000) {
                isRecordInvalid = false;
            }
            btnRecord.setPressed(false);
            audioRecorder.stop();
        }
    }

    private void init() {

        String out_dir = getIntent().getStringExtra("out_dir");
        outDir = TextUtils.isEmpty(out_dir) ? outDir : out_dir;

        int max_time = getIntent().getIntExtra("max_time", 10_000);
        max_time_ms = max_time == 0 ? 10_000 : max_time;

        fftFactory = new FftFactory(FftFactory.Level.Original);
        audioView = findViewById(R.id.audioView);
        audioView.setStyle(AudioView.ShowStyle.STYLE_ALL, AudioView.ShowStyle.STYLE_ALL);

        OnRecordListener listener = new OnRecordListener() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: ");
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onRecording(byte[] data) {
                byte[] fftData = fftFactory.makeFftData(data);
                audioView.setWaveData(fftData);
            }

            @Override
            public void onConverting(int progress) {
                Log.i(TAG, "onConverting: " + Thread.currentThread().getName() + "->" + progress);
            }

            @Override
            public void onComplete(final String filePath) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "onComplete: " + filePath);
                        audioView.setWaveData(null);
                        Intent intent = new Intent();
                        intent.putExtra("result", filePath);
                        setResult(isRecordInvalid ? RESULT_OK : RESULT_CANCELED, intent);
                        finish();
                    }
                });
            }
        };
        audioRecorder = new AudioRecorder(listener);
    }
}
