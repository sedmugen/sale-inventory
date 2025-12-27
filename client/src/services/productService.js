// File: src/services/productService.js
import api from './api';

/**
 * Product API service
 * All product-related API calls
 */
const productService = {
    /**
     * Get all products
     */
    getAllProducts: async () => {
        const response = await api.get('/products');
        return response.data;
    },

    /**
     * Get product by ID
     */
    getProductById: async (id) => {
        const response = await api.get(`/products/${id}`);
        return response.data;
    },

    /**
     * Get product by code
     */
    getProductByCode: async (code) => {
        const response = await api.get(`/products/code/${code}`);
        return response.data;
    },

    /**
     * Create new product
     */
    createProduct: async (productData) => {
        const response = await api.post('/products', productData);
        return response.data;
    },

    /**
     * Update product
     */
    updateProduct: async (id, productData) => {
        const response = await api.put(`/products/${id}`, productData);
        return response.data;
    },

    /**
     * Delete product
     */
    deleteProduct: async (id) => {
        const response = await api.delete(`/products/${id}`);
        return response.data;
    },

    /**
     * Get active products only
     */
    getActiveProducts: async () => {
        const response = await api.get('/products/active');
        return response.data;
    },

    /**
     * Search products by name
     */
    searchProducts: async (name) => {
        const response = await api.get('/products/search', {
            params: { name },
        });
        return response.data;
    },

    /**
     * Get low stock products
     */
    getLowStockProducts: async (limit = 5) => {
        const response = await api.get('/products/low-stock', {
            params: { limit },
        });
        return response.data;
    },
};

export default productService;