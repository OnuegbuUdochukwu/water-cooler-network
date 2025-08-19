# Water Cooler Network – System Design

## 1. Overview
The Water Cooler Network is a digital platform that recreates informal professional networking for remote and hybrid workers through:
- Instant "Coffee Chat" matchmaking
- Topic Lounges
- Gamification features
- Corporate private spaces
- Analytics for HR

Our goal is to foster organic connections and reduce isolation in distributed work environments.

---

## 2. High-Level Architecture
- Frontend Web App
  - Connects to Backend API
    - Connects to Database
  - Integrates with:
    - Video Chat (WebRTC)
      - Push/Email Notifications
- Backend API
  - Powers:
    - Matching Engine
      - Corporate Admin Dashboard


**Planned Tech Stack**
- **Frontend**: React (web), React Native (mobile)
- **Backend**: Spring Boot or Node.js/NestJS
- **Database**: PostgreSQL
- **Video**: WebRTC / OpenTok / Daily.co
- **Auth**: OAuth2 (Google, LinkedIn)
- **Payments**: Stripe / Paystack
- **Hosting**: AWS / Render / Vercel

---

## 3. Feature Breakdown & Modules

### 3.1 User Onboarding & Profile Integration
- Secure signup (email, Google, LinkedIn)
- Industry, skills, interests input
- LinkedIn API integration for importing professional data

### 3.2 Coffee Chat Matching System
- Matchmaking algorithm (blend of shared traits + randomness)
- Instant match queue for “I’m available now”
- 1-on-1 video chat room with conversation prompts

### 3.3 Topic Lounges
- Persistent, themed chat/video rooms
- Threaded discussions
- Article/resource sharing

### 3.4 Gamification & Engagement
- Chat streaks
- Badges for cross-industry chats
- Mentorship pairing

### 3.5 Corporate "Water Cooler" Spaces
- Private internal company spaces
- Cross-department matchmaking
- New hire onboarding chats

### 3.6 Analytics Dashboard (Corporate)
- Anonymized engagement reports
- Cross-department connection trends

### 3.7 Subscription & Payment System
- Freemium tier for individuals
- Premium $15/month
- Corporate per-employee/month billing

### 3.8 Notifications
- Push notifications
- Email updates for chats, streaks, and lounges

---

## 4. Data Models

### Users
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Unique user ID |
| name | String | Full name |
| email | String | User email |
| password_hash | String | Hashed password |
| role | Enum | user / admin |
| industry | String | Industry field |
| skills | Array | List of skills |
| interests | Array | Professional interests |
| company_id | UUID | Linked company |
| linkedin_url | String | LinkedIn profile |

### Matches
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Match ID |
| user1_id | UUID | First user |
| user2_id | UUID | Second user |
| match_time | Timestamp | Time of match |
| match_type | Enum | random / mentorship |
| status | Enum | completed / cancelled |

### Lounges
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Lounge ID |
| title | String | Lounge name |
| description | String | Lounge details |
| topic | String | Topic category |
| created_by | UUID | User ID of creator |
| visibility | Enum | public / private |

### Messages
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Message ID |
| lounge_id | UUID | Lounge reference |
| user_id | UUID | Sender |
| content | Text | Message body |
| timestamp | Timestamp | Time sent |

### Achievements
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Badge ID |
| user_id | UUID | User |
| badge_type | String | Type of achievement |
| date_earned | Timestamp | When earned |

### Companies
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Company ID |
| name | String | Company name |
| admin_id | UUID | Company admin |
| subscription_tier | String | Tier level |

### Payments
| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Payment ID |
| user_id/company_id | UUID | Payer |
| amount | Decimal | Amount paid |
| plan | String | Plan type |
| status | String | Status |
| payment_date | Timestamp | Date of payment |

---

## 5. Build Sequence
1. **Core Backend** – auth, profiles, matchmaking data structures
2. **Coffee Chat Matching + Video Chat**
3. **Topic Lounges**
4. **Gamification**
5. **Corporate Spaces**
6. **Payments & Subscriptions**
7. **Analytics Dashboard**
8. **Notifications**
9. **Mentorship Feature**

---

## 6. Dependencies
- **Matching System** depends on Core Backend
- **Topic Lounges** depends on Auth & User Profiles
- **Gamification** depends on Lounge & Match tracking
- **Corporate Spaces** depends on Core Backend
- **Payments** depends on User & Company entities
- **Analytics** depends on Match, Lounge, and Company data
- **Notifications** depends on Match, Lounge, and Gamification events

---

## 7. Non-Functional Requirements
- **Scalability**: Handle thousands of concurrent video chats
- **Security**: End-to-end encrypted video sessions
- **Performance**: Matchmaking response < 1 second
- **Availability**: 99.9% uptime target

---
