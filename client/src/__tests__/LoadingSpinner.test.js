import React from 'react';
import { render, screen } from '@testing-library/react';
import LoadingSpinner from '../components/common/LoadingSpinner';

describe('LoadingSpinner Component', () => {
    test('renders default loading message', () => {
        render(<LoadingSpinner />);

        expect(screen.getByText(/Loading.../i)).toBeInTheDocument();
    });

    test('renders custom loading message', () => {
        render(<LoadingSpinner message="Fetching sales data..." />);

        expect(screen.getByText(/Fetching sales data.../i)).toBeInTheDocument();
    });
});
