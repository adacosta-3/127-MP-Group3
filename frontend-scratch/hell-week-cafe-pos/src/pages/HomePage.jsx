import React from 'react';
import '../components/Navbar.jsx';
import './Pages.css';
import './HomePage.css';

const HomePage = () => {
    return (
        <div>
            <h1>Cat-astrophe Café!</h1>
            <h2 style={{ fontSize: '36px' }}>Welcome to our cafe ಥ_ಥ</h2>
            <p>
                This is our final requirement for CMSC 127.
                This is being submitted to Inst. David Pasumbal.
            </p>

            <h2>Meet the Creators</h2>
            <div className="creators-container">
                
            </div>
        </div>
    );
};

export default HomePage;
