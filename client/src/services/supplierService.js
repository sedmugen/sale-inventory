import api from './api';

/**
 * Supplier API service
 */
const supplierService = {
    /**
     * Get all suppliers
     */
    getAllSuppliers: async () => {
        const response = await api.get('/suppliers');
        return response.data;
    },

    /**
     * Get supplier by ID
     */
    getSupplierById: async (id) => {
        const response = await api.get(`/suppliers/${id}`);
        return response.data;
    },

    /**
     * Create new supplier
     */
    createSupplier: async (supplierData) => {
        const response = await api.post('/suppliers', supplierData);
        return response.data;
    },

    /**
     * Update supplier
     */
    updateSupplier: async (id, supplierData) => {
        const response = await api.put(`/suppliers/${id}`, supplierData);
        return response.data;
    },

    /**
     * Delete supplier
     */
    deleteSupplier: async (id) => {
        const response = await api.delete(`/suppliers/${id}`);
        return response.data;
    },

    /**
     * Get active suppliers only
     */
    getActiveSuppliers: async () => {
        const response = await api.get('/suppliers/active');
        return response.data;
    },

    /**
     * Search suppliers by name
     */
    searchSuppliers: async (name) => {
        const response = await api.get('/suppliers/search', {
            params: { name },
        });
        return response.data;
    },

    /**
     * Get supplier purchases
     */
    getSupplierPurchases: async (supplierId) => {
        const response = await api.get(`/suppliers/${supplierId}/purchases`);
        return response.data;
    },
};

export default supplierService;