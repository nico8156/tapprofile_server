package com.nm.tapprofile.tapProfileContext.shared.time;

import java.time.Instant;

public final class SystemDateTimeProvider implements DateTimeProvider {
	@Override
	public Instant now() {
		return Instant.now();
	}
}
