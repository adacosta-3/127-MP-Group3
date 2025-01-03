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
      await axios.delete(`http://localhost:8081/api/items/${itemCode}`);
      setItems(items.filter((item) => item.itemCode !== itemCode));
    } catch (error) {
      console.error('Error deleting item:', error);
    }
  };

  const handleEditItem = (itemCode) => {
    console.log('Edit item with code:', itemCode);
  };

  const handleSizeChange = (index, field, value) => {
    const updatedSizes = newItem.sizes.map((size, i) =>
      i === index ? { ...size, [field]: value } : size
    );
    setNewItem((prev) => ({ ...prev, sizes: updatedSizes }));
  };

  const handleAddSize = () => {
    setNewItem((prev) => ({
      ...prev,
      sizes: [...prev.sizes, { sizeName: '', priceAdjustment: '' }],
    }));
  };

  const handleAddItem = async () => {
    if (!newItem.name || !newItem.basePrice) {
      alert('Please fill in all fields');
      return;
    }

    try {
      // Add the item
      const response = await axios.post('http://localhost:8081/api/items', {
        ...newItem,
        categoryId: selectedCategory,
      });

      // Update the items list with the newly added item
      setItems([...items, response.data]);

      // If the item has sizes, save them after adding the item
      if (newItem.hasSizes) {
        const sizeData = newItem.sizes.map((size) => ({
          sizeName: size.sizeName,
          priceAdjustment: parseFloat(size.priceAdjustment) || 0.0,
        }));

        try {
          await axios.put(
            `http://localhost:8081/api/items/sizes/${response.data.itemCode}`,
            sizeData
          );
          console.log('Sizes saved successfully');
        } catch (error) {
          console.error('Error saving sizes:', error);
        }
      }

      // Reset the form after adding the item and sizes
      setNewItem({ name: '', basePrice: '', hasSizes: false, sizes: [] });
    } catch (error) {
      console.error('Error adding item:', error);
    }
  };

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        minHeight: 'calc(100vh - 64px)',
        padding: 2,
        paddingTop: '10vh',
        position: 'relative',
        marginTop: '64px',
        width: '70vh',
      }}
    >
      <form>
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
          {/* Item Name */}
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

          {/* Amount (Base Price) */}
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

        {/* Size and Price Inputs */}
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

        {/* Add Item Button */}
        <Grid container justifyContent="center" sx={{ mt: 3 }}>
          <Grid item>
            <Button variant="contained" color="primary" onClick={handleAddItem}>
              Add Item
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
