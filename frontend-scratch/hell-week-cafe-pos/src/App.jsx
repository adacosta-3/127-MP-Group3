import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import LoginSignUp from './components/LoginSignUp';

const App = () => {
    return (
        <Router>
            <div>
                <Navbar />
                <div style={{ marginLeft: '250px', padding: '20px' }}>
                    <Routes>
                      <Route path="/" element={<HomePage />} />
                      <Route path="/loginsignup" element={<LoginSignUp />} />
                      
                    </Routes>
                </div>
            </div>
        </Router>
    );
};

export default App;
