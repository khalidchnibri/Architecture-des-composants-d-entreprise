import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './CreateEvent.css';

function CreateEvent({ showToast }) {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    dateTime: '',
    location: '',
    totalTickets: '',
    ticketPrice: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const eventData = {
        ...formData,
        totalTickets: parseInt(formData.totalTickets),
        ticketPrice: parseFloat(formData.ticketPrice),
        dateTime: new Date(formData.dateTime).toISOString()
      };
      await axios.post('/events', eventData);
      showToast('Événement créé avec succès !', 'success');
      navigate('/events');
    } catch (err) {
      const errorMsg = err.response?.data?.message || 'Échec de la création de l\'événement';
      setError(errorMsg);
      showToast(errorMsg, 'error');
    }
  };

  return (
    <div className="card create-event-card">
      <h2>Créer un Événement</h2>
      <form onSubmit={handleSubmit} className="create-event-form">
        <div className="form-row">
          <div className="form-group">
            <label>Titre</label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label>Lieu</label>
            <input
              type="text"
              name="location"
              value={formData.location}
              onChange={handleChange}
              required
            />
          </div>
        </div>
        <div className="form-group">
          <label>Description</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            required
            rows="3"
          />
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Date et Heure</label>
            <input
              type="datetime-local"
              name="dateTime"
              value={formData.dateTime}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label>Nombre total de tickets</label>
            <input
              type="number"
              name="totalTickets"
              value={formData.totalTickets}
              onChange={handleChange}
              required
              min="1"
            />
          </div>
          <div className="form-group">
            <label>Prix du ticket (MAD)</label>
            <input
              type="number"
              step="0.01"
              name="ticketPrice"
              value={formData.ticketPrice}
              onChange={handleChange}
              required
              min="0"
            />
          </div>
        </div>
        {error && <div className="error">{error}</div>}
        <div className="form-actions">
          <button 
            type="button" 
            onClick={() => navigate('/events')} 
            className="btn btn-secondary"
          >
            Annuler
          </button>
          <button type="submit" className="btn btn-primary">
            ✨ Créer l'Événement
          </button>
        </div>
      </form>
    </div>
  );
}

export default CreateEvent;
