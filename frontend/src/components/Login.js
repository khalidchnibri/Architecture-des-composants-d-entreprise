import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function Login({ onLogin, showToast }) {
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const response = await axios.post('/auth/login', formData);
      onLogin(response.data.token, response.data.username, response.data.email, response.data.roles);
      showToast('Connexion réussie !', 'success');
      navigate('/events');
    } catch (err) {
      const errorMsg = err.response?.data?.message || 'Échec de la connexion';
      setError(errorMsg);
      showToast(errorMsg, 'error');
    }
  };

  return (
    <div className="card">
      <h2>Connexion</h2>
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
          <label>Mot de passe</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
          />
        </div>
        {error && <div className="error">{error}</div>}
        <div style={{ marginTop: '30px' }}>
          <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
            🔐 Se connecter
          </button>
        </div>
      </form>
    </div>
  );
}

export default Login;

