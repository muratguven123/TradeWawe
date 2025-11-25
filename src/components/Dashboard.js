import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';
import { productAPI, categoryAPI, cartAPI } from '../services/api';

function Dashboard() {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [productsResponse, categoriesResponse] = await Promise.all([
        productAPI.getAllProducts({ page: currentPage, size: 6 }),
        categoryAPI.getAllCategories()
      ]);

      if (productsResponse.data) {
        setProducts(productsResponse.data.content || []);
        setTotalPages(productsResponse.data.totalPages || 0);
      }

      if (categoriesResponse.data) {
        setCategories(categoriesResponse.data);
      }

      setError(null);
    } catch (err) {
      console.error('Veri alınamadı:', err);
      setError('Ürünler yüklenirken bir hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  const [message, setMessage] = useState(null);

  const handleAddToCart = async (productId) => {
    try {
      const userId = localStorage.getItem('userId');
      if (!userId) {
        setMessage({
          type: 'error',
          text: '⚠️ Lütfen önce giriş yapın'
        });
        setTimeout(() => {
          navigate('/login');
        }, 1500);
        return;
      }

      // AddToCartRequest şemasına göre: userId, productId, quantity (min: 1)
      const response = await cartAPI.addToCart({
        userId: parseInt(userId),
        productId: productId,
        quantity: 1
      });

      if (response.status === 200 || response.status === 201) {
        setMessage({
          type: 'success',
          text: '✅ Ürün sepete eklendi!'
        });
        setTimeout(() => setMessage(null), 3000);
      }
    } catch (err) {
      console.error('Sepete eklenemedi:', err);
      setMessage({
        type: 'error',
        text: '❌ Ürün sepete eklenemedi'
      });
      setTimeout(() => setMessage(null), 3000);
    }
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h2>Hoş Geldiniz - TradeWave E-Ticaret</h2>
        <p>Kaliteli ürünleri keşfedin</p>
        {error && <div className="error-message">⚠️ {error}</div>}
        {loading && <div className="loading-indicator">🔄 Yükleniyor...</div>}
      </div>

      <div className="categories-section">
        <h3>Kategoriler</h3>
        <div className="categories-grid">
          {categories.map((category) => (
            <div key={category.categoryId} className="category-card">
              <span>{category.categoryName}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="products-section">
        <h3>Öne Çıkan Ürünler</h3>
        <div className="products-grid">
          {products.map((product) => (
            <div key={product.id} className="product-card">
              <div className="product-image">
                <img src={`https://via.placeholder.com/300x200?text=${product.name}`} alt={product.name} />
                {product.stock <= 10 && product.stock > 0 && (
                  <span className="stock-badge low">Son {product.stock} adet!</span>
                )}
                {product.stock === 0 && (
                  <span className="stock-badge out">Stokta Yok</span>
                )}
              </div>
              <div className="product-info">
                <h4>{product.name}</h4>
                <p className="product-description">{product.description}</p>
                <div className="product-footer">
                  <span className="product-price">{product.price} ₺</span>
                  <button
                    className="add-to-cart-btn"
                    onClick={() => handleAddToCart(product.id)}
                    disabled={product.stock === 0}
                  >
                    {product.stock === 0 ? 'Stokta Yok' : 'Sepete Ekle'}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        {totalPages > 1 && (
          <div className="pagination">
            <button
              onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
              disabled={currentPage === 0}
            >
              ← Önceki
            </button>
            <span>Sayfa {currentPage + 1} / {totalPages}</span>
            <button
              onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
              disabled={currentPage >= totalPages - 1}
            >
              Sonraki →
            </button>
          </div>
        )}
      </div>

      <div className="info-section">
        <div className="info-card">
          <h3>🚚 Hızlı Kargo</h3>
          <p>Tüm siparişlerinizde ücretsiz kargo imkanı</p>
        </div>
        <div className="info-card">
          <h3>💳 Güvenli Ödeme</h3>
          <p>Kredi kartı ve havale ile güvenli ödeme</p>
        </div>
        <div className="info-card">
          <h3>🔄 Kolay İade</h3>
          <p>14 gün içinde ücretsiz iade</p>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;

