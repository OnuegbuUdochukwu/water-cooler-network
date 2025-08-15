-- Initialize default badges for gamification
INSERT INTO badge (name, description, category, icon_url, points_required, created_at, updated_at) VALUES
('Welcome Aboard', 'Joined the Water Cooler Network', 'MILESTONE', '🎉', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('First Connection', 'Made your first coffee chat match', 'SOCIAL', '☕', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Conversation Starter', 'Completed 5 coffee chats', 'SOCIAL', '💬', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Networking Pro', 'Completed 25 coffee chats', 'SOCIAL', '🤝', 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Social Butterfly', 'Completed 50 coffee chats', 'SOCIAL', '🦋', 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Lounge Explorer', 'Joined your first topic lounge', 'ENGAGEMENT', '🏠', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Discussion Leader', 'Created your first topic lounge', 'ENGAGEMENT', '👑', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Community Builder', 'Created 5 topic lounges', 'ENGAGEMENT', '🏗️', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Daily Visitor', 'Logged in for 7 consecutive days', 'STREAK', '📅', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Dedicated User', 'Logged in for 30 consecutive days', 'STREAK', '🔥', 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Loyalty Champion', 'Logged in for 100 consecutive days', 'STREAK', '💎', 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Rising Star', 'Earned 100 total points', 'ACHIEVEMENT', '⭐', 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Network Champion', 'Earned 500 total points', 'ACHIEVEMENT', '🏆', 500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Legend', 'Earned 1000 total points', 'ACHIEVEMENT', '👑', 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);