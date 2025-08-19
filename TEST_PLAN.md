## Comprehensive Test Plan - Water Cooler Network

### Scope
Backend (Spring Boot, H2), Frontend (React + TS). Covers unit, integration, and E2E paths for auth, users, matching, lounges, notifications, gamification, corporate, subscriptions, analytics, mentorship, and insights.

### Key Flows
- Authentication: register, login, me.
- Users: profile CRUD, search, admin restrictions.
- Matching: request/respond, list, preferences.
- Lounges: CRUD, join/leave, messages.
- Notifications: list, unread, mark read, prefs.
- Gamification: summary, badges, streaks, leaderboard.
- Corporate: companies, departments, invitations.
- Subscriptions: plans, create/update/cancel/reactivate.
- Analytics: company/department metrics, generate.
- Mentorship: programs, relationships, sessions, feedback.
- Insights: behaviors, insights CRUD, analytics.

### Testing Strategy
- Unit: services and utilities with mocked repos/providers.
- Integration: controller + repo with H2, using MockMvc.
- Frontend: component behavior and routing with mocked API.
- E2E: smoke via Playwright/Cypress (future).

### Success Criteria
- All automated tests pass locally (CI parity).
- Protected endpoints require JWT; public endpoints accessible.
- No runtime exceptions in logs during test runs.


