import React from 'react';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Navbar from '../components/Navbar';

describe('Navbar Component', () => {
    test('renders brand title and navigation links', () => {
        render(
            <BrowserRouter>
                <Navbar />
            </BrowserRouter>
        );

        expect(screen.getByText(/Inventory Management System/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Products/i })).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Customer Dashboard/i })).toBeInTheDocument();
    });
});
