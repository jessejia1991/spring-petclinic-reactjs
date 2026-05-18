import * as React from 'react';
import { Link } from 'react-router-dom';

interface Pet {
  id?: number;
  name?: string;
  birthDate?: string;
  typeId?: number;
  label?: string;
}

interface PetType {
  id: number;
  name: string;
}

interface PetEditorProps {
  pet: Pet;
  petTypes: PetType[];
  ownerId: number;
  onSave: (pet: Pet) => void;
}

interface PetEditorState {
  pet: Pet;
  errors: { [key: string]: string };
  submitting: boolean;
  labelSaving: boolean;
  labelSaved: boolean;
  labelError: string | null;
}

class PetEditor extends React.Component<PetEditorProps, PetEditorState> {
  constructor(props: PetEditorProps) {
    super(props);
    this.state = {
      pet: { ...props.pet },
      errors: {},
      submitting: false,
      labelSaving: false,
      labelSaved: false,
      labelError: null,
    };
  }

  handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    this.setState(prevState => ({
      pet: { ...prevState.pet, [name]: value },
    }));
  };

  validate = (): boolean => {
    const { pet } = this.state;
    const errors: { [key: string]: string } = {};
    if (!pet.name || pet.name.trim() === '') {
      errors.name = 'Name is required';
    }
    if (!pet.birthDate) {
      errors.birthDate = 'Birth date is required';
    }
    if (!pet.typeId) {
      errors.typeId = 'Type is required';
    }
    this.setState({ errors });
    return Object.keys(errors).length === 0;
  };

  handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!this.validate()) return;
    this.setState({ submitting: true });
    this.props.onSave(this.state.pet);
  };

  handleLabelSave = async () => {
    const { pet } = this.state;
    if (!pet.id) return;
    this.setState({ labelSaving: true, labelSaved: false, labelError: null });
    try {
      const response = await fetch(`/api/pets/${pet.id}/label`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ label: pet.label || '' }),
      });
      if (!response.ok) {
        const errorText = await response.text();
        this.setState({ labelSaving: false, labelError: `Failed to save label: ${errorText}` });
        return;
      }
      this.setState({ labelSaving: false, labelSaved: true });
      setTimeout(() => this.setState({ labelSaved: false }), 2000);
    } catch (err) {
      this.setState({ labelSaving: false, labelError: String(err) });
    }
  };

  render() {
    const { pet, errors, submitting, labelSaving, labelSaved, labelError } = this.state;
    const { petTypes, ownerId } = this.props;
    const isEdit = !!pet.id;

    return (
      <div className="container">
        <h2>{isEdit ? 'Edit Pet' : 'Add Pet'}</h2>
        <form onSubmit={this.handleSubmit}>
          <div className="form-group">
            <label htmlFor="name">Name</label>
            <input
              id="name"
              name="name"
              type="text"
              className={`form-control ${errors.name ? 'is-invalid' : ''}`}
              value={pet.name || ''}
              onChange={this.handleChange}
            />
            {errors.name && <div className="invalid-feedback">{errors.name}</div>}
          </div>

          <div className="form-group">
            <label htmlFor="birthDate">Birth Date</label>
            <input
              id="birthDate"
              name="birthDate"
              type="date"
              className={`form-control ${errors.birthDate ? 'is-invalid' : ''}`}
              value={pet.birthDate || ''}
              onChange={this.handleChange}
            />
            {errors.birthDate && <div className="invalid-feedback">{errors.birthDate}</div>}
          </div>

          <div className="form-group">
            <label htmlFor="typeId">Type</label>
            <select
              id="typeId"
              name="typeId"
              className={`form-control ${errors.typeId ? 'is-invalid' : ''}`}
              value={pet.typeId || ''}
              onChange={this.handleChange}
            >
              <option value="">-- select type --</option>
              {petTypes.map(t => (
                <option key={t.id} value={t.id}>{t.name}</option>
              ))}
            </select>
            {errors.typeId && <div className="invalid-feedback">{errors.typeId}</div>}
          </div>

          {/* Label field — wired to /api/pets/:id/label */}
          <div className="form-group">
            <label htmlFor="label">Label</label>
            <div className="input-group">
              <input
                id="label"
                name="label"
                type="text"
                className="form-control"
                placeholder="e.g. indoor, needs-medication"
                value={pet.label || ''}
                onChange={this.handleChange}
              />
              {isEdit && (
                <div className="input-group-append">
                  <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={this.handleLabelSave}
                    disabled={labelSaving}
                  >
                    {labelSaving ? 'Saving…' : labelSaved ? 'Saved ✓' : 'Save Label'}
                  </button>
                </div>
              )}
            </div>
            {labelError && <div className="text-danger mt-1">{labelError}</div>}
            {!isEdit && (
              <small className="form-text text-muted">
                You can set a label after the pet has been created.
              </small>
            )}
          </div>

          <div className="form-group">
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? 'Saving…' : isEdit ? 'Update Pet' : 'Add Pet'}
            </button>
            {' '}
            <Link to={`/owners/${ownerId}`} className="btn btn-secondary">
              Cancel
            </Link>
          </div>
        </form>
      </div>
    );
  }
}

export default PetEditor;
