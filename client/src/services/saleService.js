import api from './api';

/**
 * Sale API service
 */
const saleService = {
    /**
     * Get all sales
     */
    getAllSales: async () => {
        const response = await api.get('/sales');
        return response.data;
    },

    /**
     * Get sale by ID
     */
    getSaleById: async (id) => {
        const response = await api.get(`/sales/${id}`);
        return response.data;
    },

    /**
     * Create new sale
     */
    createSale: async (saleData) => {
        const response = await api.post('/sales', saleData);
        return response.data;
    },

    /**
     * Get sales by status
     */
    getSalesByStatus: async (status) => {
        const response = await api.get(`/sales/status/${status}`);
        return response.data;
    },

    /**
     * Get sales by product
     */
    getSalesByProduct: async (productId) => {
        const response = await api.get(`/sales/product/${productId}`);
        return response.data;
    },
};

export default saleService;