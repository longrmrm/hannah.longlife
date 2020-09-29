package com.hannah.ip;

import java.io.File;
import java.io.IOException;

public class IPSeekerTest {

	public static void main(String[] args) throws IOException {
		IPSeeker seeker = new IPSeeker(new File("QQWry.DAT"));
		IPLocation location = seeker.getLocation("211.155.113.233");
		System.out.println(location);
	}

}