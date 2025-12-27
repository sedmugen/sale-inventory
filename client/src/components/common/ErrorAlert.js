// File: src/components/common/ErrorAlert.js
import React from 'react';
import { Alert, AlertTitle, Box } from '@mui/material';

/**
 * Error alert component
 * Displays error messages with title and optional details
 */
const ErrorAlert = ({ error, onClose }) => {
    // Extract error message from different error formats
    const getErrorMessage = () => {
        if (typeof error === 'string') {
            return error;
        }
        if (error?.response?.data?.message) {
            return error.response.data.message;
        }
        if (error?.message) {
            return error.message;
        }
        return 'An unexpected error occurred';
    };

    // Extract validation errors if present
    const getValidationErrors = () => {
        if (error?.response?.data?.validationErrors) {
            return error.response.data.validationErrors;
        }
        return null;
    };

    const errorMessage = getErrorMessage();
    const validationErrors = getValidationErrors();

    return (
        <Box sx={{ mb: 3 }}>
            <Alert severity="error" onClose={onClose}>
                <AlertTitle>Error</AlertTitle>
                {errorMessage}

                {validationErrors && (
                    <Box sx={{ mt: 1 }}>
                        <strong>Validation Errors:</strong>
                        <ul style={{ margin: '8px 0', paddingLeft: '20px' }}>
                            {Object.entries(validationErrors).map(([field, message]) => (
                                <li key={field}>
                                    <strong>{field}:</strong> {message}
                                </li>
                            ))}
                        </ul>
                    </Box>
                )}
            </Alert>
        </Box>
    );
};

export default ErrorAlert;