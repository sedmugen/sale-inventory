// File: src/components/PurchaseForm.js
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
import supplierService from '../services/supplierService';
import purchaseService from '../services/purchaseService';
import SuccessSnackbar from './common/SuccessSnackbar';

/**
 * Purchase form modal dialog
 * Creates a new purchase for the selected product
 */
const PurchaseForm = ({ open, onClose, product, onSuccess }) => {
    const [suppliers, setSuppliers] = useState([]);
    const [formData, setFormData] = useState({
        supplierId: '',
        quantity: 1,
        unitCost: 0,
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');

    // Fetch active suppliers when dialog opens
    useEffect(() => {
        if (open) {
            fetchSuppliers();
            // Reset form
            setFormData({
                supplierId: '',
                quantity: 1,
                unitCost: 0,
            });
            setError(null);
        }
    }, [open]);

    const fetchSuppliers = async () => {
        try {
            const data = await supplierService.getActiveSuppliers();
            setSuppliers(data);
        } catch (err) {
            setError('Failed to load suppliers');
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: name === 'quantity' ? parseInt(value) : parseFloat(value),
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validation
        if (!formData.supplierId) {
            setError('Please select a supplier');
            return;
        }
        if (formData.quantity < 1) {
            setError('Quantity must be at least 1');
            return;
        }
        if (formData.unitCost < 0) {
            setError('Unit cost must be non-negative');
            return;
        }

        try {
            setLoading(true);
            setError(null);

            const purchaseData = {
                productId: product.id,
                supplierId: formData.supplierId,
                quantity: formData.quantity,
                unitCost: formData.unitCost,
            };

            await purchaseService.createPurchase(purchaseData);

            setSuccessMessage('Purchase created successfully!');
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

    const totalCost = (formData.quantity * formData.unitCost).toFixed(2);
    const newStock = product?.currentStock + formData.quantity;

    return (
        <>
            <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
                <DialogTitle>
                    Create Purchase - {product?.name}
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
                                <strong>Current Stock:</strong> {product?.currentStock} units
                            </Typography>
                            <Typography variant="body2">
                                <strong>Selling Price:</strong> ${product?.unitPrice}
                            </Typography>
                        </Box>

                        {/* Error Display */}
                        {error && (
                            <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
                                {typeof error === 'string' ? error : error?.response?.data?.message || 'An error occurred'}
                            </Alert>
                        )}

                        {/* Supplier Selection */}
                        <TextField
                            select
                            fullWidth
                            label="Supplier"
                            name="supplierId"
                            value={formData.supplierId}
                            onChange={handleChange}
                            required
                            sx={{ mb: 2 }}
                        >
                            <MenuItem value="">
                                <em>Select a supplier</em>
                            </MenuItem>
                            {suppliers.map((supplier) => (
                                <MenuItem key={supplier.id} value={supplier.id}>
                                    {supplier.name} {supplier.companyName && `(${supplier.companyName})`}
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
                            inputProps={{ min: 1 }}
                            sx={{ mb: 2 }}
                        />

                        {/* Unit Cost */}
                        <TextField
                            fullWidth
                            type="number"
                            label="Unit Cost"
                            name="unitCost"
                            value={formData.unitCost}
                            onChange={handleChange}
                            required
                            inputProps={{ min: 0, step: 0.01 }}
                            helperText="Cost per unit from supplier"
                            sx={{ mb: 2 }}
                        />

                        {/* Summary */}
                        <Box sx={{ p: 2, bgcolor: 'success.light', color: 'success.contrastText', borderRadius: 1 }}>
                            <Typography variant="body2" gutterBottom>
                                <strong>Total Cost:</strong> ${totalCost}
                            </Typography>
                            <Typography variant="body2">
                                <strong>New Stock Level:</strong> {newStock} units
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
                            {loading ? 'Creating...' : 'Create Purchase'}
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

export default PurchaseForm;