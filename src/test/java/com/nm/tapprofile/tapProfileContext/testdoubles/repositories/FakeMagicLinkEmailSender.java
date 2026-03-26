package com.nm.tapprofile.tapProfileContext.testdoubles.repositories;

import com.nm.tapprofile.tapProfileContext.application.ports.MagicLinkEmailSender;
import com.nm.tapprofile.tapProfileContext.domain.model.EmailAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class FakeMagicLinkEmailSender implements MagicLinkEmailSender {

	private final List<SentMagicLinkEmail> sentEmails = new ArrayList<>();

	@Override
	public void sendMagicLink(EmailAddress recipient, UUID token) {
		sentEmails.add(new SentMagicLinkEmail(recipient.value(), token));
	}

	public List<SentMagicLinkEmail> sentEmails() {
		return List.copyOf(sentEmails);
	}

	public record SentMagicLinkEmail(String recipient, UUID token) {
	}
}
