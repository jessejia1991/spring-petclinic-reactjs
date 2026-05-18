import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import PetEditor from './PetEditor';

interface PetDetailPageParams {
  ownerId: string;
  petId?: string;
}

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

interface PetDetailPageState {
  pet: Pet;
  petTypes: PetType[];
  loading: boolean;
  error?: string;
  successMessage?: string;
}

export default class PetDetailPage extends React.Component<
  RouteComponentProps<PetDetailPageParams>,
  PetDetailPageState
> {
  constructor(props: RouteComponentProps<PetDetailPageParams>) {
    super(props);
    this.state = {
      pet: {},
      petTypes: [],
      loading: true,
    };
    this.handleSave = this.handleSave.bind(this);
  }

  componentDidMount() {
    const { ownerId, petId } = this.props.match.params;

    const petTypesPromise = fetch('/api/pettypes')
      .then(res => res.json())
      .then((data: PetType[]) => {
        this.setState({ petTypes: data });
      });

    if (petId) {
      const petPromise = fetch(`/api/owners/${ownerId}/pets/${petId}`)
        .then(res => {
          if (!res.ok) throw new Error('Failed to load pet');
          return res.json();
        })
        .then((data: Pet) => {
          this.setState({ pet: data });
        })
        .catch(err => {
          this.setState({ error: err.message });
        });

      Promise.all([petTypesPromise, petPromise]).then(() =>
        this.setState({ loading: false })
      );
    } else {
      petTypesPromise.then(() => this.setState({ loading: false }));
    }
  }

  handleSave(pet: Pet) {
    const { ownerId, petId } = this.props.match.params;
    const method = petId ? 'PUT' : 'POST';
    const url = petId
      ? `/api/owners/${ownerId}/pets/${petId}`
      : `/api/owners/${ownerId}/pets`;

    fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(pet),
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to save pet');
        return res.json();
      })
      .then(() => {
        this.setState({ successMessage: 'Pet saved successfully.' });
        this.props.history.push(`/owners/${ownerId}`);
      })
      .catch(err => {
        this.setState({ error: err.message });
      });
  }

  render() {
    const { pet, petTypes, loading, error, successMessage } = this.state;
    const { ownerId } = this.props.match.params;

    if (loading) {
      return <div>Loading...</div>;
    }

    return (
      <div>
        {error && (
          <div className="alert alert-danger" role="alert">
            {error}
          </div>
        )}
        {successMessage && (
          <div className="alert alert-success" role="alert">
            {successMessage}
          </div>
        )}
        <PetEditor
          pet={pet}
          petTypes={petTypes}
          ownerId={parseInt(ownerId, 10)}
          onSave={this.handleSave}
        />
      </div>
    );
  }
}
