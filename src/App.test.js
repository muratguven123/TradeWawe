import { render, screen } from '@testing-library/react';
import App from './App';

test('renders TradeWave', () => {
  render(<App />);
  const titleElement = screen.getByText(/TradeWave/i);
  expect(titleElement).toBeInTheDocument();
});
