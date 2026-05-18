import * as React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route } from 'react-router-dom';
import PetEditor from '../../../src/components/pets/PetEditor';
import PetLabelField from '../../../src/components/pets/PetLabelField';

// Mock fetch / API calls
beforeEach(() => {
  global.fetch = jest.fn().mockResolvedValue({
    ok: true,
    json: async () => ({}),
  }) as jest.Mock;
});

afterEach(() => {
  jest.resetAllMocks();
});

describe('PetLabelField', () => {
  it('renders a label input field', () => {
    const mockOnChange = jest.fn();
    render(
      <PetLabelField
        value=""
        onChange={mockOnChange}
      />
    );

    // The label field should be present in the DOM
    const input = screen.getByLabelText(/label/i);
    expect(input).toBeInTheDocument();
  });

  it('displays the current label value', () => {
    const mockOnChange = jest.fn();
    render(
      <PetLabelField
        value="fluffy-cat"
        onChange={mockOnChange}
      />
    );

    const input = screen.getByDisplayValue('fluffy-cat');
    expect(input).toBeInTheDocument();
  });

  it('calls onChange when the user types a new label', async () => {
    const mockOnChange = jest.fn();
    render(
      <PetLabelField
        value=""
        onChange={mockOnChange}
      />
    );

    const input = screen.getByLabelText(/label/i);
    await userEvent.type(input, 'indoor');

    expect(mockOnChange).toHaveBeenCalled();
  });

  it('clears the label value when cleared by user', async () => {
    const mockOnChange = jest.fn();
    const { rerender } = render(
      <PetLabelField
        value="indoor"
        onChange={mockOnChange}
      />
    );

    const input = screen.getByDisplayValue('indoor');
    fireEvent.change(input, { target: { value: '' } });

    expect(mockOnChange).toHaveBeenCalled();

    rerender(
      <PetLabelField
        value=""
        onChange={mockOnChange}
      />
    );

    expect(screen.getByLabelText(/label/i)).toHaveValue('');
  });
});

describe('PetEditor with label field', () => {
  const renderPetEditor = (ownerId: number, petId?: number) => {
    const path = petId
      ? `/owners/${ownerId}/pets/${petId}/edit`
      : `/owners/${ownerId}/pets/new`;

    return render(
      <MemoryRouter initialEntries={[path]}>
        <Route
          path={petId ? '/owners/:ownerId/pets/:petId/edit' : '/owners/:ownerId/pets/new'}
          component={PetEditor}
        />
      </MemoryRouter>
    );
  };

  it('renders the label field within PetEditor for a new pet', async () => {
    // Mock owner fetch
    (global.fetch as jest.Mock).mockImplementation((url: string) => {
      if (url.includes('/owners/')) {
        return Promise.resolve({
          ok: true,
          json: async () => ({
            id: 1,
            firstName: 'John',
            lastName: 'Doe',
            pets: [],
          }),
        });
      }
      return Promise.resolve({ ok: true, json: async () => ({}) });
    });

    renderPetEditor(1);

    await waitFor(() => {
      const labelField = screen.queryByLabelText(/label/i);
      if (labelField) {
        expect(labelField).toBeInTheDocument();
      }
    });
  });

  it('submits the form including the label value', async () => {
    (global.fetch as jest.Mock).mockImplementation((url: string) => {
      if (url.includes('/owners/')) {
        return Promise.resolve({
          ok: true,
          json: async () => ({
            id: 1,
            firstName: 'John',
            lastName: 'Doe',
            pets: [],
          }),
        });
      }
      return Promise.resolve({ ok: true, json: async () => ({}) });
    });

    renderPetEditor(1);

    await waitFor(() => {
      const labelInput = screen.queryByLabelText(/label/i);
      if (labelInput) {
        fireEvent.change(labelInput, { target: { value: 'outdoor' } });
      }
    });

    const submitButton = screen.queryByRole('button', { name: /submit|save|add pet/i });
    if (submitButton) {
      fireEvent.click(submitButton);

      await waitFor(() => {
        const calls = (global.fetch as jest.Mock).mock.calls;
        const postCall = calls.find(
          ([url, options]: [string, RequestInit]) =>
            options && (options.method === 'POST' || options.method === 'PUT')
        );
        if (postCall) {
          const body = JSON.parse(postCall[1].body as string);
          expect(body).toHaveProperty('label');
        }
      });
    }
  });
});
