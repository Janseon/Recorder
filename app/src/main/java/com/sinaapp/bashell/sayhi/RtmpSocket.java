package com.sinaapp.bashell.sayhi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class RtmpSocket {
	private final String TAG = "RtmpSocket";
	private static final int PORT = 5678;
	private Socket mSocket = null;
	private DataOutputStream mDOS;
	private DataInputStream mDIS;

	public RtmpSocket() {
	}

	public void connect(String serverIp) {
		try {
			mSocket = new Socket(serverIp, PORT);
			mDOS = new DataOutputStream(mSocket.getOutputStream());
			mDIS = new DataInputStream(mSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(byte[] buffer) throws IOException {
		if (mDOS != null) {
			mDOS.write(buffer);
		}
	}

	public int read(byte[] buffer) throws IOException {
		if (mDIS != null) {
			return mDIS.read(buffer);
		}
		return -1;
	}

	public void close() {
		try {
			mDOS.close();
			mDIS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] getBytes(String str)
			throws UnsupportedEncodingException {
		return str.getBytes("UTF-8");
	}

}
