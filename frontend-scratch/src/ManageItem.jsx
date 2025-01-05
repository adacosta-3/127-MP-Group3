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
import Typography from '@mui/material/Typography';
import CloseIcon from '@mui/icons-material/Close';

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

  const handleEditItem = async (itemCode) => {
    const item = items.find((item) => item.itemCode === itemCode);
  
    // Fetch sizes for the item
    try {
      const response = await axios.get(`http://localhost:8081/api/items/sizes/${itemCode}`);
      
      // Set the item data and sizes
      setNewItem({
        ...item,
        sizes: response.data || [{ sizeName: '', priceAdjustment: '' }],
        hasSizes: response.data && response.data.length > 0,
      });
    } catch (error) {
      console.error("Error fetching item sizes:", error);
      setNewItem({
        ...item,
        sizes: [{ sizeName: '', priceAdjustment: '' }],
        hasSizes: false,
      });
    }
  
    setEditingItem(item);
    setIsEditing(true);
  };

  const handleSizeChange = (index, field, value) => {
    const updatedSizes = [...newItem.sizes];
    updatedSizes[index] = { ...updatedSizes[index], [field]: value };
    setNewItem((prev) => ({ ...prev, sizes: updatedSizes }));
  };

  const handleDeleteSize = async (index) => {
    const sizeId = newItem.sizes[index].sizeId; // Assuming `sizeId` is part of the size object
  
    try {
      // Send DELETE request to the backend to delete the size
      await axios.delete(`http://localhost:8081/api/items/sizes/${sizeId}`);
  
      // Remove the size from the state after successful deletion
      const updatedSizes = newItem.sizes.filter((_, i) => i !== index);
      setNewItem((prevItem) => ({
        ...prevItem,
        sizes: updatedSizes,
      }));
    } catch (error) {
      console.error("Error deleting size:", error);
      // Optionally handle error by showing a message to the user
    }
  };

  const handleAddSize = () => {
    setNewItem((prevState) => ({
      ...prevState,
      sizes: [...prevState.sizes, { sizeName: '', priceAdjustment: '' }],
    }));
  };

  const handleAddItem = async () => {
    if (!newItem.name || !newItem.basePrice) {
      alert('Cannot add item due to incomplete fields: Item Name and Base Price are required.');
      return;
    }

    if (newItem.hasSizes) {
      for (const size of newItem.sizes) {
        if (!size.sizeName || !size.priceAdjustment) {
          alert('Cannot add item due to incomplete fields: Each size must have a name and an additional cost.');
          return;
        }
      }
    }

    try {
      const response = await axios.post('http://localhost:8081/api/items', {
        ...newItem,
        categoryId: selectedCategory,
      });
      setItems([...items, response.data]);

      if (newItem.hasSizes) {
        const sizeData = newItem.sizes.map((size) => ({
          sizeName: size.sizeName,
          priceAdjustment: parseFloat(size.priceAdjustment) || 0.0,
        }));
        await axios.put(`http://localhost:8081/api/items/sizes/${response.data.itemCode}`, sizeData);
      }

      setNewItem({ name: '', basePrice: '', hasSizes: false, sizes: [] });
    } catch (error) {
      console.error('Error adding item:', error);
      alert('Error adding item. Please try again.');
    }
  };

  const handleUpdateItem = async () => {
    try {
      // Make sure sizes are unique
      const uniqueSizes = [
        ...newItem.sizes.reduce((map, size) => map.set(size.sizeName, size), new Map()).values(),
      ];
  
      // Prepare the updated item data
      const updatedItemData = {
        itemCode: editingItem.itemCode,
        name: newItem.name,
        basePrice: newItem.basePrice,
        categoryId: editingItem.categoryId,
        sizes: uniqueSizes,
      };
  
      // Update the item data
      const updateItemResponse = await axios.put(
        `http://localhost:8081/api/items/code/${editingItem.itemCode}`,
        updatedItemData
      );
  
      if (updateItemResponse.status !== 200) {
        console.error("Error updating item:", updateItemResponse.data);
        return;
      }
  
      // Modify the sizes for the item
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
  
      // Update the item in the state immediately
      const updatedItem = { ...editingItem, sizes: uniqueSizes }; // Merge updated sizes
      const updatedItems = items.map((item) =>
        item.itemCode === updatedItem.itemCode ? updatedItem : item
      );
      setItems(updatedItems); // Update state with modified item
      setNewItem(updatedItem); // Update the newly created item
    } catch (error) {
      console.error("Error updating item:", error);
      alert("Failed to update item. Please try again.");
    }
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: 2, minHeight: 'calc(100vh - 64px)', marginTop: '64px', width: '70vh' }}>
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

        <Grid container spacing={2} justifyContent="center" sx={{ mb: 3 }}>
          <Grid item xs={5} sx={{ minWidth: 250 }}>
            <Input
              placeholder="Item Name"
              fullWidth
              value={newItem.name}
              onChange={(e) => setNewItem((prev) => ({ ...prev, name: e.target.value }))}
              sx={{ marginTop: 2 }}
            />
          </Grid>

          <Grid item xs={5} sx={{ minWidth: 250 }}>
            <FormControl fullWidth sx={{ m: 0 }} variant="standard">
              <InputLabel htmlFor="standard-adornment-amount">Amount</InputLabel>
              <Input
                id="standard-adornment-amount"
                value={newItem.basePrice}
                onChange={(e) => setNewItem((prev) => ({ ...prev, basePrice: e.target.value }))}
                startAdornment={<InputAdornment position="start">₱</InputAdornment>}
                sx={{ marginTop: 0 }}
              />
            </FormControl>
          </Grid>
        </Grid>

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

        {newItem.hasSizes &&
  newItem.sizes.map((size, index) => (
    <Grid container spacing={2} justifyContent="center" sx={{ mb: 3 }} key={index}>
      <Grid item xs={4} sx={{ minWidth: 200 }}>
        <FormControl fullWidth variant="standard">
          <InputLabel htmlFor={`size-name-${index}`}>Size Name</InputLabel>
          <Input
            id={`size-name-${index}`}
            value={size.sizeName}
            onChange={(e) => handleSizeChange(index, 'sizeName', e.target.value)}
            placeholder="Size Name"
            fullWidth
          />
        </FormControl>
      </Grid>
      <Grid item xs={4} sx={{ minWidth: '5vh' }}>
        <FormControl fullWidth variant="standard">
          <InputLabel htmlFor={`price-adjustment-${index}`}>Price Adjustment</InputLabel>
          <Input
            id={`price-adjustment-${index}`}
            value={size.priceAdjustment}
            onChange={(e) => handleSizeChange(index, 'priceAdjustment', e.target.value)}
            startAdornment={<InputAdornment position="start">₱</InputAdornment>}
            fullWidth
          />
        </FormControl>
      </Grid>
      <Grid item xs={2} sx={{ display: 'flex', alignItems: 'center' }}>
        <Button
          variant="outlined"
          color="error"
          onClick={() => handleDeleteSize(index)}
          sx={{ minWidth: 'unset' }}
        >
          <DeleteIcon />
        </Button>
      </Grid>
    </Grid>
  ))}

        {newItem.hasSizes && (
          <Grid container justifyContent="center">
            <Grid item xs={5} sx={{ minWidth: 250 }}>
              <Button
                variant="outlined"
                onClick={handleAddSize}
                fullWidth
                sx={{ mt: 1 }}
              >
                Add Size
              </Button>
            </Grid>
          </Grid>
        )}

        {isEditing && (
          <Button
            onClick={() => setIsEditing(false)}
            sx={{
              position: 'absolute',
              top: 10,
              right: 10,
              color: 'gray',
              minWidth: 'unset',
              padding: 0,
            }}
          >
            <CloseIcon />
          </Button>
        )}

        <Grid container justifyContent="center" sx={{ mt: 3 }}>
          <Grid item>
            <Button variant="contained" color="primary" onClick={isEditing ? handleUpdateItem : handleAddItem}>
              {isEditing ? 'Update Item' : 'Add Item'}
            </Button>
          </Grid>
          <Grid item sx={{ ml: 2 }}>
            <Button
              variant="outlined"
              onClick={() => {
                setIsEditing(false);
                setNewItem({ name: '', basePrice: '', hasSizes: false, sizes: [] });
              }}
            >
              Cancel
            </Button>
          </Grid>
        </Grid>
      </form>

      <Divider sx={{ width: '100%', marginTop: 3, marginBottom: 3 }} />

      <Grid container spacing={2}>
        {items.map((item) => (
          <Grid item xs={12} sm={6} md={4} lg={3} key={item.itemCode}>
            <Box
              sx={{
                p: 2,
                border: '1px solid #ccc',
                borderRadius: 2,
                width: '14vh',
                height: '20vh',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                marginBottom: 2,
              }}
            >
              {/* Close button inside each item box */}
              <Button
                onClick={() => handleDeleteItem(item.itemCode)}
                sx={{
                  position: 'absolute',
                  top: 10,
                  right: 10,
                  color: 'gray',
                  minWidth: 'unset',
                  padding: 0,
                }}
              >
                <CloseIcon />
              </Button>
              <Box sx={{ flex: 0.45, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                <Typography variant="h6" sx={{ fontSize: '16px', fontWeight: 'bold', textAlign: 'center' }}>
                  {item.name}
                </Typography>
              </Box>

              <Box sx={{ flex: 0.5, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                <p style={{ textAlign: 'center' }}>{`₱${item.basePrice}`}</p>
              </Box>

              <Box sx={{ flex: 0.5, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'space-between' }}>
                <Button
                  variant="outlined"
                  color="error"
                  onClick={() => handleDeleteItem(item.itemCode)}
                  startIcon={<DeleteIcon />}
                  sx={{ width: '80%' }}
                >
                  Delete
                </Button>
                <Button
                  variant="outlined"
                  color="info"
                  onClick={() => handleEditItem(item.itemCode)}
                  sx={{ width: '80%', mt: 1 }}
                >
                  Edit
                </Button>
              </Box>
            </Box>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default ManageItems;
