import React, { useState } from 'react';
import axios from 'axios';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent the form from reloading the page

    try {
      // Send a GET request to check if the user exists by username
      const response = await axios.get(`http://localhost:8081/api/users/username/${username}`);
      
      // Check if user exists, verify password
      if (response.data) {
        if (response.data.password === password) {
          // If password matches, handle successful login
          console.log('Login successful');
          alert('Login successful!');
          // You can redirect or set the user in context
        } else {
          setError('Incorrect password');
        }
      }
    } catch (err) {
      // Handle errors, if user is not found or any other errors
      if (err.response && err.response.status === 404) {
        setError('User not found');
      } else {
        setError('An error occurred. Please try again later.');
      }
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.heading}>Login</h2>
      <form onSubmit={handleSubmit} style={styles.form}>
        <div style={styles.inputGroup}>
          <label style={styles.label}>Username</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            style={styles.input}
            placeholder="Enter your username"
          />
        </div>
        <div style={styles.inputGroup}>
          <label style={styles.label}>Password</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            style={styles.input}
            placeholder="Enter your password"
          />
        </div>
        {error && <div style={styles.error}>{error}</div>}
        <button type="submit" style={styles.button}>Login</button>
      </form>
    </div>
  );
};

const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    flexDirection: 'column',
    minHeight: '100vh',
    backgroundColor: '#f4f4f4',
  },
  heading: {
    marginBottom: '20px',
    fontSize: '2rem',
  },
  form: {
    width: '300px',
    padding: '20px',
    borderRadius: '8px',
    backgroundColor: '#fff',
    boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)',
  },
  inputGroup: {
    marginBottom: '15px',
  },
  label: {
    fontSize: '1rem',
    marginBottom: '5px',
    display: 'block',
  },
  input: {
    width: '100%',
    padding: '8px',
    fontSize: '1rem',
    borderRadius: '4px',
    border: '1px solid #ddd',
  },
  error: {
    color: 'red',
    marginBottom: '10px',
    fontSize: '0.9rem',
  },
  button: {
    width: '100%',
    padding: '10px',
    backgroundColor: '#007bff',
    color: '#fff',
    border: 'none',
    borderRadius: '4px',
    fontSize: '1rem',
    cursor: 'pointer',
  },
};

export default Login;
