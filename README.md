
# TapProfile

TapProfile est un MVP conçu pour tester un usage réel en meetup :
permettre à une personne de partager un profil simple et de capturer des leads via QR code / lien.

---

# 🎯 Objectif produit

Créer un flow simple :

1. Créer un profil
2. Le publier
3. Le partager (QR code / lien)
4. Capturer des leads
5. Visualiser un dashboard

---

# ⚙️ Fonctionnalités actuelles

- Création de profil (draft)
- Publication de profil
- Consultation publique via slug
- Capture de leads
- Tracking des vues
- Dashboard avec :
  - nombre de vues
  - nombre de leads
  - conversion rate

---

# 🏗️ Architecture

Architecture hexagonale (clean architecture) :

````

domain → application → adapters

````

## Domain
- Entités : Profile, Lead, ProfileView
- Value objects : Slug, Email, etc.
- Règles métier pures

## Application
- CommandHandlers / QueryHandlers
- Orchestration
- Ports (repositories)

## Adapters
- HTTP (Spring Boot)
- Repositories in-memory

---

# ❗ Gestion des erreurs

Approche inspirée FP (Functional Programming) :

## Validation (accumulation)
```java
Validation<ValidationError, A>
````

* utilisée pour les value objects
* accumule plusieurs erreurs

## Result (fail-fast)

```java
Result<DomainError, A>
```

* utilisé dans les use cases
* succès OU erreur

## DomainError

* typé
* mappable HTTP
* pas d’exception métier

---

# 🧪 Tests

3 niveaux :

## 1. Domain

* tests purs
* aucune dépendance framework

## 2. Application

* handlers testés avec fakes
* pas de mocks

## 3. Integration (HTTP)

* `MockMvc`
* tests des controllers
* wiring complet

---

# 🔥 Philosophie

* pas de logique métier dans les controllers
* pas de validation Spring
* pas d’exceptions métier
* code testable en isolation
* approche pragmatique (pas dogmatique FP)

---

# 🚀 État du projet

Backend MVP complet :

* API REST fonctionnelle
* flow complet validé
* prêt pour test réel

Prochaine étape :
👉 Front (React) + test terrain meetup

---
