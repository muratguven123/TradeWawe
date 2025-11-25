import axios from 'axios';

// Backend API URL - OpenAPI dokümantasyonuna göre port 8081
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - token ekleme
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - hata yönetimi
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
      localStorage.removeItem('userName');
      localStorage.removeItem('userEmail');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API - User Management
export const authAPI = {
  register: (userData) => api.post('/user/register', userData),
  login: (credentials) => api.post('/user/login', credentials),
};

// Seller API
export const sellerAPI = {
  register: (sellerData) => api.post('/sellers/register', sellerData),
  login: (credentials) => api.post('/sellers/login', credentials),
  getSellerById: (id) => api.get(`/sellers/${id}`),
};

// Product API
export const productAPI = {
  getAllProducts: (params = {}) => {
    const { page = 0, size = 6, sort = 'createdAt', direction = 'desc' } = params;
    return api.get('/products/all', {
      params: { page, size, sort, direction }
    });
  },
  getProduct: (id) => api.get(`/products/${id}`),
  createProduct: (productData) => api.post('/products/create', productData),
  updateProduct: (id, productData) => api.put(`/products/update/${id}`, productData),
  deleteProduct: (id) => api.delete(`/products/delete/${id}`),
};

// Category API
export const categoryAPI = {
  getAllCategories: () => api.get('/category'),
  getCategoryById: (id) => api.get(`/category/${id}`),
  addCategory: (categoryData) => api.post('/category', categoryData),
  updateCategory: (id, categoryData) => api.put(`/category/${id}`, categoryData),
};

// Cart API
export const cartAPI = {
  viewCart: (userId) => api.get('/cart/view', { params: { userId } }),
  addToCart: (cartData) => api.post('/cart/add', cartData),
  removeFromCart: (cartData) => api.delete('/cart/remove', { data: cartData }),
  checkout: (userId) => api.post('/cart/checkout', null, { params: { userId } }),
};

// Order API
export const orderAPI = {
  createOrder: (orderData) => api.post('/orders/create', orderData),
  getOrderDetail: (id) => api.get(`/orders/${id}`),
};

// Payment API
export const paymentAPI = {
  sendPayment: (paymentData) => api.post('/payment/send', paymentData),
  getPaymentHistory: () => api.get('/payment/history'),
};

// Address API
export const addressAPI = {
  getAllAddresses: () => api.get('/address'),
  getAddressById: (id) => api.get(`/address/address/${id}`),
  saveAddress: (addressData) => api.post('/address/save', addressData),
  updateAddress: (addressData) => api.put('/address/update', addressData),
  deleteAddress: (addressData) => api.delete('/address/delete', { data: addressData }),
};

// Admin API
export const adminAPI = {
  getAllUsers: () => api.get('/admin/users'),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),
  changeUserRole: (id, newRole) => api.put(`/admin/users/${id}/role`, null, { params: { newRole } }),
  deleteAnyProduct: (id) => api.delete(`/admin/products/${id}`),
};

export default api;

