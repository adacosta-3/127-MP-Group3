import { StrictMode, useState } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import Login from './Login.jsx';
import Manager from './Manager.jsx';
const ManagerDashboard = () => {
  return <Manager/>;
};

const AdminDashboard = () => {
  return <div>Welcome, Admin!</div>;
};

const CashierDashboard = () => {
  return <div>Welcome, Cashier!</div>;
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

  return <div>{renderContent()}</div>;
};

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Main />
  </StrictMode>,
);
