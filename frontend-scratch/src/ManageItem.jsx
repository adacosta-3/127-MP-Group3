import React, { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Divider from '@mui/material/Divider';
import Input from '@mui/material/Input';
import InputAdornment from '@mui/material/InputAdornment'; 
import axios from 'axios';
import DeleteIcon from '@mui/icons-material/Delete';
import SendIcon from '@mui/icons-material/Send';

const ManageItems = () => {
  const [filteredCategories, setFilteredCategories] = useState([]);
  const [itemType, setItemType] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [items, setItems] = useState([]);
  const [newItem, setNewItem] = useState({
    name: '',
    basePrice: '',
    hasSizes: false,
    sizes: [],
  });
  const [isEditing, setIsEditing] = useState(false);
  const [editingItem, setEditingItem] = useState(null);

  useEffect(() => {
    if (itemType && selectedCategory) {
      const fetchItems = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8081/api/items/category/${selectedCategory}`
          );
          setItems(response.data);
        } catch (error) {
          console.error('Error fetching items:', error);
        }
      };
      fetchItems();
    } else {
      setItems([]);
    }
  }, [itemType, selectedCategory]);

  useEffect(() => {
    if (itemType) {
      const fetchFilteredCategories = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8081/api/categories/item-type/${itemType}`
          );
          setFilteredCategories(response.data);
        } catch (error) {
          console.error('Error fetching filtered categories:', error);
        }
      };
      fetchFilteredCategories();
    } else {
      setFilteredCategories([]);
    }
  }, [itemType]);

  const handleChangeCategory = (event) => {
    setSelectedCategory(event.target.value);
  };

  const handleChangeItemType = (event) => {
    setItemType(event.target.value);
    setSelectedCategory('');
  };

  const handleDeleteItem = async (itemCode) => {
    try {
      await axios.delete(`http://localhost:8081/api/items/code/${itemCode}`);
      setItems(items.filter((item) => item.itemCode !== itemCode));
    } catch (error) {
      console.error('Error deleting item:', error);
    }
  };

  const handleEditItem = (itemCode) => {
    const item = items.find((item) => item.itemCode === itemCode);
    
    // Ensure the item has sizes or initialize an empty array if none
    setNewItem({
      ...item,
      sizes: item.sizes || [{ sizeName: '', priceAdjustment: '' }], // Default size if empty
      hasSizes: item.sizes && item.sizes.length > 0,
    });
  
    setEditingItem(item); // Set the editing state
    setIsEditing(true); // Trigger edit mode
  };
  
  
  
  const handleSizeChange = (index, field, value) => {
    const updatedSizes = [...newItem.sizes];  // Make a shallow copy of sizes
    updatedSizes[index] = { ...updatedSizes[index], [field]: value }; // Update the specific size field
    setNewItem((prev) => ({ ...prev, sizes: updatedSizes })); // Set the updated sizes state
  };
  

  const handleAddSize = () => {
    // Add a new empty size object to the sizes array
    setNewItem((prevState) => ({
      ...prevState,
      sizes: [...prevState.sizes, { sizeName: '', priceAdjustment: '' }], // Add an empty size
    }));
  };
  
  
  const handleUpdateSize = (index) => {
    const updatedSizes = [...newItem.sizes];
    const updatedSize = updatedSizes[index];
  
    // If no changes are made, don't update
    if (
      updatedSize.sizeName === originalSizes[index].sizeName &&
      updatedSize.priceAdjustment === originalSizes[index].priceAdjustment
    ) {
      return; // Do nothing if there is no change
    }
  
    // Update size only if it has been modified
    updatedSizes[index] = updatedSize;
    setNewItem((prevState) => ({
      ...prevState,
      sizes: updatedSizes,
    }));
  
    // Optionally, send update to backend if needed
    // updateSizeInBackend(updatedSize);
  };
  
  const handleAddItem = async () => {
    // Check if the item name and price are provided
    if (!newItem.name || !newItem.basePrice) {
      alert('Cannot add item due to incomplete fields: Item Name and Base Price are required.');
      return;
    }

    // If 'Has Size' is selected, ensure each size has both a name and an additional cost
    if (newItem.hasSizes) {
      for (const size of newItem.sizes) {
        if (!size.sizeName || !size.priceAdjustment) {
          alert('Cannot add item due to incomplete fields: Each size must have a name and an additional cost.');
          return;
        }
      }
    }

    try {
      // Send request to add the item
      const response = await axios.post('http://localhost:8081/api/items', {
        ...newItem,
        categoryId: selectedCategory,
      });

      // Add the newly created item to the list
      setItems([...items, response.data]);

      // If item has sizes, save sizes
      if (newItem.hasSizes) {
        const sizeData = newItem.sizes.map((size) => ({
          sizeName: size.sizeName,
          priceAdjustment: parseFloat(size.priceAdjustment) || 0.0,
        }));

        try {
          await axios.put(`http://localhost:8081/api/items/sizes/${response.data.itemCode}`, sizeData);
          console.log('Sizes saved successfully');
        } catch (error) {
          console.error('Error saving sizes:', error);
        }
      }

      // Reset the form fields
      setNewItem({ name: '', basePrice: '', hasSizes: false, sizes: [] });
    } catch (error) {
      console.error('Error adding item:', error);
      alert('Error adding item. Please try again.');
    }
  };
  const handleUpdateItem = async () => {
    try {
      // Ensure no duplicates in sizes
      const uniqueSizes = [
        ...newItem.sizes.reduce((map, size) => map.set(size.sizeName, size), new Map()).values(),
      ];
  
      const updatedItemData = {
        itemCode: editingItem.itemCode,
        name: newItem.name,
        basePrice: newItem.basePrice,
        categoryId: editingItem.categoryId,
        sizes: uniqueSizes, // Send only unique sizes
      };
  
      // Update the item with sizes
      const updateItemResponse = await axios.put(
        `http://localhost:8081/api/items/code/${editingItem.itemCode}`,
        updatedItemData
      );
  
      if (updateItemResponse.status !== 200) {
        console.error("Error updating item:", updateItemResponse.data);
        return;
      }
  
      // Now send sizes to the backend (ensuring no duplicates)
      const modifySizesResponse = await axios.put(
        `http://localhost:8081/api/items/sizes/modify/${editingItem.itemCode}`,
        uniqueSizes
      );
  
      if (modifySizesResponse.status !== 200) {
        console.error("Error modifying item sizes:", modifySizesResponse.data);
        return;
      }
  
      alert("Item updated successfully!");
      setEditingItem(null);
      setIsEditing(false);
  
      // Refresh item list
      const response = await axios.get(
        `http://localhost:8081/api/items/category/${selectedCategory}`
      );
      setItems(response.data);
  
      // Make sure the sizes are updated correctly in the UI
      const updatedItem = response.data.find((item) => item.itemCode === editingItem.itemCode);
      setNewItem(updatedItem); // This ensures that the updated item, including sizes, is set to state
    } catch (error) {
      console.error("Error updating item:", error);
      alert("Failed to update item. Please try again.");
    }
  };
  
  

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', minHeight: 'calc(100vh - 64px)', padding: 2, paddingTop: '10vh', position: 'relative', marginTop: '64px', width: '70vh' }}>
      <form>
        {/* Item Type and Category Selectors */}
        <Grid container spacing={2} justifyContent="center" sx={{ mb: 3 }}>
          <Grid item xs={5} sx={{ minWidth: 250 }}>
            <FormControl fullWidth>
              <InputLabel id="item-type-select-label">Item Type</InputLabel>
              <Select
                labelId="item-type-select-label"
                id="item-type-select"
                value={itemType}
                label="Item Type"
                onChange={handleChangeItemType}
              >
                <MenuItem value="Food">Food</MenuItem>
                <MenuItem value="Merchandise">Merchandise</MenuItem>
                <MenuItem value="Drink">Drink</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={5} sx={{ minWidth: 250 }}>
            <FormControl fullWidth>
              <InputLabel id="category-select-label">Category</InputLabel>
              <Select
                labelId="category-select-label"
                id="category-select"
                value={selectedCategory}
                label="Category"
                onChange={handleChangeCategory}
                disabled={!itemType}
              >
                {filteredCategories.length > 0 ? (
                  filteredCategories.map((category) => (
                    <MenuItem key={category.categoryId} value={category.categoryId}>
                      {category.name || 'Unnamed Category'}
                    </MenuItem>
                  ))
                ) : (
                  <MenuItem value="" disabled>No categories available</MenuItem>
                )}
              </Select>
            </FormControl>
          </Grid>
        </Grid>

        {/* Item Name and Base Price */}
        <Grid container spacing={2} justifyContent="center" sx={{ mb: 3 }}>
          <Grid item xs={5} sx={{ minWidth: 250 }}>
            <Input
              placeholder="Item Name"
              fullWidth
              value={newItem.name}
              onChange={(e) =>
                setNewItem((prev) => ({ ...prev, name: e.target.value }))
              }
              sx={{ marginTop: 2 }}
            />
          </Grid>

          <Grid item xs={5} sx={{ minWidth: 250 }}>
            <FormControl fullWidth sx={{ m: 0 }} variant="standard">
              <InputLabel htmlFor="standard-adornment-amount">Amount</InputLabel>
              <Input
                id="standard-adornment-amount"
                value={newItem.basePrice}
                onChange={(e) =>
                  setNewItem((prev) => ({ ...prev, basePrice: e.target.value }))
                }
                startAdornment={<InputAdornment position="start">₱</InputAdornment>}
                sx={{ marginTop: 0 }}
              />
            </FormControl>
          </Grid>
        </Grid>

        {/* Has Sizes Button */}
        <Grid container justifyContent="center" sx={{ mt: 2, mb: 2 }}>
          <Grid item xs={5} sx={{ minWidth: 250 }}>
            <Button
              variant={newItem.hasSizes ? 'contained' : 'outlined'}
              onClick={() =>
                setNewItem((prev) => ({
                  ...prev,
                  hasSizes: !prev.hasSizes,
                  sizes: !prev.hasSizes ? [{ sizeName: '', priceAdjustment: '' }] : [],
                }))
              }
              fullWidth
            >
              Has Size
            </Button>
          </Grid>
        </Grid>

        {/* Size Inputs */}
        {newItem.hasSizes &&
          newItem.sizes.map((size, index) => (
            <Grid container spacing={2} key={index} sx={{ mb: 2, alignItems: 'center', justifyContent: 'center' }}>
              <Grid item xs={5} sx={{ display: 'flex', justifyContent: 'center' }}>
                <Input
                  placeholder="Size Name"
                  fullWidth
                  value={size.sizeName}
                  onChange={(e) => handleSizeChange(index, 'sizeName', e.target.value)}
                  sx={{ marginTop: 1.75, height: '3vh' }}
                />
              </Grid>

              <Grid item xs={5} sx={{ display: 'flex', justifyContent: 'center' }}>
                <FormControl fullWidth sx={{ m: 0 }} variant="standard">
                  <InputLabel htmlFor="standard-adornment-amount">Additional Cost</InputLabel>
                  <Input
                    id="standard-adornment-amount"
                    value={size.priceAdjustment}
                    onChange={(e) => handleSizeChange(index, 'priceAdjustment', e.target.value)}
                    startAdornment={<InputAdornment position="start">₱</InputAdornment>}
                    sx={{ height: '3vh' }}
                  />
                </FormControl>
              </Grid>
            </Grid>
          ))}

        {newItem.hasSizes && (
          <Grid container justifyContent="center" sx={{ mt: 2 }}>
            <Button variant="text" onClick={handleAddSize}>
              + Add Size
            </Button>
          </Grid>
        )}

        {/* Add/Update Item Button */}
        <Grid container justifyContent="center" sx={{ mt: 3 }}>
          <Grid item>
            <Button
              variant="contained"
              color="primary"
              onClick={isEditing ? handleUpdateItem : handleAddItem}
            >
              {isEditing ? 'Update Item' : 'Add Item'}
            </Button>
          </Grid>
        </Grid>

        {/* Items List */}
        <Box sx={{ mt: 3, width: '70vh', maxWidth: '90%' }}>
          {items.length > 0 ? (
            <ul>
              {items.map((item) => (
                <Box key={item.itemCode} sx={{ width: '100%', mb: 2 }}>
                  <Box
                    sx={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      padding: 1,
                      border: '1px solid #ddd',
                      borderRadius: '4px',
                      backgroundColor: '#f5f5f5',
                    }}
                  >
                    <Box sx={{ flex: 1, marginRight: '20px' }}>
                      <strong>{item.name}</strong> - ₱{item.basePrice}
                    </Box>
                    <Box sx={{ display: 'flex' }}>
                      <Button
                        variant="outlined"
                        startIcon={<DeleteIcon />}
                        onClick={() => handleDeleteItem(item.itemCode)}
                        sx={{ ml: 2 }}
                      >
                        Delete
                      </Button>
                      <Button
                        variant="contained"
                        endIcon={<SendIcon />}
                        onClick={() => handleEditItem(item.itemCode)}
                        sx={{ ml: 2 }}
                      >
                        Edit
                      </Button>
                    </Box>
                  </Box>
                  <Divider sx={{ mt: 1 }} />
                </Box>
              ))}
            </ul>
          ) : (
            <p>No items found for the selected category.</p>
          )}
        </Box>
      </form>
    </Box>
  );
};

export default ManageItems;
