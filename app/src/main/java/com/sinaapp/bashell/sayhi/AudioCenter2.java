package com.sinaapp.bashell.sayhi;

import java.util.Vector;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class AudioCenter2 {
	private String subTAG = "AudioCenter";

	private static final int frequency = 8000;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	public static int packagesize = 160;

	final byte[] SpeexRtmpHead = new byte[] { (byte) 0xB2 };
	private boolean isPublish, isPlaying;
	private Vector<byte[]> buffer2 = new Vector<byte[]>();
	private Publishable mPublishable;

	public AudioCenter2() {
	}

	public void setPublishable(Publishable publishable) {
		mPublishable = publishable;
	}

	public void putData(byte[] data) {
		buffer2.add(data);
	}

	public void publishSpeexAudio() {

		new Thread(new Runnable() {
			private Speex publishSpeex = Speex.getSpeex();
			private int frameSize;
			private byte[] processedData;

			@Override
			public void run() {
				frameSize = publishSpeex.getFrameSize();
				processedData = new byte[frameSize];

				// int bufferSize = AudioRecord.getMinBufferSize(8000,
				// AudioFormat.CHANNEL_IN_MONO,
				// AudioFormat.ENCODING_PCM_16BIT);
				// short[] mAudioRecordBuffer = new short[bufferSize];
				// AudioRecord mAudioRecord = new AudioRecord(
				// MediaRecorder.AudioSource.MIC, 8000,
				// AudioFormat.CHANNEL_IN_MONO,
				// AudioFormat.ENCODING_PCM_16BIT, bufferSize);
				// mAudioRecord.startRecording();

				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

				int bufferSize = AudioRecord.getMinBufferSize(frequency,
						AudioFormat.CHANNEL_IN_MONO, audioEncoding);
				short[] mAudioRecordBuffer = new short[packagesize];
				AudioRecord mAudioRecord = new AudioRecord(
						MediaRecorder.AudioSource.MIC, frequency,
						AudioFormat.CHANNEL_IN_MONO, audioEncoding, bufferSize);
				mAudioRecord.startRecording();

				int bufferRead = 0;
				int len;
				isPublish = true;

				short[] decData = new short[256];

				while (isPublish) {
					bufferRead = mAudioRecord.read(mAudioRecordBuffer, 0,
							frameSize);
					LogHelper.d("mAudioRecord.read=" + bufferRead, subTAG);
					if (bufferRead > 0) {
						try {
							len = publishSpeex.encode(mAudioRecordBuffer, 0,
									processedData, frameSize);
							byte[] enData = new byte[len];
							System.arraycopy(processedData, 0, enData, 0, len);
							if (mPublishable != null) {
								mPublishable.onPublish(enData);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				mAudioRecord.stop();
				mAudioRecord.release();
				mAudioRecord = null;
				LogHelper.d("Publish SpeexAudio Thread Release", subTAG);
			}
		}, "Publish SpeexAudio Thread").start();
	}

	public void playSpeexAudio() {
		new Thread(new Runnable() {
			private Speex playSpeex = Speex.getSpeex();

			@Override
			public void run() {
				short[] decData = new short[256];
				AudioTrack audioTrack;
				int bufferSizeInBytes = AudioTrack.getMinBufferSize(8000,
						AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT);
				audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
						8000, AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT, 2 * bufferSizeInBytes,
						AudioTrack.MODE_STREAM);
				audioTrack.play();
				isPlaying = true;
				while (isPlaying) {
					while (!buffer2.isEmpty()) {
						byte[] data = buffer2.elementAt(0);
						buffer2.remove(0);
						if (data != null) {
							int declen = playSpeex.decode(data, decData,
									data.length);
							if (declen > 0) {
								audioTrack.write(decData, 0, declen);
								LogHelper.d("audioTrack.write=" + declen,
										subTAG);
							}
						}
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				audioTrack.stop();
				audioTrack.release();
				audioTrack = null;
				buffer2.clear();
				LogHelper.d("Play SpeexAudio Thread Release", subTAG);

			}
		}, "Play SpeexAudio Thread").start();
	}

	public void stopPlay() {
		isPlaying = false;
	}

	public void stopPublish() {
		isPublish = false;
	}

	public void closeAll() {
		isPlaying = false;
		isPublish = false;
	}
}
