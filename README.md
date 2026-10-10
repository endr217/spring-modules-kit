# spring-modules-kit

A set of Spring Boot 3 (Java 17) modules extracted from a real project. They are meant to be
copied into your own application: take the packages you need, rename `dev.moduleskit` to your
base package, and wire them to your domain.

## Modules

| Package | What it does | Depends on |
|---|---|---|
| `bootstrap` | `Seeder` interface + runner that executes all `Seeder` beans in `@Order` on startup when `app.seed.enabled=true` | - |
| `shared` | `BaseException` hierarchy, `GlobalExceptionHandler` (uniform JSON errors), `AuditLogger`, `BaseRepository`, `@RequiresAdmin` aspect | `security` (for the aspect) |
| `security` | Stateless JWT auth: `JwtService` (HS512), request filter, `SecurityConfig` | `modules.users` |
| `modules.auth` | Register / login endpoints, Google and Facebook OAuth2 code exchange, complete-registration flow | `security`, `modules.users` |
| `modules.users` | Minimal `User` / `Role` / `UserRole` entities and repositories, `RoleSeeder` | `shared`, `bootstrap` |
| `modules.media` | Polymorphic media attachments (`targetType` + `targetId` + collection), local disk storage, image validation, WebP conversions per profile, SVG support | `shared` |
| `modules.translations` | Database-backed translations per entity / field / language | `shared`, `bootstrap` |
| `modules.payments` | Provider abstraction with Stripe Checkout and a Paysera skeleton, webhook dispatch per provider | `shared` |

All controllers are served under `/api/v1` (see `config/ApiConfig`).

## Setup

```bash
cp .env.example .env   # fill in the values
./mvnw spring-boot:run
```

Requires PostgreSQL (the media entity uses `jsonb` columns).

## Wiring it to your application

- **Payments** - implement `modules.payments.service.PaymentOrderPort` as a bean. It tells the
  module what an order costs and is called back when the payment is confirmed. Without it the
  placeholder in `PaymentsConfig` throws on use.
- **Media** - add your entity names to `MediaTargetType` and a conversion profile for each in
  `src/main/resources/config/media-profiles.yml`, then call `MediaFacade.attachMedia(...)`.
- **Security** - `SecurityConfig` denies every route that is not listed. Add matchers for your
  own controllers.
- **Seeding** - implement `Seeder`, annotate with `@Component` and `@Order(n)`.

## Known limitations

- **Paysera is a skeleton.** Request signing is not implemented and
  `PayseraWebhookHandler.verifySignature` rejects every callback until you implement it. Do not
  make it return `true` without real verification - the webhook endpoint is public.
- The OAuth2 callback does not validate the `state` parameter; do that in your frontend or add
  it server-side.
- `CheckoutRequest.idempotencyKey` is accepted but not stored.
- Stripe and Paysera sessions are created in EUR.
- No tests are included.
