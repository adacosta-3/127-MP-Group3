import React from 'react'; 
import { useLocation, useNavigate } from 'react-router-dom';

const Receipt = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const { orderDetails } = location.state || {}; // Get orderDetails passed through location.state
  const userRole = location.state?.role || localStorage.getItem('role'); // Retrieve role from localStorage

  if (!orderDetails) {
    return <div>No order details available.</div>;
  }

  // Save role and order details in localStorage
  localStorage.setItem('role', userRole);
  localStorage.setItem('orderDetails', JSON.stringify(orderDetails));

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    marginBottom: '20px',
  };

  const thStyle = {
    borderBottom: '1px solid #000',
    padding: '8px',
    textAlign: 'left',
  };

  const tdStyle = {
    borderBottom: '1px solid #ddd',
    padding: '8px',
    textAlign: 'left',
  };

  const totalPriceStyle = {
    textAlign: 'right',
    fontWeight: 'bold',
    fontSize: '1.2em',
    marginTop: '10px',
  };

  const headerStyle = {
    fontSize: '1.5em',
    marginBottom: '10px',
  };

  // Function to navigate back to the Order page
  const handleBackToOrder = () => {
    navigate('/cashier'); // Redirect back to the cashier order page
  };

  return (
    <div
      style={{
        padding: '20px',
        textAlign: 'left',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'flex-start',
        overflowY: 'auto', // Enable vertical scrolling
        maxHeight: '80vh', // Limit height to 80% of the viewport height
        scrollbarWidth: 'none', // For Firefox to hide scrollbar
        msOverflowStyle: 'none', // For IE to hide scrollbar
      }}
    >
      <h2 style={headerStyle}>Receipt</h2>
      <p><strong>Order ID:</strong> {orderDetails.orderId}</p>
      <p><strong>Order Date:</strong> {new Date(orderDetails.date).toLocaleString()}</p>
      <p><strong>Total Price:</strong> ${orderDetails.totalPrice ? orderDetails.totalPrice.toFixed(2) : '0.00'}</p>
      
      <h3>Items</h3>
      <table style={tableStyle}>
        <thead>
          <tr>
            <th style={thStyle}>Item Code</th> {/* Changed to Item Code */}
            <th style={thStyle}>Quantity</th>
            <th style={thStyle}>Customizations</th>
            <th style={thStyle}>Customization Price</th>
            <th style={thStyle}>Total Price</th>
          </tr>
        </thead>
        <tbody>
          {orderDetails.items.map((item, index) => (
            <tr key={index}>
              <td style={tdStyle}>{item.itemCode || 'N/A'}</td> {/* Display Item Code */}
              <td style={tdStyle}>{item.quantity}</td>
              <td style={tdStyle}>
                {item.customizations && item.customizations.length > 0 ? (
                  item.customizations.map((custom, i) => (
                    <div key={i}>{custom.customizationName}</div>
                  ))
                ) : (
                  'Default'
                )}
              </td>
              <td style={tdStyle}>
                {item.customizations && item.customizations.length > 0 ? (
                  item.customizations.reduce((sum, custom) => sum + (custom.additionalCost || 0), 0).toFixed(2)
                ) : (
                  '0.00'
                )}
              </td>
              <td style={tdStyle}>
                ${item.totalPrice ? item.totalPrice.toFixed(2) : '0.00'}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <p style={totalPriceStyle}>
        <strong>Total Amount: </strong>
        ${orderDetails.totalPrice.toFixed(2)}
      </p>

      <button onClick={() => window.print()}>Print Receipt</button>

      <button onClick={handleBackToOrder} style={{ marginTop: '20px' }}>
        Back to Order
      </button>
    </div>
  );
};

export default Receipt;
