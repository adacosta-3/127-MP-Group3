import React, { useState } from 'react';
import AppBarComponent from './AppBar';
import ManageItems from './ManageItem';
import ManageCustomizations from './Customization'; // Your ManageCustomizations component

const Manager = () => {
  const [selectedView, setSelectedView] = useState('ManageItems'); // Default view is ManageItems

  // Handle button clicks to set the view
  const handleViewChange = (view) => {
    setSelectedView(view);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
      {/* AppBar with bottom margin */}
      <AppBarComponent handleViewChange={handleViewChange} style={{ marginBottom: '20px' }} />

      {/* Content Area with scroll and hidden scrollbar */}
      <main style={{ flexGrow: 1, overflowY: 'scroll', padding: '10px', scrollbarWidth: 'none', WebkitOverflowScrolling: 'touch' }}>
        {selectedView === 'ManageItems' && <ManageItems />}
        {selectedView === 'ManageCustomization' && <ManageCustomizations />}
      </main>
    </div>
  );
};

export default Manager;
