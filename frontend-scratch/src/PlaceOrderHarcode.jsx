import React, { useState } from "react";
import "./PlaceOrder.css";

const itemsData = {
  Food: [
    { name: "Croissant", price: 50, customizations: ["Butter"] },
    { name: "Bagel", price: 40, customizations: ["Jam"] },
  ],
  Drink: [
    { name: "Americano", price: 100, customizations: ["Milk"] },
    { name: "Latte", price: 120, customizations: ["Milk", "Sauce"] },
  ],
  Merchandise: [
    { name: "Coffee Mug", price: 200, customizations: [] },
    { name: "Canvas Tote", price: 300, customizations: [] },
  ],
};

const customizationOptions = {
  Milk: { "Soy Milk": 35, "Oat Milk": 40 },
  Sauce: { Caramel: 30, Mocha: 30 },
  Butter: { "Extra Butter": 10 },
  Jam: { "Strawberry Jam": 20, "Blueberry Jam": 25 },
};

function PlaceOrder() {
  const [transactionType, setTransactionType] = useState("");
  const [customerInfo, setCustomerInfo] = useState("");
  const [isInfoSubmitted, setIsInfoSubmitted] = useState(false);
  const [orderItems, setOrderItems] = useState([]);
  const [newOrder, setNewOrder] = useState({
    type: "",
    name: "",
    customizations: {},
    quantity: 1,
    price: 0,
  });
  const [isTransactionComplete, setIsTransactionComplete] = useState(false);

  const resetForm = () => {
    setTransactionType("");
    setCustomerInfo("");
    setIsInfoSubmitted(false);
    setOrderItems([]);
    setNewOrder({
      type: "",
      name: "",
      customizations: {},
      quantity: 1,
      price: 0,
    });
    setIsTransactionComplete(false);
  };

  const handleAddItem = () => {
    const existingItemIndex = orderItems.findIndex(
      (item) =>
        item.name === newOrder.name &&
        JSON.stringify(item.customizations) === JSON.stringify(newOrder.customizations)
    );

    if (existingItemIndex > -1) {
      const updatedItems = [...orderItems];
      updatedItems[existingItemIndex].quantity += newOrder.quantity;
      setOrderItems(updatedItems);
    } else {
      setOrderItems([...orderItems, newOrder]);
    }

    setNewOrder({
      type: "",
      name: "",
      customizations: {},
      quantity: 1,
      price: 0,
    });
  };

  const handlePrintReceipt = () => {
    const printSection = document.getElementById("receipt");
    const printWindow = window.open("", "_blank");
    printWindow.document.write("<html><head><title>Receipt</title></head><body>");
    printWindow.document.write(printSection.innerHTML);
    printWindow.document.write("</body></html>");
    printWindow.document.close();
    printWindow.print();
  };

  const calculateTotalPrice = () => {
    return orderItems.reduce((total, item) => {
      const customizationPrice = getCustomizationPrice(item.customizations);
      return total + item.quantity * (item.price + customizationPrice);
    }, 0);
  };

  const getCustomizationPrice = (customizations) => {
    return Object.entries(customizations).reduce(
      (total, [key, value]) => total + (customizationOptions[key] ? customizationOptions[key][value] : 0),
      0
    );
  };

  return (
    <div className="place-order">
      <h1>Place Order</h1>
      {!transactionType ? (
        <div>
          <button onClick={() => setTransactionType("Guest")}>Guest</button>
          <button onClick={() => setTransactionType("Member")}>Member</button>
        </div>
      ) : !isInfoSubmitted ? (
        <div>
          <h2>
            {transactionType === "Guest" ? "Enter your First Name:" : "Enter your Membership ID:"}
          </h2>
          <input
            type="text"
            value={customerInfo}
            onChange={(e) => setCustomerInfo(e.target.value)}
          />
          <button
            onClick={() => {
              if (customerInfo) setIsInfoSubmitted(true);
              else alert("Please enter a valid input!");
            }}
          >
            Confirm
          </button>
        </div>
      ) : (
        <div>
          {!isTransactionComplete ? (
            <div>
              <h2>Add Items to Your Order</h2>
              <div className="order-form">
                <label>Type:</label>
                <select
                  value={newOrder.type}
                  onChange={(e) => {
                    const type = e.target.value;
                    setNewOrder({
                      ...newOrder,
                      type,
                      name: "",
                      customizations: {},
                      price: 0,
                    });
                  }}
                >
                  <option value="">Select Type</option>
                  {Object.keys(itemsData).map((type) => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                  ))}
                </select>
                {newOrder.type && (
                  <>
                    <label>Item:</label>
                    <select
                      value={newOrder.name}
                      onChange={(e) => {
                        const item = itemsData[newOrder.type].find(
                          (item) => item.name === e.target.value
                        );
                        setNewOrder({
                          ...newOrder,
                          name: item.name,
                          price: item.price,
                          customizations: {},
                        });
                      }}
                    >
                      <option value="">Select Item</option>
                      {itemsData[newOrder.type].map((item) => (
                        <option key={item.name} value={item.name}>
                          {item.name}
                        </option>
                      ))}
                    </select>
                  </>
                )}
                {newOrder.name &&
                  itemsData[newOrder.type].find(
                    (item) => item.name === newOrder.name
                  ).customizations.map((cust) => (
                    <div key={cust}>
                      <label>{cust}:</label>
                      <select
                        onChange={(e) =>
                          setNewOrder({
                            ...newOrder,
                            customizations: {
                              ...newOrder.customizations,
                              [cust]: e.target.value,
                            },
                          })
                        }
                      >
                        <option value="">None</option>
                        {Object.keys(customizationOptions[cust]).map((opt) => (
                          <option key={opt} value={opt}>
                            {opt} (+₱{customizationOptions[cust][opt]})
                          </option>
                        ))}
                      </select>
                    </div>
                  ))}
                <label>Quantity:</label>
                <input
                  type="number"
                  value={newOrder.quantity}
                  onChange={(e) =>
                    setNewOrder({ ...newOrder, quantity: parseInt(e.target.value) })
                  }
                  min="1"
                />
                <button onClick={handleAddItem}>Add to Order</button>
              </div>
              <h3>Current Order</h3>
              <ul>
                {orderItems.map((item, index) => {
                  const customizationPrice = getCustomizationPrice(item.customizations);
                  const subtotal = item.quantity * (item.price + customizationPrice);
                  return (
                    <li key={index}>
                      {item.quantity}x {item.name} - ₱{item.price} each
                      <br />
                      Customizations:{" "}
                      {Object.keys(item.customizations).length > 0
                        ? Object.entries(item.customizations).map(
                            ([key, value]) => `${key}: ${value} (+₱${customizationOptions[key][value]})`
                          ).join(", ")
                        : "None"}
                      <br />
                      Subtotal: ₱{subtotal}
                    </li>
                  );
                })}
              </ul>
              <h3>Total: ₱{calculateTotalPrice() || "0.00"}</h3>
              <button onClick={() => setIsTransactionComplete(true)}>
                Complete Transaction
              </button>
            </div>
          ) : (
            <div id="receipt">
              <h2>Receipt</h2>
              <p>
                <strong>
                  {transactionType === "Guest" ? "Guest Name: " : "Membership ID: "}
                  {customerInfo}
                </strong>
              </p>
              <ul>
                {orderItems.map((item, index) => {
                  const customizationPrice = getCustomizationPrice(item.customizations);
                  const subtotal = item.quantity * (item.price + customizationPrice);
                  return (
                    <li key={index}>
                      <strong>{item.name}</strong>
                      <br />
                      Original Price: ₱{item.price} each
                      <br />
                      Customizations:{" "}
                      {Object.keys(item.customizations).length > 0
                        ? Object.entries(item.customizations).map(
                            ([key, value]) => `${key}: ${value} (+₱${customizationOptions[key][value]})`
                          ).join(", ")
                        : "None"}
                      <br />
                      Subtotal: ₱{subtotal}
                    </li>
                  );
                })}
              </ul>
              <h3>Total: ₱{calculateTotalPrice() || "0.00"}</h3>
              <button onClick={handlePrintReceipt}>Print Receipt</button>
              <button onClick={() => setIsTransactionComplete(false)}>
                Add More Items
              </button>
              <button onClick={resetForm}>New Transaction</button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default PlaceOrder;
