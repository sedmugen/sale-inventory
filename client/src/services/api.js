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
 * Configures authorization headers and request logging
 */
api.interceptors.request.use(
    (config) => config,
    (error) => Promise.reject(error)
);

/**
 * Response interceptor
 * Handles and normalizes API error responses
 */
api.interceptors.response.use(
    (response) => response,
    (error) => Promise.reject(error)
);

export default api;