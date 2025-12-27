// File: src/services/customerService.js
import api from './api';

/**
 * Customer API service
 */
const customerService = {
    /**
     * Get all customers
     */
    getAllCustomers: async () => {
        const response = await api.get('/customers');
        return response.data;
    },

    /**
     * Get customer by ID
     */
    getCustomerById: async (id) => {
        const response = await api.get(`/customers/${id}`);
        return response.data;
    },

    /**
     * Create new customer
     */
    createCustomer: async (customerData) => {
        const response = await api.post('/customers', customerData);
        return response.data;
    },

    /**
     * Update customer
     */
    updateCustomer: async (id, customerData) => {
        const response = await api.put(`/customers/${id}`, customerData);
        return response.data;
    },

    /**
     * Delete customer
     */
    deleteCustomer: async (id) => {
        const response = await api.delete(`/customers/${id}`);
        return response.data;
    },

    /**
     * Get active (non-blocked) customers
     */
    getActiveCustomers: async () => {
        const response = await api.get('/customers/active');
        return response.data;
    },

    /**
     * Search customers by name
     */
    searchCustomers: async (name) => {
        const response = await api.get('/customers/search', {
            params: { name },
        });
        return response.data;
    },

    /**
     * Get customer sales
     */
    getCustomerSales: async (customerId) => {
        const response = await api.get(`/customers/${customerId}/sales`);
        return response.data;
    },

    /**
     * Get customer sales by status
     */
    getCustomerSalesByStatus: async (customerId, status) => {
        const response = await api.get(`/customers/${customerId}/sales/filter`, {
            params: status ? { status } : {},
        });
        return response.data;
    },
};

export default customerService;