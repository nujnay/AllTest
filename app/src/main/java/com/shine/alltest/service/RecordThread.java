package com.shine.alltest.service;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class RecordThread extends Thread {
    private static final String TAG = "RecordThread";
    private AudioRecord ar;
    private int bs;
    private boolean isRun = false;

    public RecordThread() {
        super();
        int SAMPLE_RATE_IN_HZ = 8000;
        bs = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT);
        ar = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, bs);
    }

    public void run() {
        super.run();
        ar.startRecording();
        // 用于读取的 buffer
        byte[] buffer = new byte[bs];
        isRun = true;
        while (isRun) {
            int r = ar.read(buffer, 0, bs);
            int v = 0;
            // 将 buffer 内容取出，进行平方和运算
            for (byte aBuffer : buffer) {
                // 这里没有做运算的优化，为了更加清晰的展示代码
                v += aBuffer * aBuffer;
            }
            // 平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
            // 如果想利用这个数值进行操作，建议用 sendMessage 将其抛出，在 Handler 里进行处理。
            Log.d("volume", String.valueOf(v / (float) r));
        }
        ar.stop();
    }

    public void startRecording() {
        // 在调用本线程的 Activity 的 onResume 里调用，以便 Activity 恢复后继续获取麦克风输入音量
        run();
    }

    public void stopRecording() {
        isRun = false;
    }
}