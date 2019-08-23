package com.basiscomponents.bc.config;

public class CachingConfiguration {
	private long timeToLive;

	public long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(final long timeToLive) {
		this.timeToLive = timeToLive*1000;
	}
}
