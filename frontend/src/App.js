import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import axios from 'axios';
import Login from './components/Login';
import Register from './components/Register';
import EventList from './components/EventList';
import CreateEvent from './components/CreateEvent';
import MyReservations from './components/MyReservations';
import AdminDashboard from './components/AdminDashboard';
import ToastContainer from './components/ToastContainer';
import NotificationPanel from './components/NotificationPanel';
import { useToast } from './hooks/useToast';
import './App.css';

axios.defaults.baseURL = 'http://localhost:8080/api';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const { toasts, showToast, removeToast } = useToast();

  useEffect(() => {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    const email = localStorage.getItem('email');
    const rolesRaw = localStorage.getItem('roles');
    if (token && username) {
      const roles = rolesRaw ? JSON.parse(rolesRaw) : [];
      setIsAuthenticated(true);
      setUser({ username, email, roles });
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
  }, []);

  const handleLogin = (token, username, email, roles) => {
    localStorage.setItem('token', token);
    localStorage.setItem('username', username);
    localStorage.setItem('email', email);
    localStorage.setItem('roles', JSON.stringify(roles || []));
    setIsAuthenticated(true);
    setUser({ username, email, roles: roles || [] });
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    localStorage.removeItem('roles');
    setIsAuthenticated(false);
    setUser(null);
    delete axios.defaults.headers.common['Authorization'];
    showToast('Déconnexion réussie', 'success');
  };

  const isAdmin = user?.roles?.includes('ROLE_ADMIN');
  const isOrganizer = user?.roles?.includes('ROLE_ORGANIZER') || isAdmin;

  return (
    <Router>
      <div className="App">
        <ToastContainer toasts={toasts} removeToast={removeToast} />
        <header className="header">
          <div className="header-left">
            <h1>Événia</h1>
            {isAuthenticated && (
              <span className="header-subtitle">
                Plateforme complète pour planifier, organiser et gérer efficacement matchs sportifs, concerts et conférences.
              </span>
            )}
          </div>
          <nav className="nav">
            {isAuthenticated ? (
              <>
                <Link to="/events">Événements</Link>
                {isOrganizer && <Link to="/create-event">Créer un Événement</Link>}
                <Link to="/my-reservations">Mes Réservations</Link>
                {isAdmin && <Link to="/admin">Tableau de Bord Admin</Link>}
                <NotificationPanel />
                <span className="nav-user">Bienvenue, {user?.username}</span>
                <button onClick={handleLogout} className="btn btn-danger">Déconnexion</button>
              </>
            ) : (
              <>
                <Link to="/login">Connexion</Link>
                <Link to="/register">Inscription</Link>
              </>
            )}
          </nav>
        </header>

        <div className="container">
          <Routes>
            <Route path="/login" element={
              isAuthenticated ? <Navigate to="/events" /> : <Login onLogin={handleLogin} showToast={showToast} />
            } />
            <Route path="/register" element={
              isAuthenticated ? <Navigate to="/events" /> : <Register onLogin={handleLogin} showToast={showToast} />
            } />
            <Route path="/events" element={
              isAuthenticated ? <EventList showToast={showToast} /> : <Navigate to="/login" />
            } />
            <Route path="/create-event" element={
              isAuthenticated && isOrganizer ? <CreateEvent showToast={showToast} /> : <Navigate to="/events" />
            } />
            <Route path="/my-reservations" element={
              isAuthenticated ? <MyReservations showToast={showToast} /> : <Navigate to="/login" />
            } />
            <Route path="/admin" element={
              isAuthenticated && isAdmin ? <AdminDashboard showToast={showToast} /> : <Navigate to="/events" />
            } />
            <Route path="/" element={
              isAuthenticated ? <Navigate to="/events" /> : <Navigate to="/login" />
            } />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;

