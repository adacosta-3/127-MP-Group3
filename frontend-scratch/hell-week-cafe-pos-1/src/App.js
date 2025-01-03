import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import Home from './pages/Home';
import Login from './pages/Login';
import Content from './pages/Content';
import CategoryProducts from './pages/CategoryProducts';


function App() {
    return (
        <>
            <Router>
                <div className="App">
                    <Routes>
                        <Route path='/' element={<Home />} />
                        <Route path='/login' element={<Login />} />
                        <Route path='/dashboard' element={<Dashboard />}>
                            <Route path='category/:category' element={<CategoryProducts />} />
                            <Route path='' element={<Content />} />
                        </Route>
                    </Routes>
                </div>
            </Router>
        </>
    );
}

export default App;
