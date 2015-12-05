package com.sinaapp.bashell.sayhi;

import java.io.IOException;
import java.net.DatagramPacket;

public class RtmpClientTestUDP2 extends Thread implements Publishable {
	private final String TAG = "RtmpClient";
	final byte[] speex = new byte[] { Code.CODE_SPEEX };
	private RtmpUDPSocket mRtmpUDPSocket = new RtmpUDPSocket();
	private AudioCenter2 mAudioCenter = new AudioCenter2();
	private byte[] mIds = new byte[2];

	public RtmpClientTestUDP2() {
		mAudioCenter.setPublishable(this);
	}

	public void init(String serverIp, byte myId) {
		mIds[0] = myId;
		mRtmpUDPSocket.connect(serverIp);
		start();
	}

	public void sayTo(byte peerId) {
		mIds[1] = peerId;
		mAudioCenter.playSpeexAudio();
		mAudioCenter.publishSpeexAudio();
	}

	public void close() {
		mAudioCenter.closeAll();
		setRunnning(false);
		toClear();
	}

	private boolean mRunnning = false;

	public void setRunnning(boolean runnning) {
		mRunnning = runnning;
	}

	public boolean isRunnning() {
		return mRunnning;
	}

	@Override
	public void run() {
		setRunnning(true);
		int len = 0;
		while (mRunnning) {
			try {
				DatagramPacket mPacket = mRtmpUDPSocket.read();
				if (mPacket != null && (len = mPacket.getLength()) > 0) {
					byte[] revData = new byte[len];
					System.arraycopy(mPacket.getData(), 0, revData, 0, len);
					toDispatch(revData, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void toDispatch(byte[] revData, int len) {
		byte myId = revData[0];
		byte peerId = revData[1];
		if (peerId == mIds[1]) {
		}
		byte[] data = new byte[len - 2];
		System.arraycopy(revData, 2, data, 0, len - 2);
		mAudioCenter.putData(data);
	}

	@Override
	public void onPublish(byte[] data) {
		byte[] publishData = toPublishData(data);
		try {
			mRtmpUDPSocket.send(publishData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void toClear() {
		byte[] data = new byte[] { 0, 0 };
		try {
			mRtmpUDPSocket.send(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] toPublishData(byte[] data) {
		byte[] publishData = new byte[data.length + 2];
		System.arraycopy(mIds, 0, publishData, 0, 2);
		System.arraycopy(data, 0, publishData, 2, data.length);
		return publishData;
	}

}
