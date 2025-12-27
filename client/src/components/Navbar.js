// File: src/components/Navbar.js
import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';
import InventoryIcon from '@mui/icons-material/Inventory';
import DashboardIcon from '@mui/icons-material/Dashboard';

/**
 * Navigation bar component
 * Provides navigation between main pages
 */
const Navbar = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const isActive = (path) => location.pathname === path;

    return (
        <AppBar position="static" elevation={1}>
            <Toolbar>
                <InventoryIcon sx={{ mr: 2 }} />
                <Typography variant="h6" component="div" sx={{ flexGrow: 1, fontWeight: 600 }}>
                    Inventory Management System
                </Typography>

                <Box sx={{ display: 'flex', gap: 1 }}>
                    <Button
                        color="inherit"
                        onClick={() => navigate('/products')}
                        sx={{
                            fontWeight: isActive('/products') ? 600 : 400,
                            bgcolor: isActive('/products') ? 'rgba(255, 255, 255, 0.1)' : 'transparent',
                        }}
                        startIcon={<InventoryIcon />}
                    >
                        Products
                    </Button>

                    <Button
                        color="inherit"
                        onClick={() => navigate('/dashboard')}
                        sx={{
                            fontWeight: isActive('/dashboard') ? 600 : 400,
                            bgcolor: isActive('/dashboard') ? 'rgba(255, 255, 255, 0.1)' : 'transparent',
                        }}
                        startIcon={<DashboardIcon />}
                    >
                        Customer Dashboard
                    </Button>
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default Navbar;