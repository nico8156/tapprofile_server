package com.nm.tapprofile.tapProfileContext.shared.time;

import java.time.Instant;

public interface DateTimeProvider {
	Instant now();
}
