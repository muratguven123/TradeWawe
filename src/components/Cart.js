import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Cart.css';
import { cartAPI, productAPI } from '../services/api';

function Cart() {
  const navigate = useNavigate();
  const [cart, setCart] = useState(null);
  const [products, setProducts] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    fetchCart();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const fetchCart = async () => {
    try {
      setLoading(true);
      const userId = localStorage.getItem('userId');

      if (!userId) {
        setError('Lütfen önce giriş yapın');
        navigate('/login');
        return;
      }

      const response = await cartAPI.viewCart(userId);

      if (response.data) {
        setCart(response.data);

        // Sepetteki ürünlerin detaylarını al
        if (response.data.items && response.data.items.length > 0) {
          await fetchProductDetails(response.data.items);
        }
      }
      setError(null);
    } catch (err) {
      console.error('Sepet yüklenemedi:', err);

      // 500 hatası veya sepet bulunamadı - boş sepet olarak göster
      if (err.response?.status === 500 || err.response?.status === 404) {
        setCart({ id: null, userId: localStorage.getItem('userId'), items: [], checkedout: false });
        setError(null);
      } else {
        setError('Sepet yüklenirken bir hata oluştu');
      }
    } finally {
      setLoading(false);
    }
  };

  const fetchProductDetails = async (cartItems) => {
    try {
      const productPromises = cartItems.map(item =>
        productAPI.getProduct(item.productId).catch(err => {
          console.error(`Ürün ${item.productId} bulunamadı:`, err);
          return null;
        })
      );

      const productResponses = await Promise.all(productPromises);
      const productMap = {};

      productResponses.forEach((response, index) => {
        if (response && response.data) {
          productMap[cartItems[index].productId] = response.data;
        }
      });

      setProducts(productMap);
    } catch (err) {
      console.error('Ürün detayları alınamadı:', err);
    }
  };

  const handleRemoveItem = async (productId) => {
    try {
      const userId = localStorage.getItem('userId');

      // RemoveCartRequest şemasına göre
      await cartAPI.removeFromCart({
        userId: parseInt(userId),
        productId: productId
      });

      setMessage({ type: 'success', text: '✅ Ürün sepetten kaldırıldı' });
      setTimeout(() => setMessage(null), 3000);
      fetchCart();
    } catch (err) {
      console.error('Ürün kaldırılamadı:', err);
      setMessage({ type: 'error', text: '❌ Ürün kaldırılamadı' });
      setTimeout(() => setMessage(null), 3000);
    }
  };

  const handleCheckout = async () => {
    try {
      const userId = localStorage.getItem('userId');

      if (!cart || !cart.items || cart.items.length === 0) {
        setMessage({ type: 'error', text: '⚠️ Sepetiniz boş' });
        return;
      }

      await cartAPI.checkout(parseInt(userId));

      setMessage({ type: 'success', text: '✅ Sipariş başarıyla oluşturuldu!' });

      setTimeout(() => {
        navigate('/');
      }, 2000);
    } catch (err) {
      console.error('Sipariş oluşturulamadı:', err);
      setMessage({ type: 'error', text: '❌ Sipariş oluşturulamadı' });
      setTimeout(() => setMessage(null), 3000);
    }
  };

  const calculateTotal = () => {
    if (!cart || !cart.items) return 0;

    return cart.items.reduce((total, item) => {
      const product = products[item.productId];
      if (product) {
        return total + (product.price * item.quantity);
      }
      return total;
    }, 0);
  };

  if (loading) {
    return (
      <div className="cart-container">
        <div className="loading-indicator">🔄 Sepet yükleniyor...</div>
      </div>
    );
  }

  return (
    <div className="cart-container">
      <div className="cart-header">
        <h2>🛒 Alışveriş Sepetim</h2>
        {error && <div className="error-message">⚠️ {error}</div>}
        {message && (
          <div className={`message ${message.type}`}>
            {message.text}
          </div>
        )}
      </div>

      {!cart || !cart.items || cart.items.length === 0 ? (
        <div className="empty-cart">
          <p>Sepetiniz boş</p>
          <button onClick={() => navigate('/products')} className="btn-primary">
            Alışverişe Başla
          </button>
        </div>
      ) : (
        <>
          <div className="cart-items">
            {cart.items.map((item) => {
              const product = products[item.productId];
              return (
                <div key={item.id} className="cart-item">
                  <div className="item-info">
                    <h3>{product?.name || `Ürün #${item.productId}`}</h3>
                    <p>{product?.description || ''}</p>
                  </div>
                  <div className="item-details">
                    <span className="item-price">
                      {product?.price ? `${product.price.toFixed(2)} TL` : '-'}
                    </span>
                    <span className="item-quantity">Adet: {item.quantity}</span>
                    <span className="item-total">
                      Toplam: {product?.price ? `${(product.price * item.quantity).toFixed(2)} TL` : '-'}
                    </span>
                  </div>
                  <button
                    onClick={() => handleRemoveItem(item.productId)}
                    className="btn-remove"
                  >
                    🗑️ Kaldır
                  </button>
                </div>
              );
            })}
          </div>

          <div className="cart-summary">
            <div className="summary-row">
              <span>Ara Toplam:</span>
              <span>{calculateTotal().toFixed(2)} TL</span>
            </div>
            <div className="summary-row">
              <span>Kargo:</span>
              <span>Ücretsiz</span>
            </div>
            <div className="summary-row total">
              <span>Genel Toplam:</span>
              <span>{calculateTotal().toFixed(2)} TL</span>
            </div>
            <button onClick={handleCheckout} className="btn-checkout">
              Siparişi Tamamla
            </button>
          </div>
        </>
      )}
    </div>
  );
}

export default Cart;

