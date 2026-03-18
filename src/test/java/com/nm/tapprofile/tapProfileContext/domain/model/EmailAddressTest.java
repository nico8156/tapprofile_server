package com.nm.tapprofile.tapProfileContext.domain.model;

import com.nm.tapprofile.tapProfileContext.domain.errors.InvalidEmailError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailAddressTest {

	@Test
	void should_create_valid_email_address() {
		var result = EmailAddress.create("alex@example.com");

		assertTrue(result.isValid());
		assertEquals("alex@example.com", result.get().value());
	}

	@Test
	void should_fail_when_email_is_blank() {
		var result = EmailAddress.create("   ");

		assertTrue(result.isInvalid());
		assertEquals(1, result.getErrors().size());
	}

	@Test
	void should_fail_when_email_is_invalid() {
		var result = EmailAddress.create("alex-at-example.com");

		assertTrue(result.isInvalid());
		assertTrue(result.getErrors().stream().anyMatch(error -> error instanceof InvalidEmailError));
	}
}
