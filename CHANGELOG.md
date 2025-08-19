## Changelog

### [Unreleased]

- Security: Restrict `/api/auth/**` permitAll to only `/api/auth/login` and `/api/auth/register` so `/api/auth/me` requires authentication.
- Tests (Backend): Added `AuthIntegrationTests` covering register → login → current user flow.
- Tests (Frontend): Added minimal render test `App.test.tsx` to enable frontend test suite.

- Docs: Align README Java version with build (`Java 21`).
- Cleanup: Remove `backend/backend/src/main/resources/data.sql` which was outdated and unused.


