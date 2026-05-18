
import * as React from 'react';
import { shallow, mount } from 'enzyme';
import { MemoryRouter } from 'react-router-dom';
import { expect } from 'chai';
import * as sinon from 'sinon';

// Mock the PetEditor component - adjust path as needed
import PetEditor from '../../src/components/pets/PetEditor';

describe('PetEditor - label field', () => {
  const mockPet = {
    id: 10,
    name: 'Fluffy',
    birthDate: '2020-01-15',
    typeId: 1,
    label: 'indoor',
  };

  const mockPetTypes = [
    { id: 1, name: 'cat' },
    { id: 2, name: 'dog' },
  ];

  it('renders the label input field', () => {
    const wrapper = mount(
      <MemoryRouter>
        <PetEditor
          ownerId={1}
          pet={mockPet}
          petTypes={mockPetTypes}
          onSave={sinon.spy()}
        />
      </MemoryRouter>
    );

    // Check that a label input exists
    const labelInput = wrapper.find('input[name="label"]');
    expect(labelInput).to.have.length(1);
  });

  it('displays the existing label value when editing a pet', () => {
    const wrapper = mount(
      <MemoryRouter>
        <PetEditor
          ownerId={1}
          pet={mockPet}
          petTypes={mockPetTypes}
          onSave={sinon.spy()}
        />
      </MemoryRouter>
    );

    const labelInput = wrapper.find('input[name="label"]');
    expect(labelInput.prop('value')).to.equal('indoor');
  });

  it('renders label field with empty value for a new pet', () => {
    const newPet = {
      id: undefined,
      name: '',
      birthDate: '',
      typeId: 1,
      label: '',
    };

    const wrapper = mount(
      <MemoryRouter>
        <PetEditor
          ownerId={1}
          pet={newPet}
          petTypes={mockPetTypes}
          onSave={sinon.spy()}
        />
      </MemoryRouter>
    );

    const labelInput = wrapper.find('input[name="label"]');
    expect(labelInput).to.have.length(1);
    expect(labelInput.prop('value')).to.equal('');
  });

  it('updates label field value on change', () => {
    const wrapper = mount(
      <MemoryRouter>
        <PetEditor
          ownerId={1}
          pet={mockPet}
          petTypes={mockPetTypes}
          onSave={sinon.spy()}
        />
      </MemoryRouter>
    );

    const labelInput = wrapper.find('input[name="label"]');
    labelInput.simulate('change', { target: { name: 'label', value: 'outdoor' } });

    // After change, the input should reflect new value
    const updatedInput = wrapper.find('input[name="label"]');
    expect(updatedInput.prop('value')).to.equal('outdoor');
  });

  it('includes label value when form is submitted', () => {
    const onSaveSpy = sinon.spy();

    const wrapper = mount(
      <MemoryRouter>
        <PetEditor
          ownerId={1}
          pet={mockPet}
          petTypes={mockPetTypes}
          onSave={onSaveSpy}
        />
      </MemoryRouter>
    );

    // Change the label value
    const labelInput = wrapper.find('input[name="label"]');
    labelInput.simulate('change', { target: { name: 'label', value: 'outdoor' } });

    // Submit the form
    const form = wrapper.find('form');
    form.simulate('submit');

    // Verify onSave was called with updated label
    expect(onSaveSpy.called).to.equal(true);
    const savedPet = onSaveSpy.firstCall.args[0];
    expect(savedPet.label).to.equal('outdoor');
  });

  it('renders a label element or placeholder for the label field', () => {
    const wrapper = mount(
      <MemoryRouter>
        <PetEditor
          ownerId={1}
          pet={mockPet}
          petTypes={mockPetTypes}
          onSave={sinon.spy()}
        />
      </MemoryRouter>
    );

    // Check for a label element with text 'Label' or a placeholder
    const labelText = wrapper.find('label').filterWhere(l =>
      l.text().toLowerCase().includes('label')
    );
    const labelInput = wrapper.find('input[name="label"]');
    const hasPlaceholder = labelInput.length > 0 &&
      (labelInput.prop('placeholder') || '').toLowerCase().includes('label');

    expect(labelText.length > 0 || hasPlaceholder).to.equal(true);
  });

  it('submits empty label when no label is entered', () => {
    const onSaveSpy = sinon.spy();
    const newPet = {
      id: undefined,
      name: 'Buddy',
      birthDate: '2021-05-10',
      typeId: 2,
      label: '',
    };

    const wrapper = mount(
      <MemoryRouter>
        <PetEditor
          ownerId={1}
          pet={newPet}
          petTypes={mockPetTypes}
          onSave={onSaveSpy}
        />
      </MemoryRouter>
    );

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onSaveSpy.called).to.equal(true);
    const savedPet = onSaveSpy.firstCall.args[0];
    // label should be empty string or undefined when not filled
    expect(savedPet.label === '' || savedPet.label === undefined).to.equal(true);
  });
});
