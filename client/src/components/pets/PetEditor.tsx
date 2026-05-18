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
}

export default class PetEditor extends React.Component<PetEditorProps, PetEditorState> {
  constructor(props: PetEditorProps) {
    super(props);
    this.state = {
      pet: { ...props.pet },
      errors: {},
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  componentDidUpdate(prevProps: PetEditorProps) {
    if (prevProps.pet !== this.props.pet) {
      this.setState({ pet: { ...this.props.pet } });
    }
  }

  handleChange(event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
    const { name, value } = event.target;
    this.setState(prevState => ({
      pet: {
        ...prevState.pet,
        [name]: name === 'typeId' ? parseInt(value, 10) : value,
      },
    }));
  }

  validate(): boolean {
    const errors: { [key: string]: string } = {};
    const { pet } = this.state;
    if (!pet.name || pet.name.trim() === '') {
      errors['name'] = 'Name is required';
    }
    if (!pet.birthDate || pet.birthDate.trim() === '') {
      errors['birthDate'] = 'Birth date is required';
    }
    if (!pet.typeId) {
      errors['typeId'] = 'Type is required';
    }
    this.setState({ errors });
    return Object.keys(errors).length === 0;
  }

  handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (this.validate()) {
      this.props.onSave(this.state.pet);
    }
  }

  render() {
    const { pet, errors } = this.state;
    const { petTypes, ownerId } = this.props;
    const isNew = !pet.id;

    return (
      <div>
        <h2>{isNew ? 'Add Pet' : 'Edit Pet'}</h2>
        <form onSubmit={this.handleSubmit} className="form-horizontal">
          <div className={`form-group${errors['name'] ? ' has-error' : ''}`}>
            <label className="col-sm-2 control-label" htmlFor="name">
              Name
            </label>
            <div className="col-sm-10">
              <input
                type="text"
                id="name"
                name="name"
                className="form-control"
                value={pet.name || ''}
                onChange={this.handleChange}
              />
              {errors['name'] && (
                <span className="help-block">{errors['name']}</span>
              )}
            </div>
          </div>

          <div className={`form-group${errors['birthDate'] ? ' has-error' : ''}`}>
            <label className="col-sm-2 control-label" htmlFor="birthDate">
              Birth Date
            </label>
            <div className="col-sm-10">
              <input
                type="text"
                id="birthDate"
                name="birthDate"
                className="form-control"
                placeholder="YYYY-MM-DD"
                value={pet.birthDate || ''}
                onChange={this.handleChange}
              />
              {errors['birthDate'] && (
                <span className="help-block">{errors['birthDate']}</span>
              )}
            </div>
          </div>

          <div className={`form-group${errors['typeId'] ? ' has-error' : ''}`}>
            <label className="col-sm-2 control-label" htmlFor="typeId">
              Type
            </label>
            <div className="col-sm-10">
              <select
                id="typeId"
                name="typeId"
                className="form-control"
                value={pet.typeId || ''}
                onChange={this.handleChange}
              >
                <option value="">-- Select Type --</option>
                {petTypes.map(pt => (
                  <option key={pt.id} value={pt.id}>
                    {pt.name}
                  </option>
                ))}
              </select>
              {errors['typeId'] && (
                <span className="help-block">{errors['typeId']}</span>
              )}
            </div>
          </div>

          <div className="form-group">
            <label className="col-sm-2 control-label" htmlFor="label">
              Label
            </label>
            <div className="col-sm-10">
              <input
                type="text"
                id="label"
                name="label"
                className="form-control"
                placeholder="e.g. indoor, rescue, therapy"
                value={pet.label || ''}
                onChange={this.handleChange}
              />
              <span className="help-block">
                Optional short label or tag for this pet.
              </span>
            </div>
          </div>

          <div className="form-group">
            <div className="col-sm-offset-2 col-sm-10">
              <button type="submit" className="btn btn-default">
                {isNew ? 'Add Pet' : 'Update Pet'}
              </button>
              &nbsp;
              <Link
                to={`/owners/${ownerId}`}
                className="btn btn-default"
              >
                Cancel
              </Link>
            </div>
          </div>
        </form>
      </div>
    );
  }
}
