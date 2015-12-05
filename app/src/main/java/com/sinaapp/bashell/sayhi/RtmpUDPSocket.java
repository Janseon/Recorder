package com.sinaapp.bashell.sayhi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RtmpUDPSocket {
	private final String TAG = "RtmpUDPSocket";
	private static final int PORT = 5678;
	public static String SERVER = "192.168.1.8";
	InetAddress IPAddress;

	DatagramSocket mSocket;

	public RtmpUDPSocket() {
	}

	public void connect(String serverIp) {
		try {
			mSocket = new DatagramSocket();
			IPAddress = InetAddress.getByName(serverIp);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void send(byte[] data) throws IOException {
		if (mSocket == null) {
			return;
		}
		DatagramPacket sendPacket = new DatagramPacket(data, data.length,
				IPAddress, PORT);
		mSocket.send(sendPacket);
	}

	public DatagramPacket read() throws IOException {
		if (mSocket == null) {
			return null;
		}
		byte[] buffer = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
		mSocket.receive(receivePacket);
		return receivePacket;
	}

	public void close() {
		if (mSocket == null) {
			return;
		}
		mSocket.close();
	}

	public static byte[] getBytes(String str)
			throws UnsupportedEncodingException {
		return str.getBytes("UTF-8");
	}

}
