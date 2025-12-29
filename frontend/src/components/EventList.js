import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function EventList({ showToast }) {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editingEventId, setEditingEventId] = useState(null);
  const [editForm, setEditForm] = useState({});
  const navigate = useNavigate();

  const currentUsername = localStorage.getItem('username');

  useEffect(() => {
    fetchEvents();
  }, []);

  const fetchEvents = async () => {
    try {
      const response = await axios.get('/events');
      setEvents(response.data);
      setLoading(false);
    } catch (err) {
      setError('Échec du chargement des événements');
      setLoading(false);
      showToast('Échec du chargement des événements', 'error');
    }
  };

  const handleReserve = async (eventId, quantity) => {
    try {
      const event = events.find(e => e.id === eventId);
      await axios.post('/reservations', { eventId, quantity });
      showToast(`Réservation réussie ! ${quantity} ticket(s) réservé(s)`, 'success');
      // Ajouter des notifications dans le panneau
      if (window.addNotification) {
        window.addNotification(`📧 Email envoyé : Réservation confirmée pour ${event?.title}`, 'success');
        window.addNotification(`📱 SMS envoyé : ${quantity} ticket(s) réservé(s) pour ${event?.title}`, 'success');
      }
      navigate('/my-reservations');
    } catch (err) {
      const errorMsg = err.response?.data?.message || 'Désolé, la limite de réservation est de 4 tickets maximum par événement. Veuillez ajuster votre quantité.';
      showToast(errorMsg, 'error');
    }
  };

  const handleDelete = async (eventId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer cet événement ?')) return;
    try {
      await axios.delete(`/events/${eventId}`);
      showToast('Événement supprimé avec succès', 'success');
      await fetchEvents();
    } catch (err) {
      const errorMsg = err.response?.data?.message || 'Échec de la suppression';
      showToast(errorMsg, 'error');
    }
  };

  const startEdit = (event) => {
    setEditingEventId(event.id);
    setEditForm({
      title: event.title,
      description: event.description,
      dateTime: event.dateTime,
      location: event.location,
      totalTickets: event.totalTickets,
      ticketPrice: event.ticketPrice
    });
  };

  const cancelEdit = () => {
    setEditingEventId(null);
    setEditForm({});
  };

  const handleEditChange = (e) => {
    setEditForm({ ...editForm, [e.target.name]: e.target.value });
  };

  const saveEdit = async (eventId) => {
    try {
      const payload = {
        ...editForm,
        totalTickets: parseInt(editForm.totalTickets),
        ticketPrice: parseFloat(editForm.ticketPrice),
        dateTime: new Date(editForm.dateTime).toISOString()
      };
      await axios.put(`/events/${eventId}`, payload);
      setEditingEventId(null);
      setEditForm({});
      showToast('Événement modifié avec succès', 'success');
      await fetchEvents();
    } catch (err) {
      const errorMsg = err.response?.data?.message || 'Échec de la modification';
      showToast(errorMsg, 'error');
    }
  };

  if (loading) return <div className="card">Chargement...</div>;
  if (error) return <div className="card error">{error}</div>;

  return (
    <div>
      <h2 className="page-title">Événements</h2>
      <div className="event-grid">
        {events.map(event => {
          const isOrganizer = event.organizerUsername === currentUsername;
          const isEditing = editingEventId === event.id;

          return (
            <div key={event.id} className="event-card">
              {isEditing ? (
                <>
                  <div className="form-group">
                    <label>Titre</label>
                    <input
                      type="text"
                      name="title"
                      value={editForm.title || ''}
                      onChange={handleEditChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Description</label>
                    <textarea
                      name="description"
                      value={editForm.description || ''}
                      onChange={handleEditChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Date et Heure</label>
                    <input
                      type="datetime-local"
                      name="dateTime"
                      value={editForm.dateTime ? editForm.dateTime.substring(0, 16) : ''}
                      onChange={handleEditChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Lieu</label>
                    <input
                      type="text"
                      name="location"
                      value={editForm.location || ''}
                      onChange={handleEditChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Nombre total de tickets</label>
                    <input
                      type="number"
                      name="totalTickets"
                      value={editForm.totalTickets || ''}
                      onChange={handleEditChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Prix du ticket</label>
                    <input
                      type="number"
                      step="0.01"
                      name="ticketPrice"
                      value={editForm.ticketPrice || ''}
                      onChange={handleEditChange}
                    />
                  </div>
                  <button
                    onClick={() => saveEdit(event.id)}
                    className="btn btn-primary"
                    style={{ marginRight: '10px' }}
                  >
                    Enregistrer
                  </button>
                  <button onClick={cancelEdit} className="btn btn-danger">
                    Annuler
                  </button>
                </>
              ) : (
                <>
                  <h3>{event.title}</h3>
                  <p><strong>Description :</strong> {event.description}</p>
                  <p><strong>Date :</strong> {new Date(event.dateTime).toLocaleString('fr-FR')}</p>
                  <p><strong>Lieu :</strong> {event.location}</p>
                  <p><strong>Prix :</strong> {event.ticketPrice} MAD</p>
                  <p><strong>Tickets disponibles :</strong> {event.availableTickets} / {event.totalTickets}</p>
                  <p><strong>Organisateur :</strong> {event.organizerUsername}</p>
                  <div className="event-card-footer">
                    {event.availableTickets > 0 ? (
                      <div className="event-actions">
                        <div className="quantity-wrapper">
                          <label style={{ fontSize: '12px', marginBottom: '4px', display: 'block', color: 'var(--text-secondary)' }}>Quantité</label>
                          <input
                            type="number"
                            min="1"
                            max="4"
                            defaultValue="1"
                            id={`quantity-${event.id}`}
                            className="quantity-input"
                          />
                        </div>
                        <button
                          onClick={() => {
                            const quantity = parseInt(document.getElementById(`quantity-${event.id}`).value);
                            handleReserve(event.id, quantity);
                          }}
                          className="btn btn-success"
                          style={{ flex: 1 }}
                        >
                          🎫 Réserver
                        </button>
                      </div>
                    ) : (
                      <div className="sold-out-badge">
                        <span className="error">❌ Complet</span>
                      </div>
                    )}
                    {isOrganizer && (
                      <div className="event-admin-actions">
                        <button
                          onClick={() => startEdit(event)}
                          className="btn btn-secondary"
                        >
                          ✏️ Modifier
                        </button>
                        <button
                          onClick={() => handleDelete(event.id)}
                          className="btn btn-danger"
                        >
                          🗑️ Supprimer
                        </button>
                      </div>
                    )}
                  </div>
                </>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default EventList;
