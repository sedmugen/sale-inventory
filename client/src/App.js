// File: src/App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import theme from './theme';
import Navbar from './components/Navbar';
import ProductList from './components/ProductList';
import CustomerDashboard from './components/CustomerDashboard';

/**
 * Main App component
 * Sets up routing and theme
 */
function App() {
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <Router>
                <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
                    <Navbar />
                    <Box component="main" sx={{ flexGrow: 1, p: 3, bgcolor: 'background.default' }}>
                        <Routes>
                            <Route path="/" element={<Navigate to="/products" replace />} />
                            <Route path="/products" element={<ProductList />} />
                            <Route path="/dashboard" element={<CustomerDashboard />} />
                        </Routes>
                    </Box>
                </Box>
            </Router>
        </ThemeProvider>
    );
}

export default App;