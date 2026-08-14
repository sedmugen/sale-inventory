import React from 'react';
import { Alert, AlertTitle, Box, Collapse, IconButton } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';

/**
 * Reusable error alert component
 * Formats standard error messages and validation error bags
 */
const ErrorAlert = ({ error, onClose }) => {
    if (!error) return null;

    // Extract error message
    let message = 'An unexpected error occurred';
    let details = null;

    if (typeof error === 'string') {
        message = error;
    } else if (error.response?.data) {
        const errorData = error.response.data;
        message = errorData.message || message;

        // If there are field-specific validation errors
        if (errorData.validationErrors) {
            details = Object.entries(errorData.validationErrors).map(([field, msg]) => (
                <li key={field}>
                    <strong>{field}:</strong> {msg}
                </li>
            ));
        }
    } else if (error.message) {
        message = error.message;
    }

    return (
        <Collapse in={!!error}>
            <Alert
                severity="error"
                sx={{ mb: 2 }}
                action={
                    onClose && (
                        <IconButton
                            aria-label="close"
                            color="inherit"
                            size="small"
                            onClick={onClose}
                        >
                            <CloseIcon fontSize="inherit" />
                        </IconButton>
                    )
                }
            >
                <AlertTitle>Error</AlertTitle>
                {message}
                {details && (
                    <Box component="ul" sx={{ mt: 1, mb: 0, pl: 2 }}>
                        {details}
                    </Box>
                )}
            </Alert>
        </Collapse>
    );
};

export default ErrorAlert;