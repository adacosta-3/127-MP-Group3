import React from 'react';
import { useLocation } from 'react-router-dom';

const Receipt = () => {
  const location = useLocation();
  const { orderDetails } = location.state || {};

  console.log("Order Details in Receipt:", JSON.stringify(orderDetails, null, 2));

  if (!orderDetails) {
    return <div>No order details available.</div>;
  }

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
      {/* Custom Webkit Scrollbar Styling */}
      <style>
        {`
          ::-webkit-scrollbar {
            width: 0px; /* Hide the scrollbar */
            background: transparent; /* Optional: make the background transparent */
          }
          
          ::-webkit-scrollbar-thumb {
            background: transparent; /* Optional: make the thumb transparent */
          }
        `}
      </style>

      <h2 style={headerStyle}>Receipt</h2>
      <p><strong>Order ID:</strong> {orderDetails.orderId}</p>
      <p><strong>Order Date:</strong> {new Date(orderDetails.date).toLocaleString()}</p>
      <p><strong>Total Price:</strong> ${orderDetails.totalPrice ? orderDetails.totalPrice.toFixed(2) : '0.00'}</p>
      
      <h3>Items</h3>
      <table style={tableStyle}>
        <thead>
          <tr>
            <th style={thStyle}>Item Name</th>
            <th style={thStyle}>Quantity</th>
            <th style={thStyle}>Customizations</th>
            <th style={thStyle}>Customization Price</th>
            <th style={thStyle}>Total Price</th>
          </tr>
        </thead>
        <tbody>
          {orderDetails.items.map((item, index) => (
            <tr key={index}>
              {/* Item Name */}
              <td style={tdStyle}>{item.name || 'N/A'}</td>
              
              {/* Quantity */}
              <td style={tdStyle}>{item.quantity}</td>
              
              {/* Customizations */}
              <td style={tdStyle}>
                {item.customizations && item.customizations.length > 0 ? (
                  item.customizations.map((custom, i) => (
                    <div key={i}>{custom.customizationName}</div>
                  ))
                ) : (
                  'Default'
                )}
              </td>
              
              {/* Customization Price */}
              <td style={tdStyle}>
                {item.customizations && item.customizations.length > 0 ? (
                  item.customizations.reduce((sum, custom) => sum + (custom.additionalCost || 0), 0).toFixed(2)
                ) : (
                  '0.00'
                )}
              </td>
              
              {/* Total Price */}
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
    </div>
  );
};

export default Receipt;
