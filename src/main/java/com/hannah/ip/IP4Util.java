package com.hannah.ip;

public final class IP4Util {
	
	private IP4Util() {
	}

	public static byte[] toBytes(int address) {
		return new byte[] {
			(byte) ((address >>> 24) & 0xFF),
			(byte) ((address >>> 16) & 0xFF),
			(byte) ((address >>>  8) & 0xFF),
			(byte) ((address       ) & 0xFF)
		};
	}

}