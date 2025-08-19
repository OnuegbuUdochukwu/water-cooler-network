import React, { useState, useEffect } from 'react';
import './NotificationCenter.css';

interface Notification {
  id: number;
  userId: number;
  title: string;
  message: string;
  type: 'MATCH_FOUND' | 'MESSAGE_RECEIVED' | 'MEETING_SCHEDULED' | 'MEETING_REMINDER' | 
        'FEEDBACK_REQUEST' | 'BADGE_EARNED' | 'LOUNGE_INVITATION' | 'SYSTEM_ANNOUNCEMENT' | 
        'PROFILE_UPDATE' | 'CONNECTION_REQUEST';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  isRead: boolean;
  actionUrl?: string;
  metadata?: string;
  createdAt: string;
  readAt?: string;
  expiresAt?: string;
}

interface NotificationCenterProps {
  isOpen: boolean;
  onClose: () => void;
}

const NotificationCenter: React.FC<NotificationCenterProps> = ({ isOpen, onClose }) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(false);
  const [filter, setFilter] = useState<'all' | 'unread' | 'high-priority'>('all');

  useEffect(() => {
    if (isOpen) {
      fetchNotifications();
    }
  }, [isOpen, filter]);

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      let endpoint = '/api/notifications';
      if (filter === 'unread') {
        endpoint = '/api/notifications/unread';
      } else if (filter === 'high-priority') {
        endpoint = '/api/notifications/priority/high';
      }

      const response = await fetch(endpoint, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data = await response.json();
        setNotifications(Array.isArray(data) ? data : data.content || []);
      }
    } catch (error) {
      console.error('Error fetching notifications:', error);
      // Mock data for development
      setNotifications([
        {
          id: 1,
          userId: 1,
          title: 'New Match Found!',
          message: 'You have a new match with Sarah Johnson. Start a conversation!',
          type: 'MATCH_FOUND',
          priority: 'HIGH',
          isRead: false,
          actionUrl: '/matches',
          createdAt: new Date().toISOString(),
        },
        {
          id: 2,
          userId: 1,
          title: 'Meeting Reminder',
          message: 'Your coffee chat with Mike is in 30 minutes',
          type: 'MEETING_REMINDER',
          priority: 'URGENT',
          isRead: false,
          actionUrl: '/meetings',
          createdAt: new Date(Date.now() - 1800000).toISOString(),
        },
        {
          id: 3,
          userId: 1,
          title: 'Badge Earned!',
          message: 'Congratulations! You\'ve earned the "Conversation Starter" badge',
          type: 'BADGE_EARNED',
          priority: 'MEDIUM',
          isRead: true,
          actionUrl: '/profile',
          createdAt: new Date(Date.now() - 3600000).toISOString(),
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const markAsRead = async (notificationId: number) => {
    try {
      const response = await fetch(`/api/notifications/${notificationId}/read`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        setNotifications(prev =>
          prev.map(notif =>
            notif.id === notificationId ? { ...notif, isRead: true } : notif
          )
        );
      }
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const markAllAsRead = async () => {
    try {
      const response = await fetch('/api/notifications/read-all', {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        setNotifications(prev =>
          prev.map(notif => ({ ...notif, isRead: true }))
        );
      }
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };

  const deleteNotification = async (notificationId: number) => {
    try {
      const response = await fetch(`/api/notifications/${notificationId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        setNotifications(prev => prev.filter(notif => notif.id !== notificationId));
      }
    } catch (error) {
      console.error('Error deleting notification:', error);
    }
  };

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'MATCH_FOUND': return '🤝';
      case 'MESSAGE_RECEIVED': return '💬';
      case 'MEETING_SCHEDULED': return '📅';
      case 'MEETING_REMINDER': return '⏰';
      case 'BADGE_EARNED': return '🏆';
      case 'LOUNGE_INVITATION': return '🏠';
      case 'SYSTEM_ANNOUNCEMENT': return '📢';
      default: return '🔔';
    }
  };

  const getPriorityClass = (priority: string) => {
    switch (priority) {
      case 'URGENT': return 'priority-urgent';
      case 'HIGH': return 'priority-high';
      case 'MEDIUM': return 'priority-medium';
      default: return 'priority-low';
    }
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMinutes = Math.floor((now.getTime() - date.getTime()) / (1000 * 60));

    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}h ago`;
    return `${Math.floor(diffInMinutes / 1440)}d ago`;
  };

  if (!isOpen) return null;

  return (
    <div className="notification-center-overlay" onClick={onClose}>
      <div className="notification-center" onClick={e => e.stopPropagation()}>
        <div className="notification-header">
          <h3>Notifications</h3>
          <div className="notification-actions">
            <select 
              value={filter} 
              onChange={(e) => setFilter(e.target.value as any)}
              className="filter-select"
            >
              <option value="all">All</option>
              <option value="unread">Unread</option>
              <option value="high-priority">High Priority</option>
            </select>
            <button onClick={markAllAsRead} className="mark-all-read-btn">
              Mark All Read
            </button>
            <button onClick={onClose} className="close-btn">×</button>
          </div>
        </div>

        <div className="notification-list">
          {loading ? (
            <div className="loading">Loading notifications...</div>
          ) : notifications.length === 0 ? (
            <div className="empty-state">
              <div className="empty-icon">🔔</div>
              <p>No notifications found</p>
            </div>
          ) : (
            notifications.map(notification => (
              <div
                key={notification.id}
                className={`notification-item ${!notification.isRead ? 'unread' : ''} ${getPriorityClass(notification.priority)}`}
                onClick={() => {
                  if (!notification.isRead) {
                    markAsRead(notification.id);
                  }
                  if (notification.actionUrl) {
                    window.location.href = notification.actionUrl;
                  }
                }}
              >
                <div className="notification-icon">
                  {getNotificationIcon(notification.type)}
                </div>
                <div className="notification-content">
                  <div className="notification-title">{notification.title}</div>
                  <div className="notification-message">{notification.message}</div>
                  <div className="notification-time">{formatTime(notification.createdAt)}</div>
                </div>
                <div className="notification-controls">
                  {!notification.isRead && <div className="unread-indicator"></div>}
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      deleteNotification(notification.id);
                    }}
                    className="delete-btn"
                  >
                    ×
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default NotificationCenter;
