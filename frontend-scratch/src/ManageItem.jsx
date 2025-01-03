import React, { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import axios from 'axios';

export default function ManageItems() {
  const [filteredCategories, setFilteredCategories] = useState([]); // To store filtered categories by item type
  const [age, setAge] = useState('');
  const [itemType, setItemType] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');

  // Fetch categories by item type when the item type is selected
  useEffect(() => {
    if (itemType) {
      const fetchFilteredCategories = async () => {
        try {
          const response = await axios.get(`http://localhost:8081/api/categories/item-type/${itemType}`);
          console.log("Fetched categories for item type: ", response.data); // Log the response for debugging
          setFilteredCategories(response.data); // Assuming the API returns filtered categories
        } catch (error) {
          console.error('Error fetching filtered categories:', error);
        }
      };
      fetchFilteredCategories();
    } else {
      setFilteredCategories([]); // Reset if no item type is selected
    }
  }, [itemType]);

  const handleChangeCategory = (event) => {
    setSelectedCategory(event.target.value);
  };

  const handleChangeAge = (event) => {
    setAge(event.target.value);
  };

  const handleChangeItemType = (event) => {
    setItemType(event.target.value);
    setSelectedCategory(''); // Reset selected category when item type changes
  };

  return (
    <Box sx={{ minWidth: 120 }}>
      <form>
        {/* Category Dropdown */}
        <FormControl fullWidth sx={{ mb: 3 }}>
          <InputLabel id="category-select-label">Category</InputLabel>
          <Select
            labelId="category-select-label"
            id="category-select"
            value={selectedCategory}
            label="Category"
            onChange={handleChangeCategory}
            disabled={!itemType} // Disable category dropdown if no item type is selected
          >
            {filteredCategories.length > 0 ? (
              filteredCategories.map((category) => (
                <MenuItem key={category.category_id} value={category.category_name}>
                  {category.category_name}
                </MenuItem>
              ))
            ) : (
              <MenuItem value="">No categories available</MenuItem>
            )}
          </Select>
        </FormControl>

        {/* Age Dropdown (Just for example purposes) */}
        <FormControl fullWidth sx={{ mb: 3 }}>
          <InputLabel id="age-select-label">Age</InputLabel>
          <Select
            labelId="age-select-label"
            id="age-select"
            value={age}
            label="Age"
            onChange={handleChangeAge}
          >
            <MenuItem value={10}>Ten</MenuItem>
            <MenuItem value={20}>Twenty</MenuItem>
            <MenuItem value={30}>Thirty</MenuItem>
          </Select>
        </FormControl>

        {/* Item Type Dropdown */}
        <FormControl fullWidth sx={{ mb: 3 }}>
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
      </form>
    </Box>
  );
}
