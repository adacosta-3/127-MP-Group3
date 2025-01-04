import React from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';

const AppBarComponent = ({ handleViewChange }) => {
  return (
    <AppBar position="fixed" sx={{ top: 0, left: 0, right: 0 }}>
      <Toolbar sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        <Box sx={{ display: 'flex', width: '30%' }}>
          <Button
            color="inherit"
            sx={{ flex: 1 }}
            onClick={() => handleViewChange('ManageItem')}
          >
            Manage Items
          </Button>
          <Button
            color="inherit"
            sx={{ flex: 1 }}
            onClick={() => handleViewChange('ManageCustomization')}
          >
            Manage Customizations
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default AppBarComponent;
