// File: src/components/common/SuccessSnackbar.js
import React from 'react';
import { Snackbar, Alert } from '@mui/material';

/**
 * Success snackbar component
 * Displays temporary success notification
 */
const SuccessSnackbar = ({ open, message, onClose }) => {
    return (
        <Snackbar
            open={open}
            autoHideDuration={3000}
            onClose={onClose}
            anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        >
            <Alert onClose={onClose} severity="success" sx={{ width: '100%' }}>
                {message}
            </Alert>
        </Snackbar>
    );
};

export default SuccessSnackbar;