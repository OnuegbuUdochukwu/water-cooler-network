import React, { useState, useEffect } from 'react';
import NotificationCenter from './NotificationCenter';
import './NotificationBell.css';

interface NotificationBellProps {
  className?: string;
}

const NotificationBell: React.FC<NotificationBellProps> = ({ className }) => {
  const [unreadCount, setUnreadCount] = useState(0);
  const [showNotifications, setShowNotifications] = useState(false);
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    fetchUnreadCount();
    // Set up WebSocket connection for real-time notifications
    setupWebSocketConnection();
    
    return () => {
      // Cleanup WebSocket connection
    };
  }, []);

  const fetchUnreadCount = async () => {
    try {
      const response = await fetch('/api/notifications/unread/count', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const count = await response.json();
        setUnreadCount(count);
      }
    } catch (error) {
      console.error('Error fetching unread count:', error);
      // Mock data for development
      setUnreadCount(3);
    }
  };

  const setupWebSocketConnection = () => {
    try {
      // In a real implementation, you would set up SockJS/STOMP connection here
      // For now, we'll simulate real-time updates
      const interval = setInterval(() => {
        // Simulate occasional new notifications
        if (Math.random() < 0.1) { // 10% chance every 5 seconds
          setUnreadCount(prev => prev + 1);
          showNotificationToast();
        }
      }, 5000);

      setIsConnected(true);
      
      return () => clearInterval(interval);
    } catch (error) {
      console.error('Error setting up WebSocket connection:', error);
      setIsConnected(false);
    }
  };

  const showNotificationToast = () => {
    // Create a temporary toast notification
    const toast = document.createElement('div');
    toast.className = 'notification-toast';
    toast.innerHTML = `
      <div class="toast-icon">🔔</div>
      <div class="toast-content">
        <div class="toast-title">New Notification</div>
        <div class="toast-message">You have a new notification</div>
      </div>
    `;
    
    document.body.appendChild(toast);
    
    // Animate in
    setTimeout(() => toast.classList.add('show'), 100);
    
    // Remove after 3 seconds
    setTimeout(() => {
      toast.classList.remove('show');
      setTimeout(() => document.body.removeChild(toast), 300);
    }, 3000);
  };

  const handleBellClick = () => {
    setShowNotifications(true);
  };

  const handleCloseNotifications = () => {
    setShowNotifications(false);
    fetchUnreadCount(); // Refresh count when closing
  };

  return (
    <>
      <div className={`notification-bell ${className || ''}`} onClick={handleBellClick}>
        <div className="bell-icon">
          🔔
          {unreadCount > 0 && (
            <span className="notification-badge">
              {unreadCount > 99 ? '99+' : unreadCount}
            </span>
          )}
        </div>
        {isConnected && <div className="connection-indicator"></div>}
      </div>

      <NotificationCenter 
        isOpen={showNotifications} 
        onClose={handleCloseNotifications}
      />
    </>
  );
};

export default NotificationBell;
