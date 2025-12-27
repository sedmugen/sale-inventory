// File: src/services/purchaseService.js
import api from './api';

/**
 * Purchase API service
 */
const purchaseService = {
    /**
     * Get all purchases
     */
    getAllPurchases: async () => {
        const response = await api.get('/purchases');
        return response.data;
    },

    /**
     * Get purchase by ID
     */
    getPurchaseById: async (id) => {
        const response = await api.get(`/purchases/${id}`);
        return response.data;
    },

    /**
     * Create new purchase
     */
    createPurchase: async (purchaseData) => {
        const response = await api.post('/purchases', purchaseData);
        return response.data;
    },

    /**
     * Get purchases by status
     */
    getPurchasesByStatus: async (status) => {
        const response = await api.get(`/purchases/status/${status}`);
        return response.data;
    },

    /**
     * Get purchases by product
     */
    getPurchasesByProduct: async (productId) => {
        const response = await api.get(`/purchases/product/${productId}`);
        return response.data;
    },
};

export default purchaseService;