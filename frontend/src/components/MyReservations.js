import React, { useState, useEffect } from 'react';
import axios from 'axios';

function MyReservations({ showToast }) {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchReservations();
  }, []);

  const fetchReservations = async () => {
    try {
      const response = await axios.get('/reservations');
      setReservations(response.data);
      setLoading(false);
    } catch (err) {
      setError('Échec du chargement des réservations');
      setLoading(false);
      showToast('Échec du chargement des réservations', 'error');
    }
  };

  const handlePayment = async (reservationId) => {
    try {
      const response = await axios.post('/payments', { reservationId });
      if (response.data.status === 'SUCCESS') {
        showToast('Paiement réussi ! Vous recevrez une confirmation par email et SMS.', 'success');
        // Ajouter une notification dans le panneau
        if (window.addNotification) {
          window.addNotification('📧 Email envoyé : Confirmation de paiement', 'success');
          window.addNotification('📱 SMS envoyé : Votre paiement a été confirmé', 'success');
        }
      } else {
        showToast('Paiement échoué. Veuillez réessayer.', 'error');
        if (window.addNotification) {
          window.addNotification('❌ Paiement échoué. Veuillez réessayer.', 'error');
        }
      }
      fetchReservations();
    } catch (err) {
      const errorMsg = err.response?.data?.message || 'Échec du paiement';
      showToast(errorMsg, 'error');
    }
  };

  if (loading) return <div className="card">Chargement...</div>;
  if (error) return <div className="card error">{error}</div>;

  return (
    <div>
      <h2 className="page-title">Mes Réservations</h2>
      {reservations.length === 0 ? (
        <div className="card">Aucune réservation trouvée.</div>
      ) : (
        <div className="event-grid">
          {reservations.map(reservation => (
            <div key={reservation.id} className="event-card">
              <h3>{reservation.eventTitle}</h3>
              <div style={{ marginTop: '16px' }}>
                <p><strong>Quantité :</strong> {reservation.quantity} ticket(s)</p>
                <p><strong>Montant total :</strong> <span style={{ fontSize: '18px', fontWeight: '700', color: '#667eea' }}>{reservation.totalAmount} MAD</span></p>
                <p><strong>Statut :</strong> {reservation.paid ? (
                  <span className="success" style={{ padding: '6px 12px', borderRadius: '8px', background: 'rgba(16, 185, 129, 0.1)', display: 'inline-block' }}>✅ Payé</span>
                ) : (
                  <span className="error" style={{ padding: '6px 12px', borderRadius: '8px', background: 'rgba(239, 68, 68, 0.1)', display: 'inline-block' }}>⏳ Non payé</span>
                )}</p>
              </div>
              {!reservation.paid && (
                <div className="event-card-footer">
                  <button
                    onClick={() => handlePayment(reservation.id)}
                    className="btn btn-success"
                    style={{ width: '100%' }}
                  >
                    💳 Payer maintenant
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default MyReservations;
