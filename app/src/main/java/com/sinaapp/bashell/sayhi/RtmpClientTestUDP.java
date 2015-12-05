package com.sinaapp.bashell.sayhi;

import java.io.IOException;
import java.net.DatagramPacket;

public class RtmpClientTestUDP extends Thread implements Publishable {
	private final String TAG = "RtmpClient";
	final byte[] speex = new byte[] { Code.CODE_SPEEX };
	private RtmpUDPSocket mRtmpUDPSocket = new RtmpUDPSocket();
	private AudioCenter2 mAudioCenter = new AudioCenter2();
	private byte[] mPeerId = new byte[1];

	public RtmpClientTestUDP() {
		mAudioCenter.setPublishable(this);
	}

	public void init(String serverIp, byte myId) {
		mRtmpUDPSocket.connect(serverIp);
		start();
	}

	public void sayTo(byte peerId) {
		mPeerId[0] = peerId;
		mAudioCenter.playSpeexAudio();
		mAudioCenter.publishSpeexAudio();
	}

	public void close() {
		mAudioCenter.closeAll();
		setRunnning(false);
	}

	private boolean mRunnning = false;

	public void setRunnning(boolean runnning) {
		mRunnning = runnning;
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
		byte peerId = revData[0];
		if (peerId == mPeerId[0]) {
		}
		byte[] data = new byte[len - 1];
		System.arraycopy(revData, 1, data, 0, len - 1);
		mAudioCenter.putData(data);
	}

	@Override
	public void onPublish(byte[] data) {
		byte[] publishData = toPublishData(data);
		// toDispatch(publishData, publishData.length);
		try {
			mRtmpUDPSocket.send(publishData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] toPublishData(byte[] data) {
		byte[] publishData = new byte[data.length + 1];
		System.arraycopy(mPeerId, 0, publishData, 0, 1);
		System.arraycopy(data, 0, publishData, 1, data.length);
		return publishData;
	}

}
