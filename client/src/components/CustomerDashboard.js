// File: src/components/CustomerDashboard.js
import React, { useState, useEffect, useCallback } from 'react';
import {
    Box,
    Paper,
    Typography,
    MenuItem,
    Chip,
    FormControl,
    InputLabel,
    Select,
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import customerService from '../services/customerService';
import LoadingSpinner from './common/LoadingSpinner';
import ErrorAlert from './common/ErrorAlert';

/**
 * Customer Sales Dashboard
 * View sales history for a selected customer with filtering
 */
const CustomerDashboard = () => {
    const [customers, setCustomers] = useState([]);
    const [selectedCustomerId, setSelectedCustomerId] = useState('');
    const [sales, setSales] = useState([]);
    const [filteredSales, setFilteredSales] = useState([]);
    const [statusFilter, setStatusFilter] = useState('ALL');
    const [loading, setLoading] = useState(false);
    const [customersLoading, setCustomersLoading] = useState(true);
    const [error, setError] = useState(null);

    // Fetch customers on mount
    useEffect(() => {
        fetchCustomers();
    }, []);

    const fetchCustomerSales = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await customerService.getCustomerSales(selectedCustomerId);
            setSales(data);
            setFilteredSales(data);
        } catch (err) {
            setError(err);
            setSales([]);
            setFilteredSales([]);
        } finally {
            setLoading(false);
        }
    }, [selectedCustomerId]);

    // Fetch sales when customer is selected
    useEffect(() => {
        if (selectedCustomerId) {
            fetchCustomerSales();
        } else {
            setSales([]);
            setFilteredSales([]);
        }
    }, [selectedCustomerId, fetchCustomerSales]);

    // Filter sales when status filter changes
    useEffect(() => {
        if (statusFilter === 'ALL') {
            setFilteredSales(sales);
        } else {
            const filtered = sales.filter((sale) => sale.status === statusFilter);
            setFilteredSales(filtered);
        }
    }, [statusFilter, sales]);

    const fetchCustomers = async () => {
        try {
            setCustomersLoading(true);
            const data = await customerService.getAllCustomers();
            setCustomers(data);
        } catch (err) {
            setError(err);
        } finally {
            setCustomersLoading(false);
        }
    };

    const handleCustomerChange = (e) => {
        setSelectedCustomerId(e.target.value);
        setStatusFilter('ALL');
    };

    const handleStatusFilterChange = (e) => {
        setStatusFilter(e.target.value);
    };

    const columns = [
        {
            field: 'date',
            headerName: 'Date',
            width: 180,
            valueFormatter: (params) => {
                const date = new Date(params.value);
                return date.toLocaleString('en-US', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                });
            },
        },
        {
            field: 'productCode',
            headerName: 'Product Code',
            width: 130,
            valueGetter: (params) => params.row.product?.code || '—',
        },
        {
            field: 'productName',
            headerName: 'Product Name',
            flex: 1,
            minWidth: 200,
            valueGetter: (params) => params.row.product?.name || '—',
        },
        {
            field: 'quantity',
            headerName: 'Quantity',
            width: 100,
            align: 'center',
        },
        {
            field: 'unitPrice',
            headerName: 'Unit Price',
            width: 120,
            valueFormatter: (params) => `$${params.value.toFixed(2)}`,
        },
        {
            field: 'totalPrice',
            headerName: 'Total Price',
            width: 120,
            valueFormatter: (params) => `$${params.value.toFixed(2)}`,
            renderCell: (params) => (
                <Typography variant="body2" fontWeight={600}>
                    ${params.value.toFixed(2)}
                </Typography>
            ),
        },
        {
            field: 'status',
            headerName: 'Status',
            width: 120,
            renderCell: (params) => (
                <Chip
                    label={params.value}
                    color={params.value === 'CONFIRMED' ? 'success' : 'default'}
                    size="small"
                />
            ),
        },
    ];

    // Calculate statistics
    const totalSales = filteredSales.length;
    const confirmedSales = filteredSales.filter((s) => s.status === 'CONFIRMED').length;
    const totalRevenue = filteredSales
        .filter((s) => s.status === 'CONFIRMED')
        .reduce((sum, sale) => sum + sale.totalPrice, 0);

    if (customersLoading) {
        return <LoadingSpinner message="Loading customers..." />;
    }

    return (
        <Box>
            {/* Header */}
            <Box sx={{ mb: 3 }}>
                <Typography variant="h4" gutterBottom fontWeight={600}>
                    Customer Sales Dashboard
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    View sales history and statistics for any customer
                </Typography>
            </Box>

            {/* Error Display */}
            {error && <ErrorAlert error={error} onClose={() => setError(null)} />}

            {/* Customer Selection */}
            <Paper sx={{ p: 3, mb: 3 }}>
                <FormControl fullWidth>
                    <InputLabel>Select Customer</InputLabel>
                    <Select
                        value={selectedCustomerId}
                        onChange={handleCustomerChange}
                        label="Select Customer"
                    >
                        <MenuItem value="">
                            <em>Choose a customer</em>
                        </MenuItem>
                        {customers.map((customer) => (
                            <MenuItem key={customer.id} value={customer.id}>
                                {customer.name} ({customer.email})
                                {customer.blocked && (
                                    <Chip label="BLOCKED" size="small" color="error" sx={{ ml: 1 }} />
                                )}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </Paper>

            {/* Sales Content */}
            {selectedCustomerId && (
                <>
                    {/* Statistics */}
                    <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
                        <Paper sx={{ p: 2, flex: 1 }}>
                            <Typography variant="body2" color="text.secondary">
                                Total Sales
                            </Typography>
                            <Typography variant="h4" fontWeight={600}>
                                {totalSales}
                            </Typography>
                        </Paper>
                        <Paper sx={{ p: 2, flex: 1 }}>
                            <Typography variant="body2" color="text.secondary">
                                Confirmed Sales
                            </Typography>
                            <Typography variant="h4" fontWeight={600} color="success.main">
                                {confirmedSales}
                            </Typography>
                        </Paper>
                        <Paper sx={{ p: 2, flex: 1 }}>
                            <Typography variant="body2" color="text.secondary">
                                Total Revenue
                            </Typography>
                            <Typography variant="h4" fontWeight={600} color="primary.main">
                                ${totalRevenue.toFixed(2)}
                            </Typography>
                        </Paper>
                    </Box>

                    {/* Status Filter */}
                    <Paper sx={{ p: 2, mb: 3 }}>
                        <FormControl fullWidth>
                            <InputLabel>Filter by Status</InputLabel>
                            <Select
                                value={statusFilter}
                                onChange={handleStatusFilterChange}
                                label="Filter by Status"
                            >
                                <MenuItem value="ALL">All Statuses</MenuItem>
                                <MenuItem value="CONFIRMED">Confirmed</MenuItem>
                                <MenuItem value="CANCELLED">Cancelled</MenuItem>
                            </Select>
                        </FormControl>
                    </Paper>

                    {/* Sales Table */}
                    {loading ? (
                        <LoadingSpinner message="Loading sales..." />
                    ) : (
                        <Paper sx={{ height: 500, width: '100%' }}>
                            <DataGrid
                                rows={filteredSales}
                                columns={columns}
                                pageSize={10}
                                rowsPerPageOptions={[10, 25, 50]}
                                disableSelectionOnClick
                                sx={{
                                    border: 'none',
                                    '& .MuiDataGrid-cell:focus': {
                                        outline: 'none',
                                    },
                                }}
                            />
                        </Paper>
                    )}

                    {/* No Sales Message */}
                    {!loading && filteredSales.length === 0 && (
                        <Paper sx={{ p: 4, textAlign: 'center' }}>
                            <Typography variant="body1" color="text.secondary">
                                No sales found for the selected filters
                            </Typography>
                        </Paper>
                    )}
                </>
            )}

            {/* No Customer Selected Message */}
            {!selectedCustomerId && (
                <Paper sx={{ p: 4, textAlign: 'center' }}>
                    <Typography variant="body1" color="text.secondary">
                        Please select a customer to view their sales history
                    </Typography>
                </Paper>
            )}
        </Box>
    );
};

export default CustomerDashboard;