package com.evanram.voip;

public class CallData
{
	public static final byte[]
	RESERVED_LOW		= b(0x00),
	END_CALL			= b(0x01),
	RESERVED_HIGH		= b(0x7F);

	//form byte array from ints, cast to bytes
	private static byte[] b(int... data)
	{
		byte[] arry = new byte[data.length];
		for(int i = 0; i < arry.length; i++)
			arry[i] = (byte) data[i];

		return arry;
	}
}