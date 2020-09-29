package com.hannah.ip;

import com.hannah.common.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IPSeeker {

	final ByteBuffer buffer;
	final IpHelper h;
	final int offsetBegin, offsetEnd;

	public IPSeeker(String filePath) throws IOException {
		this(new File(filePath));
	}

	/**
	 * read ip data from file
	 * @param file ip datafile, can download from http://www.cz88.net/
	 * @throws IOException
	 */
	public IPSeeker(File file) throws IOException {
		if (file.exists()) {
			buffer = ByteBuffer.wrap(FileUtil.readBytes(file));
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			offsetBegin = buffer.getInt(0);
			offsetEnd = buffer.getInt(4);
			if (offsetBegin == -1 || offsetEnd == -1) {
				throw new IllegalArgumentException("File Format Error");
			}
			h = new IpHelper(this);
		} else {
			throw new FileNotFoundException();
		}
	}

	public IPLocation getLocation(final String ip) {
		String[] ips = ip.split("\\.");
		return getLocation(Integer.parseInt(ips[0]), Integer.parseInt(ips[1]), Integer.parseInt(ips[2]), Integer.parseInt(ips[3]));
	}

	public IPLocation getLocation(final int ip1, final int ip2, final int ip3, final int ip4) {
		return getLocation((byte) ip1, (byte) ip2, (byte) ip3, (byte) ip4);
	}

	public IPLocation getLocation(final byte ip1, final byte ip2, final byte ip3, final byte ip4) {
		return getLocation(new byte[] { ip1, ip2, ip3, ip4 });
	}

	protected final IPLocation getLocation(final byte[] ip) {
		return h.getLocation(h.locateOffset(ip));
	}

	public IPLocation getLocation(final Inet4Address address) {
		return getLocation(address.getAddress());
	}
}