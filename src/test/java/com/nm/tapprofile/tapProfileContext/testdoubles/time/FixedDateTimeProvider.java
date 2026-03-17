package com.nm.tapprofile.tapProfileContext.testdoubles.time;

import com.nm.tapprofile.tapProfileContext.shared.time.DateTimeProvider;

import java.time.Instant;

public final class FixedDateTimeProvider implements DateTimeProvider {

	private final Instant fixedNow;

	public FixedDateTimeProvider(Instant fixedNow) {
		this.fixedNow = fixedNow;
	}

	@Override
	public Instant now() {
		return fixedNow;
	}
}
