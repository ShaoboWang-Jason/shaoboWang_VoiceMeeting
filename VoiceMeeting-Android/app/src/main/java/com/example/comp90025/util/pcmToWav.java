//Author: Shaobo Wang
//shaobow@student.unimelb.edu.au

package com.example.comp90025.util;

import android.media.AudioFormat;
import android.media.AudioRecord;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class pcmToWav {
    private int buffer;  //audio size
    private int sample = 44100;// 44100
    private int channel = AudioFormat.CHANNEL_IN_STEREO;   //立体声
    private int CODE = AudioFormat.ENCODING_PCM_16BIT;    // 16bit

    public pcmToWav() {
        this.buffer = AudioRecord.getMinBufferSize(sample, channel, CODE);
    }

    /**
     * @param r     rate
     * @param n    channel
     * @param m    format
     */
    public pcmToWav(int r, int n, int m) {
        this.sample = r;
        this.channel = n;
        this.CODE = m;
        this.buffer = AudioRecord.getMinBufferSize(sample, channel, CODE);
    }

    /**
     * pcm change to wav format
     *
     * @param filePath  pcm file path
     * @param outPath   wav file path should be same as pcm file path
     */
    public void change(String filePath, String outPath) {
        FileInputStream in;
        FileOutputStream out;
        long audioLen;
        long dataLen;
        long sampleRate = sample;
        int channelNum = 1;  // if channel change to 2, it will be wrong
        long byteRate = 16 * sample * channelNum / 8; // byte rate
        byte[] data = new byte[buffer];
        try {
            in = new FileInputStream(filePath);
            out = new FileOutputStream(outPath);
            audioLen = in.getChannel().size();
            dataLen = audioLen + 36;

            writeWaveFileHeader(out, audioLen, dataLen,
                    sampleRate, channelNum, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * add wav head to pcm file which has been removed head
     */
    private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';  //WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;   // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (1 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16;  // bits per sample
        header[35] = 0;
        header[36] = 'd'; //data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}