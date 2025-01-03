import React from 'react';
import { NavLink } from 'react-router-dom';
import './Navbar.css';
import logo from '../assets/Icons/MainLogo.png';

const Navbar = () => {
    return (
        <nav className='sidebar'>
            <div className='logo-container'>
                <NavLink to="/" exact className='logo-link'>
                    <img src={logo} alt="Logo" className='logo' />
                </NavLink>
            </div>
            <ul>
                <li>
                    <NavLink to="/" exact className='nav-link' activeClassName='active'>
                        <i className="fas fa-home"></i>
                        <span>Home</span>
                    </NavLink>
                </li>
                <li>
                    <NavLink to="/loginsignup" className='nav-link' activeClassName='active'>
                        <i className="fas fa-lock"></i>
                        <span>Login/Signup</span>
                    </NavLink>
                </li>
                <li>
                    <NavLink to="/order" className='nav-link' activeClassName='active'>
                        <i className="fas fa-shop"></i>
                        <span>Place Order</span>
                    </NavLink>
                </li>
            </ul>
        </nav>
    );
};

export default Navbar;
