import React, { useState } from 'react';
import './LoginSignUp.css';

const LoginSignup = () => {
    const [action, setAction] = useState("Member Sign Up");
    const [formData, setFormData] = useState({
        name: "",
        birthdate: "",
        email: "",
        phone: "",
        password: ""
    });
    const [error, setError] = useState("");

    const handleSubmit = () => {
        setError(""); // Clear previous errors
        if (action === "Member Sign Up") {
            const { name, birthdate, email, phone } = formData;
            if (!name || !birthdate) {
                setError("Name and birthdate are required.");
            } else if (!email && !phone) {
                setError("Either email or phone (or both) is required.");
            }
        } else if (action === "Cashier Login") {
            const { email, password } = formData;
            if (!email || !password) {
                setError("Email and password are required.");
            }
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    return (
        <div className="login-signup-page">
            <div className='container'>
                <div className='header'>
                    <div className='text'>{action}</div>
                    <div className='underline'></div>
                </div>

                <div className='inputs'>
                    {action === "Cashier Login" ? (
                        <>
                            <div className='input'>
                                <i className="fas fa-envelope icon"></i>
                                <input
                                    type='email'
                                    name="email"
                                    placeholder="Enter your email"
                                    onChange={handleInputChange}
                                />
                            </div>
                            <div className='input'>
                                <i className="fas fa-lock icon"></i>
                                <input
                                    type='password'
                                    name="password"
                                    placeholder="Enter your password"
                                    onChange={handleInputChange}
                                />
                            </div>
                        </>
                    ) : (
                        <>
                            <div className='input'>
                                <i className="fas fa-user icon"></i>
                                <input
                                    type='text'
                                    name="name"
                                    placeholder="Enter your name"
                                    onChange={handleInputChange}
                                />
                            </div>
                            <div className='input'>
                                <i className="fas fa-calendar icon"></i>
                                <input
                                    type='date'
                                    name="birthdate"
                                    placeholder="Enter your birthdate"
                                    onChange={handleInputChange}
                                />
                            </div>
                            <div className='input'>
                                <i className="fas fa-envelope icon"></i>
                                <input
                                    type='email'
                                    name="email"
                                    placeholder="Enter your email"
                                    onChange={handleInputChange}
                                />
                            </div>
                            <div className='input'>
                                <i className="fas fa-phone icon"></i>
                                <input
                                    type='tel'
                                    name="phone"
                                    placeholder="Enter your phone number"
                                    onChange={handleInputChange}
                                />
                            </div>
                        </>
                    )}
                </div>

                {error && <div className="error-message">{error}</div>}

                <div className="submit-container">
                    <div
                        className={action === "Cashier Login" ? "submit gray" : "submit"}
                        onClick={() => setAction("Member Sign Up")}
                    >
                        Sign Up
                    </div>
                    <div
                        className={action === "Member Sign Up" ? "submit gray" : "submit"}
                        onClick={() => setAction("Cashier Login")}
                    >
                        Login
                    </div>
                    <button className="submit" onClick={handleSubmit}>
                        <i className="fas fa-check"></i>
                    </button>
                </div>
            </div>
        </div>
    );
};

export default LoginSignup;
