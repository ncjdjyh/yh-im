package com.neo.yhrpc.util;

import java.util.UUID;

public class RequestId {

	public static String next() {
		return UUID.randomUUID().toString();
	}

}
