import React from 'react';
import { Snackbar, Alert } from '@mui/material';

/**
 * Reusable success notification toast
 */
const SuccessSnackbar = ({ open, message, onClose, duration = 4000 }) => {
    return (
        <Snackbar
            open={open}
            autoHideDuration={duration}
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