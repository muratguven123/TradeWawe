import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Auth.css';
import { authAPI } from '../services/api';

function Login() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      // Backend API: POST /user/login
      // Request: UserLogRequest { email, password }
      // Response: User object
      const response = await authAPI.login(formData);

      if (response.data) {
        const userData = response.data;

        // Token varsa localStorage'a kaydet (JWT token)
        if (userData.token) {
          localStorage.setItem('token', userData.token);
        }

        // Kullanıcı bilgilerini sakla
        if (userData.username) {
          localStorage.setItem('userName', userData.username);
          localStorage.setItem('userId', userData.username); // Backend'de User nesnesinde username var
        }

        localStorage.setItem('userEmail', formData.email);

        // Login durumunu güncelle
        window.dispatchEvent(new Event('loginStatusChanged'));

        navigate('/');
      }
    } catch (err) {
      console.error('Giriş hatası:', err);

      // Backend'den gelen hata mesajlarını göster
      if (err.response?.status === 401) {
        setError('Geçersiz email veya şifre');
      } else {
        setError(err.response?.data?.message || 'Giriş yapılamadı. Lütfen tekrar deneyin.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>🔐 Giriş Yap</h2>
        <p className="auth-subtitle">TradeWave hesabınıza giriş yapın</p>

        {error && <div className="error-message">⚠️ {error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Email *</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              placeholder="ornek@email.com"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Şifre *</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              placeholder="Şifrenizi girin"
              disabled={loading}
              minLength="6"
            />
          </div>

          <button type="submit" className="btn-submit" disabled={loading}>
            {loading ? '⏳ Giriş yapılıyor...' : '✓ Giriş Yap'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Hesabınız yok mu?{' '}
            <span onClick={() => navigate('/register')} className="auth-link">
              Kayıt Ol
            </span>
          </p>
        </div>
      </div>
    </div>
  );
}

export default Login;

