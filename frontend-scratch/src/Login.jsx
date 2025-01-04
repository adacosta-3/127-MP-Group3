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
  const [setupPasswordMode, setSetupPasswordMode] = useState(false); // Toggle for setup password form

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!username || !password) {
      setError('Please enter both username and password.');
      return;
    }

    try {
      const response = await axios.get(`http://localhost:8081/api/users/username/${username}`);
      
      if (response.data) {
        if (response.data.password === password || password === "") {
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

  const handlePasswordSetup = async (e) => {
    e.preventDefault();

    if (!username || !password) {
      setError('Please enter both username and password.');
      return;
    }

    try {
      const currentUserResponse = await axios.get(`http://localhost:8081/api/users/username/${username}`);
      const currentUser = currentUserResponse.data;
  
      if (!currentUser) {
        setError('User not found');
        return;
      }
  
      const userData = {
        userId: currentUser.userId,
        username: currentUser.username,
        password, // New password to be updated
        role: currentUser.role, // Retain the existing role
      };
  
      const response = await axios.put(`http://localhost:8081/api/users/${currentUser.userId}`, userData);
  
      if (response.status === 200) {
        setError('');
        alert('Password set successfully!');
        setSetupPasswordMode(false); // Close the setup password form
      } else {
        setError('Failed to set password. Please try again.');
      }
    } catch (err) {
      console.error(err.response || err);
      setError('Failed to set password. Please try again.');
    }
  };
  

  return (
    <div style={styles.container}>
      <h1 style={styles.heading}>Login</h1>
      
      {/* Normal Login Form */}
      {!setupPasswordMode && (
        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.inputGroup}>
            <TextField
              id="outlined-basic"
              label="Username"
              variant="outlined"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              fullWidth
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
                label="Password"
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

          {/* Set Up Password Button */}
          <Button
            style={styles.setupPasswordButton}
            onClick={() => setSetupPasswordMode(true)}
          >
            Set Up Password
          </Button>
        </form>
      )}

      {/* Set Up Password Form (Overlay) */}
      {setupPasswordMode && (
        <div style={styles.overlay}>
          <form onSubmit={handlePasswordSetup} style={styles.setupForm}>
            <h2>Set Up Password</h2>
            
            <div style={styles.inputGroup}>
              <TextField
                label="Username"
                variant="outlined"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                fullWidth
              />
            </div>

            <div style={styles.inputGroup}>
              <TextField
                label="New Password"
                variant="outlined"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                fullWidth
              />
            </div>

            {error && <div style={styles.error}>{error}</div>}

            <div style={styles.buttonGroup}>
              <Button
                type="submit"
                variant="contained" // Changed to contained button for set password
                style={styles.setPasswordButton}
              >
                Set Password
              </Button>

              {/* Cancel Button (Text) */}
              <Button
                variant="text"
                onClick={() => setSetupPasswordMode(false)}
                style={styles.cancelButton}
              >
                Cancel
              </Button>
            </div>
          </form>
        </div>
      )}
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
    marginBottom: '20px',
  },
  label: {
    fontFamily: 'Varela Round, sans-serif',
    fontSize: '.9rem',
    marginBottom: '5px',
    display: 'block',
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
  setupPasswordButton: {
    width: '100%',
    padding: '10px',
    backgroundColor: '#28a745',
    color: '#fff',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    marginTop: '20px',
  },
  overlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    zIndex: 1000,
  },
  setupForm: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'stretch',
    width: '30%',
    padding: '20px',
    borderRadius: '8px',
    backgroundColor: '#fff',
    boxShadow: '0 0 10px rgba(0, 0, 0, 0.1)',
    textAlign: 'center',
  },
  buttonGroup: {
    display: 'flex',
    justifyContent: 'space-between',
    marginTop: '20px',
  },
  cancelButton: {
    width: '30%',
    color: '#dc3545', // Red color for the cancel button
    padding: 0,
    fontWeight: 'bold',
  },
  setPasswordButton: {
    width: '70%', // Adjusted to fit both buttons side by side
    padding: '10px',
    backgroundColor: '#007bff',
    color: '#fff',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
  },
};

export default Login;
