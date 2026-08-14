// File: src/components/SaleForm.js
import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Button,
    Box,
    Typography,
    MenuItem,
    Alert,
    CircularProgress,
} from '@mui/material';
import customerService from '../services/customerService';
import saleService from '../services/saleService';
import SuccessSnackbar from './common/SuccessSnackbar';

/**
 * Sale form modal dialog
 * Creates a new sale for the selected product
 */
const SaleForm = ({ open, onClose, product, onSuccess }) => {
    const [customers, setCustomers] = useState([]);
    const [formData, setFormData] = useState({
        customerId: '',
        quantity: 1,
        unitPrice: product?.unitPrice || 0,
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');

    // Fetch active customers when dialog opens
    useEffect(() => {
        if (open) {
            fetchCustomers();
            // Reset form with product's unit price
            setFormData({
                customerId: '',
                quantity: 1,
                unitPrice: product?.unitPrice || 0,
            });
            setError(null);
        }
    }, [open, product]);

    const fetchCustomers = async () => {
        try {
            const data = await customerService.getActiveCustomers();
            setCustomers(data);
        } catch (err) {
            setError('Failed to load customers');
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: name === 'quantity'
                ? (value === '' ? '' : Math.max(0, parseInt(value, 10) || 0))
                : name === 'unitPrice'
                ? (value === '' ? '' : Math.max(0, parseFloat(value) || 0))
                : value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const numQuantity = parseInt(formData.quantity, 10);
        const numUnitPrice = parseFloat(formData.unitPrice);

        // Validation
        if (!formData.customerId) {
            setError('Please select a customer');
            return;
        }
        if (isNaN(numQuantity) || numQuantity < 1) {
            setError('Quantity must be at least 1');
            return;
        }
        if (numQuantity > product.currentStock) {
            setError(`Only ${product.currentStock} units available in stock`);
            return;
        }
        if (isNaN(numUnitPrice) || numUnitPrice < 0) {
            setError('Unit price must be non-negative');
            return;
        }

        try {
            setLoading(true);
            setError(null);

            const saleData = {
                productId: product.id,
                customerId: formData.customerId,
                quantity: numQuantity,
                unitPrice: numUnitPrice,
            };

            await saleService.createSale(saleData);

            setSuccessMessage('Sale created successfully!');
            onSuccess();

            // Close dialog after short delay
            setTimeout(() => {
                onClose();
            }, 1000);
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }
    };

    const qty = typeof formData.quantity === 'number' ? formData.quantity : (parseInt(formData.quantity, 10) || 0);
    const price = typeof formData.unitPrice === 'number' ? formData.unitPrice : (parseFloat(formData.unitPrice) || 0);
    const totalPrice = (qty * price).toFixed(2);

    return (
        <>
            <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
                <DialogTitle>
                    Create Sale - {product?.name}
                </DialogTitle>

                <form onSubmit={handleSubmit}>
                    <DialogContent>
                        {/* Product Info */}
                        <Box sx={{ mb: 3, p: 2, bgcolor: 'background.default', borderRadius: 1 }}>
                            <Typography variant="subtitle2" color="text.secondary">
                                Product Details
                            </Typography>
                            <Typography variant="body2">
                                <strong>Code:</strong> {product?.code}
                            </Typography>
                            <Typography variant="body2">
                                <strong>Available Stock:</strong> {product?.currentStock} units
                            </Typography>
                            <Typography variant="body2">
                                <strong>Unit Price:</strong> ${product?.unitPrice}
                            </Typography>
                        </Box>

                        {/* Error Display */}
                        {error && (
                            <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
                                {typeof error === 'string' ? error : error?.response?.data?.message || 'An error occurred'}
                            </Alert>
                        )}

                        {/* Customer Selection */}
                        <TextField
                            select
                            fullWidth
                            label="Customer"
                            name="customerId"
                            value={formData.customerId}
                            onChange={handleChange}
                            required
                            sx={{ mb: 2 }}
                        >
                            <MenuItem value="">
                                <em>Select a customer</em>
                            </MenuItem>
                            {customers.map((customer) => (
                                <MenuItem key={customer.id} value={customer.id}>
                                    {customer.name} ({customer.email})
                                </MenuItem>
                            ))}
                        </TextField>

                        {/* Quantity */}
                        <TextField
                            fullWidth
                            type="number"
                            label="Quantity"
                            name="quantity"
                            value={formData.quantity}
                            onChange={handleChange}
                            required
                            inputProps={{ min: 1, max: product?.currentStock }}
                            sx={{ mb: 2 }}
                        />

                        {/* Unit Price (editable for discounts) */}
                        <TextField
                            fullWidth
                            type="number"
                            label="Unit Price"
                            name="unitPrice"
                            value={formData.unitPrice}
                            onChange={handleChange}
                            required
                            inputProps={{ min: 0, step: 0.01 }}
                            sx={{ mb: 2 }}
                        />

                        {/* Total Price Display */}
                        <Box sx={{ p: 2, bgcolor: 'primary.light', color: 'primary.contrastText', borderRadius: 1 }}>
                            <Typography variant="h6" align="center">
                                Total Price: ${totalPrice}
                            </Typography>
                        </Box>
                    </DialogContent>

                    <DialogActions>
                        <Button onClick={onClose} disabled={loading}>
                            Cancel
                        </Button>
                        <Button
                            type="submit"
                            variant="contained"
                            disabled={loading}
                            startIcon={loading && <CircularProgress size={20} />}
                        >
                            {loading ? 'Creating...' : 'Create Sale'}
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>

            <SuccessSnackbar
                open={!!successMessage}
                message={successMessage}
                onClose={() => setSuccessMessage('')}
            />
        </>
    );
};

export default SaleForm;