package com.sinaapp.bashell.sayhi;

import java.io.IOException;

public class RtmpClient extends Thread implements Publishable {
	private final String TAG = "RtmpClient";
	private RtmpSocket mRtmpSocket = new RtmpSocket();
	private AudioCenter mAudioCenter = new AudioCenter();
	private byte[] revData = new byte[1024];
	private byte mPeerId;

	public RtmpClient() {
		mAudioCenter.setPublishable(this);
	}

	public void init(String serverIp, String myId) {
		mRtmpSocket.connect(serverIp);
		start();
		mAudioCenter.playSpeexAudio();
		mAudioCenter.publishSpeexAudio();
	}

	public void setSayTo(byte peerId) {
		mPeerId = peerId;
		mAudioCenter.playSpeexAudio();
		mAudioCenter.publishSpeexAudio();
	}

	public void sayTo(byte peerId) {
		mPeerId = peerId;
		mAudioCenter.playSpeexAudio();
		mAudioCenter.publishSpeexAudio();
	}

	private boolean mRunnning = false;

	public void setRunnning(boolean runnning) {
		mRunnning = runnning;
	}

	public void close() {
		setRunnning(false);
		mRtmpSocket.close();
		mAudioCenter.closeAll();
	}

	@Override
	public void run() {
		setRunnning(true);
		while (mRunnning)
			try {
				int len = mRtmpSocket.read(revData);
				if (len > 0) {
					toDispatch(revData, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public void toDispatch(byte[] revData, int len) {
		byte code = revData[0];
		byte[] data = new byte[len - 1];
		System.arraycopy(revData, 1, data, 0, len - 1);
		dispatch(code, data);
	}

	public void dispatch(byte code, byte[] data) {
		switch (code) {
		case Code.CODE_SPEEX:
			mAudioCenter.putData(data);
			break;
		default:
			break;
		}
	}

	@Override
	public void onPublish(byte[] data) {
		try {
			mRtmpSocket.send(toPublishData(data));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] toPublishData(byte[] data) {
		byte[] publishData = new byte[data.length + 2];
		System.arraycopy(Code.CODE_SPEEX, 0, publishData, 0, 1);
		System.arraycopy(mPeerId, 0, publishData, 1, 1);
		System.arraycopy(data, 0, publishData, 2, data.length);
		return publishData;
	}

}
