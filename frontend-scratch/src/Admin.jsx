import React, { useState } from "react";
import axios from "axios";
import { TextField, Button, MenuItem, Select, InputLabel, FormControl, Container, Typography, Alert } from "@mui/material";

const Admin = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("Manager");
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    const userData = {
      username,
      password: password || "", // ensure password is not null
      role,
    };

    try {
      const response = await axios.post("http://localhost:8081/api/users", userData);
      if (response.status === 200) {
        setSuccess(true);
        setError("");
      }
    } catch (error) {
      console.error("Error creating user:", error);
      setError("Failed to create user. Please try again.");
      setSuccess(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Typography variant="h4" gutterBottom align="center">
        CREATE NEW USER
      </Typography>

      {/* Success and Error Alerts */}
      {success && (
        <Alert severity="success" style={{ marginBottom: "20px" }}>
          User created successfully!
        </Alert>
      )}
      {error && (
        <Alert severity="error" style={{ marginBottom: "20px" }}>
          {error}
        </Alert>
      )}

      {/* Form */}
      <form onSubmit={handleSubmit}>
        <TextField
          label="Username"
          variant="outlined"
          fullWidth
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          margin="normal"
        />

        <TextField
          label="Password (Optional)"
          variant="outlined"
          fullWidth
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          margin="normal"
        />

        <FormControl fullWidth variant="outlined" margin="normal">
          <InputLabel>Role</InputLabel>
          <Select
            value={role}
            onChange={(e) => setRole(e.target.value)}
            label="Role"
          >
            <MenuItem value="Manager">Manager</MenuItem>
            <MenuItem value="Cashier">Cashier</MenuItem>
          </Select>
        </FormControl>

        <Button
          type="submit"
          variant="contained"
          color="primary"
          fullWidth
          style={{ marginTop: "20px" }}
        >
          Create User
        </Button>
      </form>
    </Container>
  );
};

export default Admin;
