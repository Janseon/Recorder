package com.sinaapp.bashell.sayhi;

import java.io.IOException;

public class RtmpClientTest2 extends Thread implements Publishable {
	private final String TAG = "RtmpClient";
	final byte[] speex = new byte[] { Code.CODE_SPEEX };
	private RtmpSocket mRtmpSocket = new RtmpSocket();
	private AudioCenter2 mAudioCenter = new AudioCenter2();
	private byte[] mPeerId = new byte[1];

	public RtmpClientTest2() {
		mAudioCenter.setPublishable(this);
	}

	public void init(String serverIp, byte myId) {
		mRtmpSocket.connect(serverIp);
		start();
		final byte[] myInfo = new byte[] { 0, myId };
		try {
			mRtmpSocket.send(myInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sayTo(byte peerId) {
		mPeerId[0] = peerId;
		mAudioCenter.playSpeexAudio();
		mAudioCenter.publishSpeexAudio();
	}

	public void close() {
		mAudioCenter.closeAll();
	}

	private boolean mRunnning = false;

	public void setRunnning(boolean runnning) {
		mRunnning = runnning;
	}

	private byte[] revData = new byte[1024];
	private byte[] prossData = new byte[2048];
	private int endpos = 0;

	@Override
	public void run() {
		setRunnning(true);
		while (mRunnning) {
			try {
				int len = mRtmpSocket.read(revData);
				if (len > 0) {
					System.arraycopy(revData, 0, prossData, endpos, len);
					endpos += len;
					int start = 0;
					while (true) {
						int datalen = ByteInt.readInt(prossData, start);
						if (datalen + start > endpos) {
							if (start > 0) {
								int movelen = endpos - start;
								System.arraycopy(prossData, 0, prossData,
										start, movelen);
								endpos = movelen;
							}
							break;
						}
						start += 4;
						toDispatch(prossData, start, datalen);
						start += datalen;
					}
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

	public void toDispatch(byte[] prossData, int offset, int len) {
		byte peerId = prossData[offset];
		if (peerId == mPeerId[0]) {
		}
		byte[] data = new byte[len - 1];
		System.arraycopy(prossData, offset + 1, data, 0, len - 1);
		mAudioCenter.putData(data);
	}

	@Override
	public void onPublish(byte[] data) {
		// byte[] publishData = toPublishData(data);
		// toDispatch(publishData, publishData.length);
		try {
			mRtmpSocket.send(toPublishData(data));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public byte[] toPublishData(byte[] data) {
		int len = data.length + 1;
		byte[] publishData = new byte[len + 4];
		ByteInt.writeInt(publishData, 0, len);
		System.arraycopy(mPeerId, 0, publishData, 4, 1);
		System.arraycopy(data, 0, publishData, 5, data.length);
		return publishData;
	}
}
