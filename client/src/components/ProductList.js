// File: src/components/ProductList.js
import React, { useState, useEffect } from 'react';
import {
    Box,
    Paper,
    Typography,
    TextField,
    Button,
    Chip,
    InputAdornment,
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import SearchIcon from '@mui/icons-material/Search';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';
import WarningIcon from '@mui/icons-material/Warning';
import productService from '../services/productService';
import LoadingSpinner from './common/LoadingSpinner';
import ErrorAlert from './common/ErrorAlert';
import SaleForm from './SaleForm';
import PurchaseForm from './PurchaseForm';

/**
 * Product list component with DataGrid
 * Displays products with Sell and Purchase actions
 */
const ProductList = () => {
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchQuery, setSearchQuery] = useState('');

    // Modal states
    const [saleFormOpen, setSaleFormOpen] = useState(false);
    const [purchaseFormOpen, setPurchaseFormOpen] = useState(false);
    const [selectedProduct, setSelectedProduct] = useState(null);

    // Fetch products on mount
    useEffect(() => {
        fetchProducts();
    }, []);

    // Filter products when search query changes
    useEffect(() => {
        if (searchQuery.trim() === '') {
            setFilteredProducts(products);
        } else {
            const query = searchQuery.toLowerCase();
            const filtered = products.filter(
                (product) =>
                    product.name.toLowerCase().includes(query) ||
                    product.code.toLowerCase().includes(query) ||
                    (product.category && product.category.toLowerCase().includes(query))
            );
            setFilteredProducts(filtered);
        }
    }, [searchQuery, products]);

    const fetchProducts = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await productService.getAllProducts();
            setProducts(data);
            setFilteredProducts(data);
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }
    };

    const handleOpenSaleForm = (product) => {
        setSelectedProduct(product);
        setSaleFormOpen(true);
    };

    const handleOpenPurchaseForm = (product) => {
        setSelectedProduct(product);
        setPurchaseFormOpen(true);
    };

    const handleSaleSuccess = () => {
        fetchProducts(); // Refresh products to show updated stock
    };

    const handlePurchaseSuccess = () => {
        fetchProducts(); // Refresh products to show updated stock
    };

    const columns = [
        {
            field: 'code',
            headerName: 'Code',
            width: 120,
            fontWeight: 600,
        },
        {
            field: 'name',
            headerName: 'Product Name',
            flex: 1,
            minWidth: 200,
        },
        {
            field: 'category',
            headerName: 'Category',
            width: 130,
        },
        {
            field: 'brand',
            headerName: 'Brand',
            width: 130,
        },
        {
            field: 'unitPrice',
            headerName: 'Unit Price',
            width: 120,
            valueFormatter: (params) => `$${params.value.toFixed(2)}`,
        },
        {
            field: 'currentStock',
            headerName: 'Stock',
            width: 100,
            renderCell: (params) => {
                const isLowStock = params.row.currentStock <= 10;
                return (
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                        {isLowStock && (
                            <WarningIcon color="warning" fontSize="small" />
                        )}
                        <Typography
                            variant="body2"
                            color={isLowStock ? 'warning.main' : 'text.primary'}
                            fontWeight={isLowStock ? 600 : 400}
                        >
                            {params.value}
                        </Typography>
                    </Box>
                );
            },
        },
        {
            field: 'active',
            headerName: 'Status',
            width: 100,
            renderCell: (params) => (
                <Chip
                    label={params.value ? 'Active' : 'Inactive'}
                    color={params.value ? 'success' : 'default'}
                    size="small"
                />
            ),
        },
        {
            field: 'actions',
            headerName: 'Actions',
            width: 180,
            sortable: false,
            renderCell: (params) => (
                <Box sx={{ display: 'flex', gap: 1 }}>
                    <Button
                        variant="contained"
                        size="small"
                        startIcon={<ShoppingCartIcon />}
                        onClick={() => handleOpenSaleForm(params.row)}
                        disabled={!params.row.active || params.row.currentStock === 0}
                    >
                        Sell
                    </Button>
                    <Button
                        variant="outlined"
                        size="small"
                        startIcon={<AddShoppingCartIcon />}
                        onClick={() => handleOpenPurchaseForm(params.row)}
                    >
                        Buy
                    </Button>
                </Box>
            ),
        },
    ];

    if (loading) {
        return <LoadingSpinner message="Loading products..." />;
    }

    return (
        <Box>
            {/* Header */}
            <Box sx={{ mb: 3 }}>
                <Typography variant="h4" gutterBottom fontWeight={600}>
                    Product Inventory
                </Typography>
                <Typography variant="body1" color="text.secondary">
                    Manage sales and purchases for all products
                </Typography>
            </Box>

            {/* Error Display */}
            {error && <ErrorAlert error={error} onClose={() => setError(null)} />}

            {/* Search Bar */}
            <Paper sx={{ p: 2, mb: 3 }}>
                <TextField
                    fullWidth
                    placeholder="Search by product name, code, or category..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <SearchIcon />
                            </InputAdornment>
                        ),
                    }}
                />
            </Paper>

            {/* Products DataGrid */}
            <Paper sx={{ height: 600, width: '100%' }}>
                <DataGrid
                    rows={filteredProducts}
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

            {/* Sale Form Modal */}
            {selectedProduct && (
                <SaleForm
                    open={saleFormOpen}
                    onClose={() => setSaleFormOpen(false)}
                    product={selectedProduct}
                    onSuccess={handleSaleSuccess}
                />
            )}

            {/* Purchase Form Modal */}
            {selectedProduct && (
                <PurchaseForm
                    open={purchaseFormOpen}
                    onClose={() => setPurchaseFormOpen(false)}
                    product={selectedProduct}
                    onSuccess={handlePurchaseSuccess}
                />
            )}
        </Box>
    );
};

export default ProductList;