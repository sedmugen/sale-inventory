// File: src/services/api.js
import axios from 'axios';

/**
 * Axios instance with base configuration
 * All API requests should use this instance
 */
const api = axios.create({
    baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

/**
 * Request interceptor
 * Add authorization headers, logging, etc.
 */
api.interceptors.request.use(
    (config) => {
        // Could add auth token here in future
        // config.headers.Authorization = `Bearer ${token}`;
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

/**
 * Response interceptor
 * Handle common errors, transform responses, etc.
 */
api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        // Handle common errors
        if (error.response) {
            // Server responded with error
            console.error('API Error:', error.response.data);
        } else if (error.request) {
            // Request made but no response
            console.error('Network Error:', error.request);
        } else {
            // Something else happened
            console.error('Error:', error.message);
        }
        return Promise.reject(error);
    }
);

export default api;