import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import HomePage from './pages/HomePage';
import AdminPage from './pages/AdminPage';
import './App.css';

function Navbar() {
  return (
    <nav className="navbar">
      <Link to="/" className="nav-brand">MSOE Exam Scheduler</Link>
      <div className="nav-links">
        <Link to="/">Home</Link>
      </div>
    </nav>
  );
}

function NotFound() {
  return (
    <div className="page" style={{ textAlign: 'center', paddingTop: '4rem' }}>
      <h2>404 — Page Not Found</h2>
      <Link to="/">Go Home</Link>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/admin" element={<AdminPage />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}

export default App;
