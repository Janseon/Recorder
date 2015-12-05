/******************************************************************************
 *                                                                            *
 * Copyright (c) 1999-2004 Wimba S.A., All Rights Reserved.                   *
 *                                                                            *
 * COPYRIGHT:                                                                 *
 *      This software is the property of Wimba S.A.                           *
 *      This software is redistributed under the Xiph.org variant of          *
 *      the BSD license.                                                      *
 *      Redistribution and use in source and binary forms, with or without    *
 *      modification, are permitted provided that the following conditions    *
 *      are met:                                                              *
 *      - Redistributions of source code must retain the above copyright      *
 *      notice, this list of conditions and the following disclaimer.         *
 *      - Redistributions in binary form must reproduce the above copyright   *
 *      notice, this list of conditions and the following disclaimer in the   *
 *      documentation and/or other materials provided with the distribution.  *
 *      - Neither the name of Wimba, the Xiph.org Foundation nor the names of *
 *      its contributors may be used to endorse or promote products derived   *
 *      from this software without specific prior written permission.         *
 *                                                                            *
 * WARRANTIES:                                                                *
 *      This software is made available by the authors in the hope            *
 *      that it will be useful, but without any warranty.                     *
 *      Wimba S.A. is not liable for any consequence related to the           *
 *      use of the provided software.                                         *
 *                                                                            *
 * Class: RawWriter.java                                                      *
 *                                                                            *
 * Author: Marc GIMPEL                                                        *
 *                                                                            *
 * Date: 6th January 2004                                                     *
 *                                                                            *
 ******************************************************************************/

/* $Id: AudioFileWriter.java,v 1.1 2011/12/27 04:39:13 gauss Exp $ */

package com.sinaapp.bashell.sayhi;

public abstract class ByteInt {
	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		// 必须把我们要的值弄到最低位去，有人说不移位这样做也可以， result[0] = (byte)(i & 0xFF000000);
		// ，这样虽然把第一个字节取出来了，但是若直接转换为byte类型，会超出byte的界限，出现error。再提下数//之间转换的原则（不管两种类型的字节大小是否一样，原则是不改变值，内存内容可能会变，比如int转为//float肯定会变）所以此时的int转为byte会越界，只有int的前三个字节都为0的时候转byte才不会越界。虽//然
		// result[0] = (byte)(i & 0xFF000000); 这样不行，但是我们可以这样 result[0] =
		// (byte)((i & //0xFF000000) >>24);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	public static void writeInt(byte[] data, int offset, int i) {
		data[offset + 0] = (byte) ((i >> 24) & 0xFF);
		data[offset + 1] = (byte) ((i >> 16) & 0xFF);
		data[offset + 2] = (byte) ((i >> 8) & 0xFF);
		data[offset + 3] = (byte) (i & 0xFF);
	}

	public static int readInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;// 往高位游
		}
		return value;
	}

	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			value |= b[i];
			value = value << 8;
		}
		return value;
	}
}
