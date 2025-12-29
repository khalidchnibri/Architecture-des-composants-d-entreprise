import React, { useState, useEffect } from 'react';
import './NotificationPanel.css';

function NotificationPanel() {
  const [notifications, setNotifications] = useState([]);
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    // Simuler la réception de notifications depuis le backend
    // En production, cela pourrait être via WebSocket ou polling
    const storedNotifications = JSON.parse(localStorage.getItem('notifications') || '[]');
    setNotifications(storedNotifications);
  }, []);

  const addNotification = (message, type = 'info') => {
    const newNotification = {
      id: Date.now(),
      message,
      type,
      timestamp: new Date().toLocaleString('fr-FR')
    };
    const updated = [newNotification, ...notifications].slice(0, 10); // Garder les 10 dernières
    setNotifications(updated);
    localStorage.setItem('notifications', JSON.stringify(updated));
  };

  const clearNotifications = () => {
    setNotifications([]);
    localStorage.setItem('notifications', JSON.stringify([]));
  };

  // Exposer la fonction pour qu'elle puisse être appelée depuis d'autres composants
  useEffect(() => {
    window.addNotification = addNotification;
    return () => {
      delete window.addNotification;
    };
  }, [notifications]);

  return (
    <div className="notification-panel">
      <button 
        className="notification-toggle" 
        onClick={() => setIsOpen(!isOpen)}
        title="Notifications"
      >
        🔔 {notifications.length > 0 && <span className="notification-badge">{notifications.length}</span>}
      </button>
      {isOpen && (
        <div className="notification-dropdown">
          <div className="notification-header">
            <h3>Notifications</h3>
            {notifications.length > 0 && (
              <button onClick={clearNotifications} className="clear-btn">Effacer tout</button>
            )}
          </div>
          <div className="notification-list">
            {notifications.length === 0 ? (
              <div className="notification-empty">Aucune notification</div>
            ) : (
              notifications.map(notif => (
                <div key={notif.id} className={`notification-item notification-${notif.type}`}>
                  <div className="notification-message">{notif.message}</div>
                  <div className="notification-time">{notif.timestamp}</div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default NotificationPanel;

