import React, { useState, useEffect } from "react";
import axios from "axios";
import {
  TextField,
  Button,
  MenuItem,
  Select,
  InputLabel,
  FormControl,
  Container,
  Typography,
  Alert,
  Paper,
  Grid,
  List,
  ListItem,
  ListItemText,
} from "@mui/material";
import { Line, Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";


ChartJS.register(CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend);


const Admin = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("Manager");
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState("");
  const [viewDashboard, setViewDashboard] = useState(false);


  const [orderHistoryPerDay, setOrderHistoryPerDay] = useState([]);
  const [ordersByDate, setOrdersByDate] = useState([]);
  const [selectedDate, setSelectedDate] = useState("");
  const [orderHistoryForMember, setOrderHistoryForMember] = useState([]);
  const [mostOrderedItems, setMostOrderedItems] = useState([]);
  const [leastOrderedItems, setLeastOrderedItems] = useState([]);
  const [memberId, setMemberId] = useState("");
  const [loading, setLoading] = useState(true);


  useEffect(() => {
    if (viewDashboard) {
      fetchData();
    }
  }, [viewDashboard]);


  const fetchData = async () => {
    setLoading(true);
    try {
      const [orderHistoryPerDayData, mostOrderedItemsData, leastOrderedItemsData] = await Promise.all([
        axios.get("http://localhost:8081/api/admin/order-history-per-day"),
        axios.get("http://localhost:8081/api/admin/most-ordered-items"),
        axios.get("http://localhost:8081/api/admin/least-ordered-items"),
      ]);
      setOrderHistoryPerDay(orderHistoryPerDayData.data);
      setMostOrderedItems(mostOrderedItemsData.data);
      setLeastOrderedItems(leastOrderedItemsData.data);
    } catch (err) {
      console.error("Error fetching dashboard data:", err);
      setError("Failed to fetch dashboard data.");
    } finally {
      setLoading(false);
    }
  };


  const fetchOrdersByDate = async () => {
    if (!selectedDate) {
      setError("Please select a date.");
      return;
    }
    setLoading(true);
    try {
      const response = await axios.get(`http://localhost:8081/api/admin/order-history-by-date/${selectedDate}`);
      setOrdersByDate(response.data);
      setError("");
    } catch (err) {
      console.error("Error fetching orders by date:", err);
      setError("Failed to fetch orders for the selected date.");
    } finally {
      setLoading(false);
    }
  };


  const fetchOrderHistoryForMember = async () => {
    if (!memberId) {
      setError("Please enter a member ID.");
      return;
    }
    setLoading(true);
    try {
      const response = await axios.get(`http://localhost:8081/api/admin/order-history/member/${memberId}`);
      setOrderHistoryForMember(response.data);
      setError("");
    } catch (err) {
      console.error("Error fetching member order history:", err);
      setError("Failed to fetch member order history.");
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
    } catch (err) {
      console.error("Error creating user:", err);
      setError("Failed to create user. Please try again.");
      setSuccess(false);
    }
  };


  const chartOptions = {
    responsive: true,
    plugins: {
      legend: { position: "top" },
      title: { display: true },
    },
  };


  const orderHistoryPerDayData = {
    labels: orderHistoryPerDay.map((item) => item.orderDate),
    datasets: [
      {
        label: "Orders per Day",
        data: orderHistoryPerDay.map((item) => item.orderCount),
        borderColor: "rgba(75, 192, 192, 1)",
        backgroundColor: "rgba(75, 192, 192, 0.2)",
      },
    ],
  };


  const mostOrderedItemsData = {
    labels: mostOrderedItems.map((item) => item.itemName),
    datasets: [
      {
        label: "Most Ordered Items",
        data: mostOrderedItems.map((item) => item.totalQuantity),
        backgroundColor: "rgba(75, 192, 192, 0.2)",
        borderColor: "rgba(75, 192, 192, 1)",
        borderWidth: 1,
      },
    ],
  };


  const leastOrderedItemsData = {
    labels: leastOrderedItems.map((item) => item.itemName),
    datasets: [
      {
        label: "Least Ordered Items",
        data: leastOrderedItems.map((item) => item.totalQuantity),
        backgroundColor: "rgba(255, 99, 132, 0.2)",
        borderColor: "rgba(255, 99, 132, 1)",
        borderWidth: 1,
      },
    ],
  };


  return (
      <Container maxWidth="lg" style={{ padding: 20 }}>
        <Typography variant="h4" gutterBottom align="center">
          {viewDashboard ? "ADMIN DASHBOARD" : "CREATE NEW USER"}
        </Typography>


        {success && <Alert severity="success">User created successfully!</Alert>}
        {error && <Alert severity="error">{error}</Alert>}


        {viewDashboard ? (
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <Paper style={{ padding: 20 }}>
                  <Typography variant="h6">Order History Per Day</Typography>
                  <Line data={orderHistoryPerDayData} options={chartOptions} />
                </Paper>
              </Grid>


              <Grid item xs={12} sm={6}>
                <Paper style={{ padding: 20, maxHeight: "300px", overflowY: "auto" }}>
                  <Typography variant="h6">Orders for a Specific Day</Typography>
                  <TextField
                      label="Date (YYYY-MM-DD)"
                      variant="outlined"
                      fullWidth
                      value={selectedDate}
                      onChange={(e) => setSelectedDate(e.target.value)}
                      margin="normal"
                  />
                  <Button variant="contained" color="primary" onClick={fetchOrdersByDate} fullWidth>
                    Fetch Orders
                  </Button>
                  {ordersByDate.map((order) => (
                      <List key={order.orderId}>
                        <ListItem>
                          <ListItemText primary={`Order ID: ${order.orderId}`} secondary={`Total: $${order.totalPrice}`} />
                        </ListItem>
                      </List>
                  ))}
                </Paper>
              </Grid>


              <Grid item xs={12} sm={6}>
                <Paper style={{ padding: 20, maxHeight: "300px", overflowY: "auto" }}>
                  <Typography variant="h6">Order History for Member</Typography>
                  <TextField
                      label="Member ID"
                      variant="outlined"
                      fullWidth
                      value={memberId}
                      onChange={(e) => setMemberId(e.target.value)}
                      margin="normal"
                  />
                  <Button variant="contained" color="primary" onClick={fetchOrderHistoryForMember} fullWidth>
                    Fetch Member Orders
                  </Button>
                  {orderHistoryForMember.map((order) => (
                      <List key={order.orderId}>
                        <ListItem>
                          <ListItemText
                              primary={`Order ID: ${order.orderId}`}
                              secondary={`Date: ${order.orderDate} | Total: $${order.totalPrice}`}
                          />
                        </ListItem>
                      </List>
                  ))}
                </Paper>
              </Grid>


              <Grid item xs={12} sm={6}>
                <Paper style={{ padding: 20 }}>
                  <Typography variant="h6">Most Ordered Items</Typography>
                  <Bar data={mostOrderedItemsData} options={chartOptions} />
                </Paper>
              </Grid>


              <Grid item xs={12} sm={6}>
                <Paper style={{ padding: 20 }}>
                  <Typography variant="h6">Least Ordered Items</Typography>
                  <Bar data={leastOrderedItemsData} options={chartOptions} />
                </Paper>
              </Grid>
            </Grid>
        ) : (
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
              <FormControl fullWidth margin="normal">
                <InputLabel>Role</InputLabel>
                <Select value={role} onChange={(e) => setRole(e.target.value)} label="Role">
                  <MenuItem value="Admin">Admin</MenuItem>
                  <MenuItem value="Manager">Manager</MenuItem>
                  <MenuItem value="Cashier">Cashier</MenuItem>
                </Select>
              </FormControl>
              <Button type="submit" variant="contained" color="primary" fullWidth>
                Create User
              </Button>
            </form>
        )}


        <Button
            variant="contained"
            color="secondary"
            onClick={() => setViewDashboard(!viewDashboard)}
            fullWidth
            style={{ marginTop: 20 }}
        >
          {viewDashboard ? "Go Back to Create User" : "View Dashboard"}
        </Button>
      </Container>
  );
};


export default Admin;
