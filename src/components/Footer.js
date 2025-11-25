import React from 'react';
import './Footer.css';

function Footer() {
  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-section">
          <h3>TradeWave</h3>
          <p>Güvenli ve hızlı kripto para ticaret platformu</p>
          <div className="social-links">
            <a href="#" title="Twitter">🐦</a>
            <a href="#" title="Telegram">💬</a>
            <a href="#" title="Discord">🎮</a>
            <a href="#" title="GitHub">💻</a>
          </div>
        </div>
        <div className="footer-section">
          <h4>Kategoriler</h4>
          <ul>
            <li><a href="#">Elektronik</a></li>
            <li><a href="#">Giyim</a></li>
            <li><a href="#">Ev & Yaşam</a></li>
            <li><a href="#">Spor & Outdoor</a></li>
          </ul>
        </div>
        <div className="footer-section">
          <h4>Destek</h4>
          <ul>
            <li><a href="#">Yardım Merkezi</a></li>
            <li><a href="#">API Dökümanları</a></li>
            <li><a href="#">İşlem Ücretleri</a></li>
            <li><a href="#">İletişim</a></li>
          </ul>
        </div>
        <div className="footer-section">
          <h4>Hakkımızda</h4>
          <ul>
            <li><a href="#">Hakkımızda</a></li>
            <li><a href="#">Kariyer</a></li>
            <li><a href="#">Gizlilik Politikası</a></li>
            <li><a href="#">Kullanım Şartları</a></li>
          </ul>
        </div>
      </div>
      <div className="footer-bottom">
        <p>&copy; 2025 TradeWave. Tüm hakları saklıdır.</p>
      </div>
    </footer>
  );
}

export default Footer;

