import { StrictMode, useState } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import { BrowserRouter as Router, Route, Routes, useNavigate } from 'react-router-dom';
import Login from './Login.jsx';
import Manager from './Manager.jsx';
import Admin from './Admin.jsx';
import Order from './PlaceOrder.jsx';
import Receipt from './Receipt.jsx';

const ManagerDashboard = () => {
  return <Manager />;
};

const AdminDashboard = () => {
  return <Admin />;
};

const CashierDashboard = () => {
  return <Order />;
};

const Main = () => {
  const [role, setRole] = useState(null); // Track user role

  // Conditionally render based on the user's role
  const renderContent = () => {
    if (role === 'Manager') {
      return <ManagerDashboard />;
    } else if (role === 'Admin') {
      return <AdminDashboard />;
    } else if (role === 'Cashier') {
      return <CashierDashboard />;
    } else {
      return <Login setRole={setRole} />; // If no role, show login
    }
  };

  return (
    <Router>
      <Routes>
        <Route path="/" element={renderContent()} />
        <Route path="/receipt" element={<Receipt />} />
        <Route path="/cashier" element={<CashierDashboard />} />
      </Routes>
    </Router>
  );
};

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Main />
  </StrictMode>
);
