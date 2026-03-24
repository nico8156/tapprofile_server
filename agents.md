# TapProfile Server Agent Guide

## Purpose

This repository contains the TapProfile backend.

The goal of the product is to support a meetup badge + connection flow:

- a participant creates a profile
- gets a badge
- shows a QR code during an event
- another participant scans the badge
- if the scanner is identified, a connection is created
- later, the participant can review badge metrics and connections

This backend must evolve incrementally without broad rewrites.

---

## Non-negotiable architecture rules

This project uses:

- Spring Boot
- Hexagonal architecture
- DDD-inspired modeling
- functional / explicit error handling
- tests-first mindset
- no broad mocking culture
- thin HTTP controllers

You must respect the existing architecture and coding style.

### Mandatory rules

- DO NOT refactor unrelated code
- DO NOT move packages unless strictly necessary
- DO NOT rename concepts casually
- DO NOT introduce new frameworks
- DO NOT introduce hidden magic or implicit behavior
- DO NOT replace the existing architectural style
- DO NOT simplify domain concepts in a way that breaks business meaning
- KEEP controllers thin
- KEEP domain rules in domain/application layers
- KEEP tests aligned with the current project style

### Preferred approach

- adapt incrementally
- add minimal code for the requested feature
- preserve backward compatibility when possible
- confirm the actual existing contract before assuming a richer one

---

## Product model

TapProfile is no longer only a lead capture tool.

The current MVP direction is:

- badge-based networking for meetup participants
- connection creation between participants
- dashboard with scans and connections
- simple post-event exploitation of contacts

---

## Core domain concepts

### Profile

Represents a participant identity.

Typical fields:
- profileId
- slug
- displayName
- headline
- bio
- role: VISITOR | EXHIBITOR
- status: DRAFT | PUBLISHED
- publishedAt

Important:
- role is a real business concept
- role must NEVER be derived from headline or any descriptive field

### Badge

Represents the public scanning identity of a profile.

Typical fields:
- badgeId
- profileId
- badgeToken
- status
- createdAt

Important:
- badgeToken must be opaque and non-predictable
- do not expose internal IDs in QR logic if a badge token exists
- badgeToken is light obfuscation, not strong security

### Connection

Represents a relationship created when one participant scans another participant badge.

Typical fields:
- connectionId
- scannerProfileId
- scannedProfileId
- createdAt

Important invariants:
- scannerProfileId != scannedProfileId
- both profiles must exist
- scanned profile must be publicly valid / published
- no fake scanner identity inferred from thin air

---

## Source of truth: backend contracts

Always check actual code before changing contracts.

### Current known endpoints

#### Create profile
POST `/api/profiles`

Request:
```json
{
  "slug": "alex-martin",
  "displayName": "Alex Martin",
  "headline": "Backend developer",
  "bio": "I build useful products.",
  "role": "EXHIBITOR"
}
````

Response:

```json
{
  "profileId": "uuid"
}
```

Backward compatibility:

* role may be omitted
* if omitted, default is VISITOR

#### Publish profile

POST `/api/profiles/{profileId}/publish`

Response:

* 204 No Content

#### Get dashboard

GET `/api/profiles/{profileId}/dashboard`

Current actual response shape may still include legacy-compatible fields such as:

* viewCount
* scanCount
* leadCount
* connectionCount
* recentLeads

Do not assume dashboard already exposes:

* badge
* recentConnections

Check the actual response before modifying clients or read models.

#### Get profile badge

GET `/api/profiles/{profileId}/badge`

Response:

```json
{
  "badgeToken": "opaque-token",
  "publicBadgeUrl": "http://localhost:3000/b/{badgeToken}"
}
```

#### Get public badge

GET `/api/public/badges/{badgeToken}`

Response:

```json
{
  "profileId": "uuid",
  "displayName": "Alex Martin",
  "headline": "Backend developer",
  "role": "EXHIBITOR"
}
```

#### Create connection

POST `/api/connections`

Current request contract must be checked in code before changing.
If modifying it, do so minimally and explicitly.

#### Get connections

GET `/api/profiles/{profileId}/connections`

Current known response can be a simple array:

```json
[
  {
    "profileId": "uuid",
    "displayName": "Nina",
    "headline": "Designer",
    "role": "VISITOR",
    "createdAt": "2026-03-23T17:03:15.060758Z"
  }
]
```

#### Error format

```json
{
  "errors": [
    {
      "code": "string",
      "message": "string",
      "field": null
    }
  ]
}
```

Always preserve this error style unless explicitly asked otherwise.

---

## Contract discipline

Contract mismatches have already been a major source of friction.

Therefore:

* DO NOT invent richer responses than the backend actually returns
* DO NOT assume frontend expectations are correct
* DO NOT change an endpoint contract casually
* IF a new field is needed, verify whether:

  * it already exists elsewhere
  * a separate endpoint already exposes it
  * compatibility needs to be preserved

When changing a response shape:

* document the exact final response
* update tests
* call out backward compatibility implications

---

## DDD / layering guidance

### Domain

Put here:

* entities / aggregates
* value objects
* core invariants
* domain factories/services only when needed

### Application

Put here:

* command handlers
* query handlers
* orchestration across repositories
* explicit result mapping

### Adapters / primary

Put here:

* Spring REST controllers
* DTO mapping
* no business decisions

### Adapters / secondary

Put here:

* repository implementations
* persistence adapters
* infrastructure concerns

---

## Coding rules

* prefer explicitness over cleverness
* prefer small incremental changes
* preserve naming consistency
* keep domain terminology stable
* avoid hidden fallback behavior unless product explicitly requires it

If something is missing in the current domain:

* add the smallest real concept necessary
* do not fake it through another field

Example:

* `role` must be a real field, not derived from `headline`

---

## Testing rules

Testing is part of the feature, not optional.

### Always do

* add or update unit tests for new domain/application behavior
* add or update integration tests for new HTTP endpoints
* run the relevant test suite before finishing

### Required commands

Always run:

```bash
./mvnw test
./mvnw -Dtest='*IT' test
```

If one of these does not cover something, say it explicitly.

### Test philosophy

* follow existing test style
* no broad mocking rewrite
* no unnecessary abstraction in tests
* verify behavior, not implementation trivia

---

## Preferred change strategy

When asked to evolve the backend:

1. inspect the current code first
2. confirm the real current contract
3. identify the smallest incremental change
4. implement in the existing architecture
5. update tests
6. run tests
7. report:

   * what changed
   * exact final contract
   * any assumption made

---

## What to avoid

* broad refactor “for cleanliness”
* replacing existing style with your own favorite style
* silently changing endpoint payloads
* deriving business concepts from unrelated fields
* deleting legacy-compatible behavior unless explicitly requested
* changing both backend and frontend contracts blindly without checking actual usage

---

## Current MVP priorities

The MVP is near completion.

Priority is to finish product-critical features, not redesign the backend.

Key priorities include:

* stable badge retrieval
* stable connection creation
* stable connections listing
* dashboard coherence with scans / connections
* support for frontend exploitation of contacts
* keep contracts precise and explicit

---

## Expected assistant behavior

When working in this repo, you should act like a precise incremental contributor.

You should:

* preserve the maintainer’s architecture
* preserve the maintainer’s domain language
* prefer small reliable steps
* be explicit about assumptions
* verify with tests
* report exact contracts after changes

You should NOT:

* “improve” the repo by broad refactor
* introduce unrelated cleanup
* optimize prematurely
* drift away from the current DDD / hexagonal structure

---

## Final rule

If there is any doubt between:

* making the code more “generic”
* keeping it aligned with current project spirit

choose alignment with current project spirit.

---
