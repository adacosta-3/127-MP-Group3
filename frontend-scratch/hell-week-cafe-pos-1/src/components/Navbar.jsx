import React from 'react';
import { NavLink } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  return (
      <nav className='sidebar'>
        <ul>
          <li>
            <NavLink to="/" exact className='nav-link' activeClassName='active'>
              <i className="fas fa-home"></i>
              <span>Home</span>
            </NavLink>
          </li>
          <li>
            <NavLink to="/login" className='nav-link' activeClassName='active'>
              <i className="fas fa-lock"></i>
              <span>Login/Signup</span>
            </NavLink>
          </li>
          <li>
            <NavLink to="/dashboard" className='nav-link' activeClassName='active'>
              <i className="fas fa-columns"></i>
              <span>Dashboard</span>
            </NavLink>
          </li>
        </ul>
      </nav>
  );
};

export default Navbar;
