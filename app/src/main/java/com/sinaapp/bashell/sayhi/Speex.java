package com.sinaapp.bashell.sayhi;

public class Speex {

	/**
	 * —πÀı÷ ¡ø<br>
	 * 1 : 4kbps (very noticeable artifacts, usually intelligible) 10<br>
	 * 2 : 6kbps (very noticeable artifacts, good intelligibility) 15<br>
	 * 4 : 8kbps (noticeable artifacts sometimes) 20<br>
	 * 6 : 11kpbs (artifacts usually only noticeable with headphones) 28<br>
	 * 8 : 15kbps (artifacts not usually noticeable) 38<br>
	 * 9 : 15kbps (artifacts not usually noticeable) 46<br>
	 * 10 : 24.8kbps (artifacts not usually noticeable) 62
	 */
	public Speex() {
	}

	private static Speex mSpeex;

	public static Speex getSpeex() {
		if (mSpeex == null) {
			mSpeex = new Speex();
		}
		return mSpeex;
	}

	static {
		System.loadLibrary("speex");
		open(4);
	}

	public static native int open(int compression);

	public native int getFrameSize();

	public native int decode(byte encoded[], short lin[], int size);

	public native int encode(short lin[], int offset, byte encoded[], int size);

	public native void close();

}
