/**
 * PetLabelField
 *
 * A standalone component for displaying/editing the label of a pet.
 * Calls PUT /api/pets/:petId/label
 *
 * Usage:
 *   <PetLabelField petId={5} initialLabel="indoor" />
 */
import * as React from 'react';

interface Props {
  petId: number;
  initialLabel?: string;
  onLabelChanged?: (newLabel: string) => void;
}

interface State {
  label: string;
  saving: boolean;
  saved: boolean;
  error: string | null;
}

class PetLabelField extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      label: props.initialLabel || '',
      saving: false,
      saved: false,
      error: null,
    };
  }

  componentDidUpdate(prevProps: Props) {
    if (prevProps.initialLabel !== this.props.initialLabel) {
      this.setState({ label: this.props.initialLabel || '' });
    }
  }

  handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    this.setState({ label: e.target.value, saved: false, error: null });
  };

  handleSave = async () => {
    const { petId, onLabelChanged } = this.props;
    const { label } = this.state;
    this.setState({ saving: true, saved: false, error: null });
    try {
      const res = await fetch(`/api/pets/${petId}/label`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ label }),
      });
      if (!res.ok) {
        const msg = await res.text();
        this.setState({ saving: false, error: `Error: ${msg}` });
        return;
      }
      this.setState({ saving: false, saved: true });
      if (onLabelChanged) onLabelChanged(label);
      setTimeout(() => this.setState({ saved: false }), 2500);
    } catch (err) {
      this.setState({ saving: false, error: String(err) });
    }
  };

  render() {
    const { label, saving, saved, error } = this.state;
    return (
      <div className="form-group">
        <label htmlFor={`pet-label-${this.props.petId}`}>Label</label>
        <div className="input-group">
          <input
            id={`pet-label-${this.props.petId}`}
            type="text"
            className="form-control"
            value={label}
            onChange={this.handleChange}
            placeholder="e.g. indoor, senior, needs-medication"
          />
          <div className="input-group-append">
            <button
              type="button"
              className={`btn ${
                saved ? 'btn-success' : 'btn-outline-primary'
              }`}
              onClick={this.handleSave}
              disabled={saving}
            >
              {saving ? 'Saving…' : saved ? 'Saved ✓' : 'Save Label'}
            </button>
          </div>
        </div>
        {error && <small className="form-text text-danger">{error}</small>}
      </div>
    );
  }
}

export default PetLabelField;
