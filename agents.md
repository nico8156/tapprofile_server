---
# TapProfile Backend Agent Guide

## 🎯 Current State

The MVP backend is FUNCTIONAL and STABLE.

Core responsibilities:
- profile lifecycle
- badge generation
- connection creation (idempotent)
- dashboard metrics
- connections listing

Focus now:
👉 stability
👉 invariants
👉 correctness
👉 incremental evolution

---

# 🧠 CORE DOMAIN

## Profile
Participant identity.

## Badge
Public scanning identity.

## Connection
Relationship between TWO profiles.

---

# 🔥 CRITICAL INVARIANT (UPDATED)

A connection is UNIQUE per pair:

👉 A-B == B-A

Therefore:

- no duplicate connections allowed
- connection creation MUST be idempotent

---

# 🧱 ARCHITECTURE

- Spring Boot
- Hexagonal architecture
- DDD-inspired

Layers:
- domain
- application
- adapters (primary/secondary)

---

# ⚠️ NON-NEGOTIABLE RULES

- NO massive refactor
- NO contract break without explicit reason
- NO hidden behavior
- NO domain shortcut
- KEEP controllers thin
- KEEP domain logic in domain

---

# 🔐 CONTRACT DISCIPLINE

DO NOT:

- invent fields
- enrich responses arbitrarily
- change payload silently

ALWAYS:

- verify actual code
- document changes
- keep backward compatibility if needed

---

# 📡 API (REFERENCE)

## Profiles
POST /api/profiles  
POST /api/profiles/{id}/publish

## Dashboard
GET /api/profiles/{id}/dashboard

## Badge
GET /api/profiles/{id}/badge  
GET /api/public/badges/{token}

## Connections
POST /api/connections  
GET /api/profiles/{id}/connections

---

# ⚠️ CONNECTION RULE (VERY IMPORTANT)

When creating a connection:

- normalize pair (A,B)
- check existence BOTH directions
- create only if not exists

Behavior:

- first call → create
- duplicate call → no-op (idempotent)

---

# 🧪 TESTING RULES

Always run:

```bash
./mvnw test
./mvnw -Dtest='*IT' test
```
Tests must verify:

no duplicate connections
valid creation
invalid cases rejected
🎯 CURRENT PRIORITIES

NOT building new features.

Focus on:

data correctness
invariant enforcement
contract stability
performance (later)
clean read models
🧠 CHANGE STRATEGY

Always:

inspect current code
confirm contract
implement minimal change
update tests
run tests
report exact changes
🚫 WHAT TO AVOID
broad refactor
changing domain meaning
mixing layers
adding implicit rules
touching multiple concerns at once
FINAL RULE

If unsure:

👉 preserve domain correctness over convenience

