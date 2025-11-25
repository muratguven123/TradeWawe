import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Address.css';
import { addressAPI } from '../services/api';

function Address() {
  const navigate = useNavigate();
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    title: '',
    street: '',
    city: '',
    district: '',
    postalCode: '',
    country: 'Türkiye',
    default: false
  });

  useEffect(() => {
    const userId = localStorage.getItem('userId');
    if (!userId) {
      navigate('/login');
      return;
    }
    fetchAddresses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetchAddresses = async () => {
    try {
      setLoading(true);
      const response = await addressAPI.getAllAddresses();
      if (response.data) {
        setAddresses(response.data);
      }
      setError(null);
    } catch (err) {
      console.error('Adresler yüklenemedi:', err);
      setError('Adresler yüklenirken hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await addressAPI.saveAddress(formData);
      setMessage({ type: 'success', text: '✅ Adres başarıyla kaydedildi' });
      setShowForm(false);
      setFormData({
        name: '',
        title: '',
        street: '',
        city: '',
        district: '',
        postalCode: '',
        country: 'Türkiye',
        default: false
      });
      fetchAddresses();
      setTimeout(() => setMessage(null), 3000);
    } catch (err) {
      console.error('Adres kaydedilemedi:', err);
      setMessage({ type: 'error', text: '❌ Adres kaydedilemedi' });
      setTimeout(() => setMessage(null), 3000);
    }
  };

  const handleDelete = async (address) => {
    if (!window.confirm('Bu adresi silmek istediğinizden emin misiniz?')) {
      return;
    }

    try {
      await addressAPI.deleteAddress({
        name: address.name,
        title: address.title,
        street: address.street,
        city: address.city
      });
      setMessage({ type: 'success', text: '✅ Adres silindi' });
      fetchAddresses();
      setTimeout(() => setMessage(null), 3000);
    } catch (err) {
      console.error('Adres silinemedi:', err);
      setMessage({ type: 'error', text: '❌ Adres silinemedi' });
      setTimeout(() => setMessage(null), 3000);
    }
  };

  if (loading) {
    return (
      <div className="address-container">
        <div className="loading-indicator">🔄 Adresler yükleniyor...</div>
      </div>
    );
  }

  return (
    <div className="address-container">
      <div className="address-header">
        <h2>📍 Adreslerim</h2>
        {error && <div className="error-message">⚠️ {error}</div>}
        {message && (
          <div className={`message ${message.type}`}>
            {message.text}
          </div>
        )}
        <button onClick={() => setShowForm(!showForm)} className="btn-add">
          {showForm ? 'İptal' : '+ Yeni Adres Ekle'}
        </button>
      </div>

      {showForm && (
        <div className="address-form">
          <h3>Yeni Adres</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label>Ad Soyad *</label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Adres Başlığı *</label>
                <input
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  placeholder="Ev, İş, vb."
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label>Adres *</label>
              <textarea
                name="street"
                value={formData.street}
                onChange={handleChange}
                rows="3"
                required
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Şehir *</label>
                <input
                  type="text"
                  name="city"
                  value={formData.city}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>İlçe</label>
                <input
                  type="text"
                  name="district"
                  value={formData.district}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Posta Kodu</label>
                <input
                  type="text"
                  name="postalCode"
                  value={formData.postalCode}
                  onChange={handleChange}
                />
              </div>
              <div className="form-group">
                <label>Ülke</label>
                <input
                  type="text"
                  name="country"
                  value={formData.country}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-group checkbox-group">
              <label>
                <input
                  type="checkbox"
                  name="default"
                  checked={formData.default}
                  onChange={handleChange}
                />
                Varsayılan adres olarak ayarla
              </label>
            </div>

            <button type="submit" className="btn-submit">Adresi Kaydet</button>
          </form>
        </div>
      )}

      <div className="address-list">
        {addresses.length === 0 ? (
          <div className="empty-addresses">
            <p>Henüz kayıtlı adresiniz bulunmuyor</p>
          </div>
        ) : (
          addresses.map((address) => (
            <div key={address.id} className="address-card">
              {address.default && <span className="default-badge">Varsayılan</span>}
              <h3>{address.title}</h3>
              <p><strong>{address.name}</strong></p>
              <p>{address.street}</p>
              <p>{address.discrict} / {address.city}</p>
              {address.postalCode && <p>Posta Kodu: {address.postalCode}</p>}
              <p>{address.country}</p>
              <div className="address-actions">
                <button onClick={() => handleDelete(address)} className="btn-delete">
                  🗑️ Sil
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default Address;

