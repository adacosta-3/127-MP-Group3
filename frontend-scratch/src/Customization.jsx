import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { TextField, Button, FormControl, InputLabel, Select, MenuItem } from '@mui/material';
import './ManageCustomizations.css'; // External CSS for styling

const ManageCustomizations = () => {
    const [customizations, setCustomizations] = useState([]);
    const [items, setItems] = useState([]);
    const [newCustomization, setNewCustomization] = useState({ name: '', itemId: '' });
    const [newOption, setNewOption] = useState({ optionName: '', additionalCost: '' });
    const [editingOption, setEditingOption] = useState(null);
    const [editingCustomization, setEditingCustomization] = useState(null); // For editing customization name
    const API_BASE_URL = 'http://localhost:8081';

    useEffect(() => {
        fetchCustomizations();
        fetchItems();
    }, []);

    const fetchCustomizations = async () => {
        try {
            const response = await axios.get(`${API_BASE_URL}/api/customizations`);
            setCustomizations(response.data);
        } catch (error) {
            console.error('Error fetching customizations:', error);
        }
    };

    const fetchItems = async () => {
        try {
            const response = await axios.get(`${API_BASE_URL}/api/items`);
            setItems(response.data);
        } catch (error) {
            console.error('Error fetching items:', error);
        }
    };

    const handleAddCustomization = async () => {
        try {
            const response = await axios.post(`${API_BASE_URL}/api/customizations`, newCustomization);
            setCustomizations([...customizations, { ...response.data, options: [] }]);
            setNewCustomization({ name: '', itemId: '' });
        } catch (error) {
            console.error('Error adding customization:', error);
        }
    };

    const handleAddOption = async (customizationId) => {
        try {
            const options = [{ ...newOption }]; // API expects an array
            const response = await axios.post(`${API_BASE_URL}/api/customizations/${customizationId}/options`, options);
            setCustomizations(customizations.map(c =>
                c.customizationId === customizationId
                    ? { ...c, options: [...c.options, ...response.data] }
                    : c
            ));
            setNewOption({ optionName: '', additionalCost: '' });
        } catch (error) {
            console.error('Error adding option:', error);
        }
    };

    const handleDeleteCustomization = async (customizationId) => {
        try {
            await axios.delete(`${API_BASE_URL}/api/customizations/${customizationId}`);
            setCustomizations(customizations.filter(c => c.customizationId !== customizationId));
        } catch (error) {
            console.error('Error deleting customization:', error);
        }
    };

    // Function to delete an option
    const handleDeleteOption = async (customizationId, optionId) => {
        try {
            // API request to delete the option
            await axios.delete(`${API_BASE_URL}/api/customization-options/${optionId}`);
            // Update the local state to remove the deleted option
            setCustomizations(customizations.map(c =>
                c.customizationId === customizationId
                    ? { ...c, options: c.options.filter(o => o.optionId !== optionId) }
                    : c
            ));
        } catch (error) {
            console.error('Error deleting option:', error);
        }
    };

    // Function to edit customization (Name)
    const handleEditCustomization = async () => {
        if (!editingCustomization) return;
        try {
            // API request to update customization name
            const response = await axios.put(`${API_BASE_URL}/api/customizations/${editingCustomization.customizationId}`, {
                itemId: editingCustomization.itemId,
                name: editingCustomization.name
            });
            // Update the local state with the new name
            setCustomizations(customizations.map(c =>
                c.customizationId === editingCustomization.customizationId
                    ? { ...c, name: response.data.name }
                    : c
            ));
            setEditingCustomization(null); // Close the editing popup
        } catch (error) {
            console.error('Error editing customization name:', error);
        }
    };

    // Function to edit an option
    const handleEditOption = async (customizationId, optionId) => {
        if (!editingCustomization) return;
        try {
            // API request to update customization name
            const response = await axios.delete(`${API_BASE_URL}/api/customization-options/${optionId}`);
            setCustomizations(customizations.map(c =>
                c.customizationId === customizationId
                    ? { ...c, options: [...c.options, ...response.data] }
                    : c
            ));
            setNewOption({ optionName: '', additionalCost: '' });
        } catch (error) {
            console.error('Error adding option:', error);
        }
    };



    return (
        <div className="manage-customizations">
            <div className="header-tabs">
                <Button variant="contained" className="tab active">Manage Customizations</Button>
            </div>

            <div className="form-section">
                <h2>Add Customization</h2>
                <TextField
                    label="Customization Name"
                    variant="standard"
                    InputLabelProps={{ shrink: true }} // Ensures label doesn't overlap with text
                    value={newCustomization.name}
                    onChange={(e) => setNewCustomization({ ...newCustomization, name: e.target.value })}
                />
                <FormControl fullWidth>
                    <InputLabel id="item-select-label">Associated Item</InputLabel>
                    <Select
                        labelId="item-select-label"
                        value={newCustomization.itemId}
                        onChange={(e) => setNewCustomization({ ...newCustomization, itemId: e.target.value })}
                    >
                        {items.map(item => (
                            <MenuItem key={item.itemId} value={item.itemId}>
                                {`${item.name} (ID ${item.itemId})`}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                <Button variant="contained" onClick={handleAddCustomization}>
                    Add Customization
                </Button>
            </div>

            <div className="customizations-list">
                <h2>Customizations List</h2>
                {customizations.map(customization => (
                    <div key={customization.customizationId} className="customization-card">
                        <div className="customization-details">
                            <h3>{customization.name}</h3>
                            <p>Associated Item: {items.find(i => i.itemId === customization.itemId)?.name} (ID {customization.itemId})</p>
                            <Button variant="contained" onClick={() => handleDeleteCustomization(customization.customizationId)}>Delete Customization</Button>
                            {/* Edit Button */}
                            <Button variant="contained" onClick={() => setEditingCustomization({ ...customization })}>
                                Edit Name
                            </Button>
                        </div>

                        <div className="options-section">
                            <h4>Options:</h4>
                            {customization.options && customization.options.length > 0 ? (
                                customization.options.map(option => (
                                    <div key={option.optionId} className="option-item">
                                        <p>{option.optionName} (+${option.additionalCost})</p>
                                        <Button variant="contained" onClick={() => handleDeleteOption(customization.customizationId, option.optionId)}>
                                            Delete Option
                                        </Button>
                                        <Button variant="contained" onClick={() => setEditingOption({ ...option, customizationId: customization.customizationId })}>
                                            Edit Option
                                        </Button>
                                    </div>
                                ))
                            ) : (
                                <p>No options available.</p>
                            )}
                            <div className="add-option-form">
                                <TextField
                                    label="Option Name"
                                    variant="standard"
                                    value={newOption.optionName}
                                    onChange={(e) => setNewOption({ ...newOption, optionName: e.target.value })}
                                />
                                <TextField
                                    label="Additional Cost"
                                    type="number"
                                    variant="standard"
                                    value={newOption.additionalCost}
                                    onChange={(e) => setNewOption({ ...newOption, additionalCost: e.target.value })}
                                />
                                <Button variant="contained" onClick={() => handleAddOption(customization.customizationId)}>
                                    Add Option
                                </Button>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {editingCustomization && (
                <div className="editing-popup">
                    <div className="popup-content">
                        <h3>Edit Customization Name</h3>
                        <TextField
                            label="Customization Name"
                            variant="standard"
                            value={editingCustomization.name}
                            onChange={(e) => setEditingCustomization({ ...editingCustomization, name: e.target.value })}
                        />
                        <Button variant="contained" onClick={handleEditCustomization}>Save</Button>
                        <Button variant="contained" onClick={() => setEditingCustomization(null)}>Cancel</Button>
                    </div>
                </div>
            )}

            {editingOption && (
                <div className="editing-popup">
                    <div className="popup-content">
                        <h3>Edit Option</h3>
                        <TextField
                            label="Option Name"
                            variant="standard"
                            value={editingOption.optionName}
                            onChange={(e) => setEditingOption({ ...editingOption, optionName: e.target.value })}
                        />
                        <TextField
                            label="Additional Cost"
                            type="number"
                            variant="standard"
                            value={editingOption.additionalCost}
                            onChange={(e) => setEditingOption({ ...editingOption, additionalCost: e.target.value })}
                        />
                        <Button variant="contained" onClick={handleEditOption}>Save</Button>
                        <Button variant="contained" onClick={() => setEditingOption(null)}>Cancel</Button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ManageCustomizations;
