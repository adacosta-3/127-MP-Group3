import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Login from "./login.jsx";
import Customization from "./customization.jsx";

const App = () => {
    return (
        <Router>
            <div style={{ padding: "20px" }}>
                <h1>Debugging Navigation</h1>
                <div style={{ marginBottom: "20px" }}>
                    <Link to="/">
                        <button style={{ marginRight: "10px" }}>Login</button>
                    </Link>
                    <Link to="/customization">
                        <button>Customization</button>
                    </Link>
                </div>
            </div>
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/customization" element={<Customization />} />
            </Routes>
        </Router>
    );
};

export default App;
