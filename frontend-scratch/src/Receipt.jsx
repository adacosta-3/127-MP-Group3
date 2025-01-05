import React from "react";
import { useLocation } from "react-router-dom";

const Receipt = () => {
  const location = useLocation();
  const { orderDetails } = location.state || {};

  if (!orderDetails) {
    return <div>No order details available.</div>;
  }

  return (
    <div>
      <h1>Receipt</h1>
      <h2>Order ID: {orderDetails.orderId}</h2>
      <h3>Customer: {orderDetails.customer}</h3>
      <h4>Date: {orderDetails.date}</h4>

      <h3>Order Items</h3>
      <ul>
        {orderDetails.items.map((item, index) => (
          <li key={index}>
            {item.name} - ${item.price}
            {item.size && ` (Size: ${item.size})`}
            {item.customizations && (
              <ul>
                {item.customizations.map((customization, index) => (
                  <li key={index}>
                    {customization.name}: ${customization.additionalCost}
                  </li>
                ))}
              </ul>
            )}
          </li>
        ))}
      </ul>

      <h3>Total: ${orderDetails.totalPrice}</h3>

      {/* This could trigger the print dialog */}
      <button onClick={() => window.print()}>Print Receipt</button>
    </div>
  );
};

export default Receipt;
