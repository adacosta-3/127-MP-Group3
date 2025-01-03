import React, { useState } from 'react';
import AppBarComponent from './AppBar';
import ManageItems from './ManageItem';
import ManageCustomizations from './ManageCustomization'; // Your ManageCustomizations component

const Manager = () => {
  const [selectedView, setSelectedView] = useState('ManageItems'); // Default view is ManageItems

  // Handle button clicks to set the view
  const handleViewChange = (view) => {
    setSelectedView(view);
  };

  return (
    <div>
      {/* AppBar */}
      <AppBarComponent handleViewChange={handleViewChange} />

      {/* Content Area */}
      <div style={{ marginTop: '10px' }}>
        {selectedView === 'ManageItem' && <ManageItems />}
        {selectedView === 'ManageCustomization' && <ManageCustomizations />}
      </div>
    </div>
  );
};

export default Manager;
