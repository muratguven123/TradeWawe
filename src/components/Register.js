import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Auth.css';
import { authAPI } from '../services/api';

function Register() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'USER'
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

    if (formData.password !== formData.confirmPassword) {
      setError('Şifreler eşleşmiyor!');
      setLoading(false);
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError('Geçerli bir email adresi girin');
      setLoading(false);
      return;
    }

    try {
      const { confirmPassword, ...registerData } = formData;
      const response = await authAPI.register(registerData);

      if (response.data) {
        const { token, id, name } = response.data;

        if (token) {
          localStorage.setItem('token', token);
        }
        if (id) {
          localStorage.setItem('userId', id);
        }
        if (name) {
          localStorage.setItem('userName', name);
        }

        window.dispatchEvent(new Event('loginStatusChanged'));
        navigate('/');
      }
    } catch (err) {
      console.error('Kayıt hatası:', err);

      if (err.response?.status === 409) {
        setError('Bu email adresi zaten kayıtlı');
      } else if (err.response?.status === 400) {
        setError('Geçersiz kullanıcı bilgileri');
      } else {
        setError(err.response?.data?.message || 'Kayıt oluşturulamadı. Lütfen tekrar deneyin.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Kayıt Ol</h2>
        <p className="auth-subtitle">TradeWave'e katılın</p>

        {error && <div className="error-message">⚠️ {error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Ad Soyad *</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="Adınız Soyadınız"
              required
            />
          </div>

          <div className="form-group">
            <label>Email *</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="ornek@email.com"
              required
            />
          </div>

          <div className="form-group">
            <label>Şifre *</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="En az 6 karakter"
              required
              minLength="6"
            />
          </div>

          <div className="form-group">
            <label>Şifre Tekrar *</label>
            <input
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="Şifrenizi tekrar girin"
              required
            />
          </div>

          <button type="submit" className="btn-submit" disabled={loading}>
            {loading ? 'Kayıt yapılıyor...' : 'Kayıt Ol'}
          </button>
        </form>

        <div className="auth-footer">
          <p>Zaten hesabınız var mı? <span onClick={() => navigate('/login')} className="auth-link">Giriş Yap</span></p>
        </div>
      </div>
    </div>
  );
}

export default Register;

