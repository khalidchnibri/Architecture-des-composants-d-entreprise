import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function Register({ onLogin, showToast }) {
  const [formData, setFormData] = useState({ username: '', email: '', password: '', role: 'USER' });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const response = await axios.post('/auth/register', formData);
      onLogin(response.data.token, response.data.username, response.data.email, response.data.roles);
      showToast('Inscription réussie ! Bienvenue !', 'success');
      navigate('/events');
    } catch (err) {
      const errorMsg = err.response?.data?.message || 'Échec de l\'inscription';
      setError(errorMsg);
      showToast(errorMsg, 'error');
    }
  };

  return (
    <div className="card">
      <h2>Inscription</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Nom d'utilisateur</label>
          <input
            type="text"
            name="username"
            value={formData.username}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Mot de passe</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Type de compte</label>
          <select
            name="role"
            value={formData.role}
            onChange={handleChange}
            required
            style={{
              width: '100%',
              padding: '12px',
              borderRadius: '8px',
              border: '1px solid rgba(255, 255, 255, 0.2)',
              background: 'rgba(255, 255, 255, 0.9)',
              color: '#1e293b',
              fontSize: '16px',
              outline: 'none',
              transition: 'all 0.3s ease'
            }}
          >
            <option value="USER" style={{ color: '#1e293b' }}>Utilisateur</option>
            <option value="ORGANIZER" style={{ color: '#1e293b' }}>Organisateur</option>
          </select>
        </div>
        {error && <div className="error">{error}</div>}
        <div style={{ marginTop: '30px' }}>
          <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
            ✨ S'inscrire
          </button>
        </div>
      </form>
    </div>
  );
}

export default Register;

