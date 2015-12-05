package com.sinaapp.bashell.sayhi;

public class RtmpClientTest implements Publishable {
	private final String TAG = "RtmpClient";
	final byte[] speex = new byte[] { Code.CODE_SPEEX };
	private AudioCenter2 mAudioCenter = new AudioCenter2();
	private byte[] mPeerId = new byte[1];

	public RtmpClientTest() {
		mAudioCenter.setPublishable(this);
	}

	public void init(String serverIp, byte myId) {
	}

	public void sayTo(byte peerId) {
		mPeerId[0] = peerId;
		mAudioCenter.playSpeexAudio();
		mAudioCenter.publishSpeexAudio();
	}

	public void close() {
		mAudioCenter.closeAll();
	}

	public void toDispatch(byte[] revData, int len) {
		byte peerId = revData[0];
		if(peerId==mPeerId[0]){			
		}
		byte[] data = new byte[len - 1];
		System.arraycopy(revData, 1, data, 0, len - 1);
		mAudioCenter.putData(data);
	}

	

	@Override
	public void onPublish(byte[] data) {
		byte[] publishData = toPublishData(data);
		toDispatch(publishData, publishData.length);
	}

	public byte[] toPublishData(byte[] data) {
		byte[] publishData = new byte[data.length + 1];
		System.arraycopy(mPeerId, 0, publishData, 0, 1);
		System.arraycopy(data, 0, publishData, 1, data.length);
		return publishData;
	}

}
