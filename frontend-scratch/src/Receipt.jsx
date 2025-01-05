import React from "react";
import { useLocation, useNavigate } from "react-router-dom";

const Receipt = () => {
  const location = useLocation();
  const navigate = useNavigate();
  
  // Get the order data passed from the previous page
  const orderData = location.state?.orderData;

  // If no order data is passed, show an error
  if (!orderData) {
    return <div>No receipt data available.</div>;
  }

  // Function to print the receipt
  const handlePrint = () => {
    window.print();
  };

  return (
    <div style={{ padding: "20px", fontFamily: "Arial, sans-serif" }}>
      <h2>Receipt for Order {orderData.orderId}</h2>
      <p>Order Date: {new Date(orderData.orderDate).toLocaleString()}</p>

      {/* Ordered Items Table */}
      <h3>Ordered Items:</h3>
      <table style={{ width: "100%", borderCollapse: "collapse", marginBottom: "20px" }}>
        <thead>
          <tr style={{ backgroundColor: "#f4f4f4", textAlign: "left" }}>
            <th style={{ padding: "8px", border: "1px solid #ddd" }}>Item Name</th>
            <th style={{ padding: "8px", border: "1px solid #ddd" }}>Quantity</th>
            <th style={{ padding: "8px", border: "1px solid #ddd" }}>Price per Item</th>
            <th style={{ padding: "8px", border: "1px solid #ddd" }}>Customization Options</th>
            <th style={{ padding: "8px", border: "1px solid #ddd" }}>Customization Additional Cost</th>
            <th style={{ padding: "8px", border: "1px solid #ddd" }}>Item Total</th>
          </tr>
        </thead>
        <tbody>
          {orderData.items.map((item, index) => {
            const totalCustomizationCost = item.customizations.reduce(
              (sum, customization) => sum + customization.additionalCost,
              0
            );

            return (
              <tr key={index}>
                <td style={{ padding: "8px", border: "1px solid #ddd" }}>{item.itemName}</td>
                <td style={{ padding: "8px", border: "1px solid #ddd" }}>{item.quantity}</td>
                <td style={{ padding: "8px", border: "1px solid #ddd" }}>${(item.linePrice / item.quantity).toFixed(2)}</td>
                <td style={{ padding: "8px", border: "1px solid #ddd" }}>
                  {item.customizations.length > 0 ? (
                    <ul style={{ margin: 0, paddingLeft: "20px" }}>
                      {item.customizations.map((customization, idx) => (
                        <li key={idx}>{customization.customizationName}</li>
                      ))}
                    </ul>
                  ) : (
                    "No customizations"
                  )}
                </td>
                <td style={{ padding: "8px", border: "1px solid #ddd" }}>
                  {item.customizations.length > 0 ? (
                    <ul style={{ margin: 0, paddingLeft: "20px" }}>
                      {item.customizations.map((customization, idx) => (
                        <li key={idx}>${customization.additionalCost.toFixed(2)}</li>
                      ))}
                    </ul>
                  ) : (
                    "$0.00"
                  )}
                </td>
                <td style={{ padding: "8px", border: "1px solid #ddd" }}>${item.linePrice.toFixed(2)}</td>
              </tr>
            );
          })}
        </tbody>
      </table>

      {/* Total Transaction Price */}
      <h3>Total Transaction Price:</h3>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr style={{ backgroundColor: "#f4f4f4", textAlign: "left" }}>
            <th style={{ padding: "8px", border: "1px solid #ddd" }}>Total</th>
            <th style={{ padding: "8px", border: "1px solid #ddd" }}>Amount</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td style={{ padding: "8px", border: "1px solid #ddd" }}>Total Transaction Price</td>
            <td style={{ padding: "8px", border: "1px solid #ddd" }}>${orderData.totalPrice.toFixed(2)}</td>
          </tr>
        </tbody>
      </table>

      {/* Print and Back to Order Buttons */}
      <div style={{ marginTop: "20px" }}>
        <button onClick={handlePrint} style={{ marginRight: "10px" }}>
          Print Receipt
        </button>
        <button onClick={() => navigate("/orders")} style={{ marginLeft: "10px" }}>
          Back to Orders
        </button>
      </div>
    </div>
  );
};

export default Receipt;
  