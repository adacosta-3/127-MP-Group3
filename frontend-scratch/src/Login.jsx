import React, { useState } from 'react';
import axios from 'axios';
import { Button, InputAdornment, IconButton, OutlinedInput, InputLabel, FormControl, TextField } from '@mui/material'; // Import MUI components
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import './index.css';  // Keep your global CSS import

const Login = ({ setRole }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false); // For toggling password visibility

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.get(`http://localhost:8081/api/users/username/${username}`);
      
      if (response.data) {
        if (response.data.password === password) {
          console.log('Login successful');
          alert('Login successful!');
          setRole(response.data.role); // Assuming the response contains the role
        } else {
          setError('Incorrect password');
        }
      }
    } catch (err) {
      if (err.response && err.response.status === 404) {
        setError('User not found');
      } else {
        setError('An error occurred. Please try again later.');
      }
    }
  };

  const handleClickShowPassword = () => setShowPassword((show) => !show);
  const handleMouseDownPassword = (event) => event.preventDefault();

  return (
    <div style={styles.container}>
      <h1 style={styles.heading}>Login</h1>
      <form onSubmit={handleSubmit} style={styles.form}>
        <div style={styles.inputGroup}>
          <TextField
            id="outlined-basic"
            label="Username"
            variant="outlined"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            fullWidth // Adjusted for the username input
          />
        </div>

        <div style={styles.inputGroup}>
          <FormControl sx={{ width: '100%' }} variant="outlined">
            <InputLabel htmlFor="outlined-adornment-password">Password</InputLabel>
            <OutlinedInput
              id="outlined-adornment-password"
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              endAdornment={
                <InputAdornment position="end">
                  <IconButton
                    aria-label={showPassword ? 'Hide password' : 'Show password'}
                    onClick={handleClickShowPassword}
                    onMouseDown={handleMouseDownPassword}
                    edge="end"
                  >
                    {showPassword ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              }
              label="Password" // Added custom style for password input
            />
          </FormControl>
        </div>

        {error && <div style={styles.error}>{error}</div>}

        <Button 
          type="submit" 
          variant="outlined"
          style={styles.button}
        >
          SIGN IN
        </Button>
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
    padding: '0 20px',
    boxSizing: 'border-box',
  },
  
  heading: {
    marginBottom: '20px',
    fontSize: '2rem',
    fontFamily: 'Varela Round, sans-serif',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'stretch',
    width: '100%',
    padding: '20px',
    borderRadius: '8px',
    backgroundColor: '#fff',
    boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)',
  },
  inputGroup: {
    marginBottom: '20px', // Increased the margin between inputs
  },
  label: {
    fontFamily: 'Varela Round, sans-serif',
    fontSize: '.9rem',
    marginBottom: '5px',
    display: 'block',
  },
  input: {
    width: '100%',
    fontSize: '1rem',
    height: '5vh',
    padding: '10px',
    borderRadius: '4px',
    border: '1px solid #ddd',
    boxSizing: 'border-box',
    marginBottom: '15px', // Added bottom margin for better spacing
  },

  inputUsername: {
    width: '100%',
    fontSize: '1rem',
    height: '5vh',
    padding: '10px',
    borderRadius: '4px',
    boxSizing: 'border-box',
    marginBottom: '15px', // Added bottom margin for better spacing
    '& .MuiInputBase-input': {
      padding: '0 10px', // Adjust padding to align placeholder correctly
    }
  },
  
  inputPassword: {
    width: '100%',
    fontSize: '1rem',
    height: '5vh',
    padding: '10px',
    borderRadius: '4px',
    boxSizing: 'border-box',
    marginBottom: '15px', // Added bottom margin for better spacing
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
    cursor: 'pointer',
  },
};

export default Login;
