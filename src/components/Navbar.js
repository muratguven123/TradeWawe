import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';

function Navbar() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // İlk yüklemede kontrol et
    checkLoginStatus();

    // Storage değişikliklerini dinle
    window.addEventListener('storage', checkLoginStatus);
    window.addEventListener('loginStatusChanged', checkLoginStatus);

    return () => {
      window.removeEventListener('storage', checkLoginStatus);
      window.removeEventListener('loginStatusChanged', checkLoginStatus);
    };
  }, []);

  const checkLoginStatus = () => {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    const isAuthenticated = !!(token || userId);

    setIsLoggedIn(isAuthenticated);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    window.dispatchEvent(new Event('loginStatusChanged'));
    setIsLoggedIn(false);
    navigate('/');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <Link to="/">
            <h1>TradeWave</h1>
          </Link>
          <span className="beta-badge">BETA</span>
        </div>
        <ul className="navbar-menu">
          <li><Link to="/">Ana Sayfa</Link></li>
          <li><Link to="/products">Ürünler</Link></li>
          <li><Link to="/cart">🛒 Sepetim</Link></li>
          <li><Link to="/orders">📦 Siparişlerim</Link></li>
          <li><Link to="/address">📍 Adreslerim</Link></li>
        </ul>
        <div className="navbar-actions">
          {isLoggedIn ? (
            <button className="btn-logout" onClick={handleLogout}>Çıkış Yap</button>
          ) : (
            <>
              <Link to="/login">
                <button className="btn-login">Giriş Yap</button>
              </Link>
              <Link to="/register">
                <button className="btn-register">Kayıt Ol</button>
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;

