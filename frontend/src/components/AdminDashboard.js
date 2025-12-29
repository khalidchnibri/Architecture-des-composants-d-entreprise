import React, { useEffect, useState } from 'react';
import axios from 'axios';

function AdminDashboard({ showToast }) {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await axios.get('/admin/stats');
        setStats(response.data);
        setLoading(false);
      } catch (err) {
        setError('Échec du chargement des statistiques admin');
        setLoading(false);
        showToast('Échec du chargement des statistiques admin', 'error');
      }
    };
    fetchStats();
  }, [showToast]);

  if (loading) return <div className="card">Chargement du tableau de bord...</div>;
  if (error) return <div className="card error">{error}</div>;

  return (
    <div>
      <h2>Tableau de Bord Administrateur</h2>
      <div className="dashboard-grid">
        <div className="dashboard-card">
          <h3>Total des Événements</h3>
          <p className="dashboard-value">{stats.totalEvents}</p>
        </div>
        <div className="dashboard-card">
          <h3>Total des Réservations</h3>
          <p className="dashboard-value">{stats.totalReservations}</p>
        </div>
        <div className="dashboard-card">
          <h3>Tickets Vendus</h3>
          <p className="dashboard-value">{stats.totalTicketsSold}</p>
        </div>
        <div className="dashboard-card">
          <h3>Revenus Totaux</h3>
          <p className="dashboard-value">
            {stats.totalRevenue.toFixed(2)} <span className="currency">MAD</span>
          </p>
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;
