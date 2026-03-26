package com.nm.tapprofile.tapProfileContext.application.ports;

import com.nm.tapprofile.tapProfileContext.domain.model.EmailAddress;

import java.util.UUID;

public interface MagicLinkEmailSender {
	void sendMagicLink(EmailAddress recipient, UUID token);
}
