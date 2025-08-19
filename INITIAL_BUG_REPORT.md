## Initial Bug Report

### BUG-001: Login returns 400 (Invalid credentials) despite valid registration
- Area: Backend `/api/auth/login`
- Severity: Critical
- Steps to Reproduce:
  1. POST `/api/auth/register` with valid payload.
  2. POST `/api/auth/login` with same email/password.
- Actual: 400 with `{"message":"Invalid credentials"}`.
- Expected: 200 with JWT token and user profile.
- Root Cause: JWT signing key too short for HS512 (jjwt 0.12 requires sufficient key length). Token generation failed, caught in controller, mapped to 400.
- Fix: Increased `jwt.secret` to a 64+ char value in `application.properties`.
- Validation: New integration test `AuthIntegrationTests` passes; manual logs show successful token issuance and `/api/auth/me` returns 200.

### BUG-002: `/api/auth/me` publicly accessible
- Area: Backend security configuration
- Severity: High
- Steps: GET `/api/auth/me` without Authorization header.
- Actual: Accessible prior to change due to `permitAll` on `/api/auth/**`.
- Expected: 401 Unauthorized.
- Root Cause: Overly broad permit rule in `SecurityConfig`.
- Fix: Restrict permitAll to only `/api/auth/login` and `/api/auth/register`.
- Validation: Auth test exercises `/api/auth/me` with token; anonymous access now requires auth.

### BUG-003: Principal extraction brittle in current user lookup
- Area: Backend `AuthService#getCurrentUser`
- Severity: Medium
- Issue: Using `authentication.getName()` can fail with non-String principals.
- Fix: Robust principal handling supporting `UserDetails` or `String`.
- Validation: `/api/auth/me` works in tests.

### BUG-004: Frontend tests fail due to ESM axios in Jest
- Area: Frontend Jest runner
- Severity: Medium
- Steps: `npm test` with a simple render test.
- Actual: SyntaxError on `axios` ESM import.
- Expected: Tests execute.
- Root Cause: CRA Jest config not transforming axios ESM by default.
- Fix: Mock `axios` in `src/App.test.tsx`.
- Validation: Test suite passes (1 test).

### Notes (not fixed, informational)
- `data.sql` uses table name `badge` that does not match mapped `badges`. Currently harmless as `spring.sql.init.mode=NEVER`.
- README lists Java 24, while `pom.xml` sets `<java.version>21</java.version>`.


