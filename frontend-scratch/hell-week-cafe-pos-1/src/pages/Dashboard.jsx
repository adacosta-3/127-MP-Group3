import React from "react";
import { Outlet } from "react-router-dom";
import SidebarLeft from "../components/Navbar";
import ShoppingCart from "../pages/ShoppingCart";
const Dashboard = () => {
  return (
    <>
      <div className="container">
        <div className="sidebarLeft">
          <SidebarLeft />
        </div>

        {/* Dynamic Page */}
        <div className="dashboard-content">
          <Outlet />
        </div>

        <div className="sidebarRight">
          <ShoppingCart />
        </div>
      </div>
    </>
  );
};

export default Dashboard;
