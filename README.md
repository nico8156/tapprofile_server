
API REST pour le MVP "TapProfile" :
création de profils publics, capture de leads en meetup, et dashboard simple.

---

## Base URL


[http://localhost:8080](http://localhost:8080)


---

# 🔐 Conventions

- JSON uniquement
- Pas de Spring Validation
- Erreurs métier → JSON structuré
- Codes HTTP cohérents

---

# 📌 Endpoints

---

## 1. Create Profile

### POST `/api/profiles`

Créer un profil (draft).

### Request
```json
{
  "slug": "alex-martin",
  "displayName": "Alex Martin",
  "headline": "Backend developer",
  "bio": "I build useful products."
}
```
### Response (201)
```json
{
  "profileId": "uuid"
}
```

### Errors (400 / 409)
```json
{
  "errors": [
    {
      "code": "field.blank",
      "message": "Field 'slug' must not be blank",
      "field": "slug"
    }
  ]
}

```

---

## 2. Publish Profile

### POST `/api/profiles/{profileId}/publish`

### Response

* `204 No Content`

### Errors

* `404` → profile not found
* `409` → already published

---

## 3. Get Public Profile

### GET `/api/public/profiles/{slug}`

### Response (200)
```json
{
  "profileId": "uuid",
  "slug": "alex-martin",
  "displayName": "Alex Martin",
  "headline": "Backend developer",
  "bio": "I build useful products.",
  "publishedAt": "2026-03-17T11:00:00Z"
}

```

### Errors

* `404` → not found or not published

---

## 4. Register Profile View

### POST `/api/public/profiles/{slug}/views`

### Response

* `201 Created`

### Errors

* `404` → profile not found
* `404` → profile not published

---

## 5. Capture Lead

### POST `/api/public/profiles/{slug}/leads`

### Request

```json
{
  "firstName": "Nina",
  "email": "nina@example.com",
  "message": "Hello Alex"
}
```

### Response (201)

```json
{
  "leadId": "uuid"
}
```

### Errors (400)

```json
{
  "errors": [
    {
      "code": "email.invalid",
      "message": "Field 'email' must be a valid email address",
      "field": "email"
    }
  ]
}
```

---

## 6. Get Dashboard

### GET `/api/profiles/{profileId}/dashboard`

### Response (200)

```json
{
  "profile": {
    "profileId": "uuid",
    "slug": "alex-martin",
    "displayName": "Alex Martin",
    "status": "PUBLISHED"
  },
  "metrics": {
    "viewCount": 10,
    "leadCount": 3,
    "conversionRate": 30.0
  },
  "recentLeads": [
    {
      "leadId": "uuid",
      "firstName": "Nina",
      "email": "nina@example.com",
      "message": "Hello Alex",
      "createdAt": "2026-03-17T15:00:00Z"
    }
  ]
}
```

---

# ⚠️ Error Model

```json
{
  "errors": [
    {
      "code": "string",
      "message": "string",
      "field": "optional"
    }
  ]
}
```

---

# 🧠 Notes Front

* Le `slug` est l'identifiant public
* Le `profileId` est l'identifiant interne
* `conversionRate` = `(leadCount / viewCount) * 100`
* Aucun auth pour le MVP


---
