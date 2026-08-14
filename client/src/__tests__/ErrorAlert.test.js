import React from 'react';
import { render, screen } from '@testing-library/react';
import ErrorAlert from '../components/common/ErrorAlert';

describe('ErrorAlert Component', () => {
    test('renders string error message', () => {
        render(<ErrorAlert error="Failed to fetch products" />);

        expect(screen.getByText(/Failed to fetch products/i)).toBeInTheDocument();
        expect(screen.getByText(/Error/i)).toBeInTheDocument();
    });

    test('renders validation errors list when provided', () => {
        const errorObj = {
            response: {
                data: {
                    message: 'Validation Failed',
                    validationErrors: {
                        name: 'Product name is required',
                        price: 'Price must be positive',
                    },
                },
            },
        };

        render(<ErrorAlert error={errorObj} />);

        expect(screen.getByText(/Validation Failed/i)).toBeInTheDocument();
        expect(screen.getByText(/Product name is required/i)).toBeInTheDocument();
        expect(screen.getByText(/Price must be positive/i)).toBeInTheDocument();
    });
});
