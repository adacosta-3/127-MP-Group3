import React, { useState, useEffect } from "react";
import axios from "axios";
import {
  TextField, Button, MenuItem, Select, InputLabel, FormControl, Container, Typography, Alert, Paper, Grid,
} from "@mui/material";
import { Line, Bar } from "react-chartjs-2";
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend } from "chart.js";

ChartJS.register(CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend);

const Admin = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("Manager");
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState("");
  const [viewDashboard, setViewDashboard] = useState(false);
  const [orderHistoryPerDay, setOrderHistoryPerDay] = useState([]);
  const [orderHistoryForMember, setOrderHistoryForMember] = useState([]);
  const [mostOrderedItems, setMostOrderedItems] = useState([]);
  const [leastOrderedItems, setLeastOrderedItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (viewDashboard) {
      fetchData();
    }
  }, [viewDashboard]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [orderHistoryPerDayData, orderHistoryForMemberData, mostOrderedItemsData, leastOrderedItemsData] = await Promise.all([
        axios.get("http://localhost:8081/api/admin/order-history-per-day"),
        axios.get("http://localhost:8081/api/admin/order-history/member/1"),
        axios.get("http://localhost:8081/api/admin/most-ordered-items"),
        axios.get("http://localhost:8081/api/admin/least-ordered-items"),
      ]);
      setOrderHistoryPerDay(orderHistoryPerDayData.data);
      setOrderHistoryForMember(orderHistoryForMemberData.data);
      setMostOrderedItems(mostOrderedItemsData.data);
      setLeastOrderedItems(leastOrderedItemsData.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8081/api/users", {
        username,
        password: password || "",
        role,
      });
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

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
      },
    },
  };

  const orderHistoryPerDayData = {
    labels: orderHistoryPerDay.map(item => item.orderDate),
    datasets: [{
      label: "Orders per Day",
      data: orderHistoryPerDay.map(item => item.orderCount),
      borderColor: 'rgba(75, 192, 192, 1)',
      backgroundColor: 'rgba(75, 192, 192, 0.2)',
    }],
  };

  const mostOrderedItemsData = {
    labels: mostOrderedItems.map(item => item.itemName),
    datasets: [{
      label: "Most Ordered Items",
      data: mostOrderedItems.map(item => item.totalQuantity),
      backgroundColor: 'rgba(75, 192, 192, 0.2)',
      borderColor: 'rgba(75, 192, 192, 1)',
      borderWidth: 1,
    }],
  };

  const leastOrderedItemsData = {
    labels: leastOrderedItems.map(item => item.itemName),
    datasets: [{
      label: "Least Ordered Items",
      data: leastOrderedItems.map(item => item.totalQuantity),
      backgroundColor: 'rgba(255, 99, 132, 0.2)',
      borderColor: 'rgba(255, 99, 132, 1)',
      borderWidth: 1,
    }],
  };

  return (
    <Container style={{ width: "100vw", height: "100vh", padding: 20, minHeight: "100vh", overflowY: "auto" }}>
      <Typography variant="h4" gutterBottom align="center">
        {viewDashboard ? "ADMIN DASHBOARD" : "CREATE NEW USER"}
      </Typography>

      {success && <Alert severity="success" style={{ marginBottom: "20px" }}>User created successfully!</Alert>}
      {error && <Alert severity="error" style={{ marginBottom: "20px" }}>{error}</Alert>}

      {viewDashboard ? (
        <Container style={{ width: "100%", padding: 0, height: "80%" }}>
          {loading ? (
            <Typography variant="h6" align="center">Loading...</Typography>
          ) : (
            <Grid container spacing={2} style={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-around', height: "100%" }}>
              <Grid item xs={12} md={6}>
                <Paper style={{ padding: 10, height: "90%" }}>
                  <Typography variant="h6" gutterBottom align="center">ORDER HISTORY PER DAY</Typography>
                  <Line data={orderHistoryPerDayData} options={chartOptions} />
                </Paper>
              </Grid>
              <Grid item xs={12} md={6}>
                <Paper style={{ padding: 10, height: "90%" }}>
                  <Typography variant="h6" gutterBottom align="center">MOST ORDERED ITEMS</Typography>
                  <Bar data={mostOrderedItemsData} options={chartOptions} />
                </Paper>
              </Grid>
              <Grid item xs={12} md={6}>
                <Paper style={{ padding: 10, height: "90%" }}>
                  <Typography variant="h6" gutterBottom align="center">LEAST ORDERED ITEMS</Typography>
                  <Bar data={leastOrderedItemsData} options={chartOptions} />
                </Paper>
              </Grid>
              <Grid item xs={12} md={6}>
                <Paper style={{ padding: 10, height: "90%" }}>
                  <Typography variant="h6" gutterBottom align="center">ORDER HISTORY OF MEMBERS</Typography>
                  <Grid container spacing={2}>
                    {orderHistoryForMember.length ? orderHistoryForMember.map(order => (
                      <Grid item xs={12} key={order.orderId}>
                        <Paper style={{ padding: 10 }}>
                          <Typography>Order ID: {order.orderId}</Typography>
                          <Typography>Date: {order.orderDate}</Typography>
                          <Typography>Total Price: ${order.totalPrice}</Typography>
                        </Paper>
                      </Grid>
                    )) : (
                      <Typography>No order history available for this member.</Typography>
                    )}
                  </Grid>
                </Paper>
              </Grid>
            </Grid>
          )}
        </Container>
      ) : (
        <form onSubmit={handleSubmit}>
          <TextField label="Username" variant="outlined" fullWidth value={username} onChange={(e) => setUsername(e.target.value)} required margin="normal" />
          <TextField label="Password (Optional)" variant="outlined" fullWidth type="password" value={password} onChange={(e) => setPassword(e.target.value)} margin="normal" />
          <FormControl fullWidth variant="outlined" margin="normal">
            <InputLabel>Role</InputLabel>
            <Select value={role} onChange={(e) => setRole(e.target.value)} label="Role">
              <MenuItem value="Manager">Manager</MenuItem>
              <MenuItem value="Cashier">Cashier</MenuItem>
            </Select>
          </FormControl>
          <Button type="submit" variant="contained" color="primary" fullWidth>Create User</Button>
        </form>
      )}

      <Button variant="contained" color="secondary" onClick={() => setViewDashboard(prev => !prev)} fullWidth style={{ marginTop: "20px" }}>
        {viewDashboard ? "Go Back to Create User" : "View Dashboard"}
      </Button>
    </Container>
  );
};

export default Admin;
