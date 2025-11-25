import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Products.css';
import { productAPI, categoryAPI, cartAPI } from '../services/api';

function Products() {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, selectedCategory]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [productsResponse, categoriesResponse] = await Promise.all([
        productAPI.getAllProducts({ page: currentPage, size: 12 }),
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
    <div className="products-page">
      <div className="products-header">
        <h2>Tüm Ürünler</h2>
        <p>Kategorilere göre filtreleyin ve alışverişe başlayın</p>
        {error && <div className="error-message">⚠️ {error}</div>}
        {loading && <div className="loading-indicator">🔄 Yükleniyor...</div>}
        {message && (
          <div className={`message ${message.type}`}>
            {message.text}
          </div>
        )}
      </div>

      <div className="products-container">
        <aside className="categories-sidebar">
          <h3>Kategoriler</h3>
          <ul>
            <li
              className={selectedCategory === null ? 'active' : ''}
              onClick={() => setSelectedCategory(null)}
            >
              Tüm Ürünler
            </li>
            {categories.map((category) => (
              <li
                key={category.categoryId}
                className={selectedCategory === category.categoryId ? 'active' : ''}
                onClick={() => setSelectedCategory(category.categoryId)}
              >
                {category.categoryName}
              </li>
            ))}
          </ul>
        </aside>

        <main className="products-content">
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
                      {product.stock === 0 ? 'Stokta Yok' : '🛒 Sepete Ekle'}
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
        </main>
      </div>
    </div>
  );
}

export default Products;

