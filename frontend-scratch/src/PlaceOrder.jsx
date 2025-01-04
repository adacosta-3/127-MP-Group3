import React, { useState, useEffect } from "react";
import axios from "axios";

const PlaceOrder = () => {
  const [itemTypes, setItemTypes] = useState(["Food", "Merchandise", "Drink"]);
  const [categories, setCategories] = useState([]);
  const [items, setItems] = useState([]);
  const [sizes, setSizes] = useState([]);
  const [customizations, setCustomizations] = useState([]);
  const [customizationOptions, setCustomizationOptions] = useState([]);
  const [selectedItemType, setSelectedItemType] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedItem, setSelectedItem] = useState(null);
  const [selectedSize, setSelectedSize] = useState(null);
  const [selectedCustomization, setSelectedCustomization] = useState(null);
  const [selectedOption, setSelectedOption] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [totalPrice, setTotalPrice] = useState(0);
  const [itemsOrdered, setItemsOrdered] = useState([]);
  const [showReceipt, setShowReceipt] = useState(false);

  const [isMember, setIsMember] = useState(null);
  const [guestName, setGuestName] = useState("");const [orderId, setOrderId] = useState(null);  // Add this line at the top of your component

  const [memberDetails, setMemberDetails] = useState({
    name: "",
    email: "",
    phoneNumber: "",
    dateOfBirth: "",
  });
  const [isDetailsSubmitted, setIsDetailsSubmitted] = useState(false);
  const [error, setError] = useState("");
  useEffect(() => {
    if (selectedItemType) {
      const fetchCategories = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8081/api/categories/item-type/${selectedItemType}`  // Updated URL pattern
          );
          console.log("Fetched Categories:", response.data); // Debugging to check the response
          setCategories(response.data);  // Assuming response.data is the list of categories
        } catch (error) {
          console.error("Error fetching categories:", error);
          setCategories([]);  // Set empty array in case of an error
        }
      };
  
      fetchCategories();
    }
  }, [selectedItemType]); // This hook runs whenever the selected item type changes
  
  useEffect(() => {
    if (selectedCategory) {
      const fetchItems = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8081/api/items/category/${selectedCategory}`
          );
          setItems(response.data);
        } catch (error) {
          console.error("Error fetching items:", error);
        }
      };
      fetchItems();
    }
  }, [selectedCategory]);

  useEffect(() => {
    if (selectedItem) {
      const fetchSizes = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8081/api/items/sizes/${selectedItem.itemCode}`
          );
          setSizes(response.data);
        } catch (error) {
          console.error("Error fetching sizes:", error);
        }
      };

      const fetchCustomizations = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8081/api/customizations?itemId=${selectedItem.itemCode}`
          );
          setCustomizations(response.data);
        } catch (error) {
          console.error("Error fetching customizations:", error);
        }
      };

      fetchSizes();
      fetchCustomizations();
    }
  }, [selectedItem]);
    useEffect(() => {
      // Clear customization options when selectedCustomization changes
      setCustomizationOptions([]);
    
      // Check if a new customization is selected
      if (selectedCustomization) {
        const fetchCustomizationOptions = async () => {
          try {
            const response = await axios.get(
              `http://localhost:8081/api/customization-options/customization/${selectedCustomization}`
            );
            setCustomizationOptions(response.data); // Set the new customization options
          } catch (error) {
            console.error("Error fetching customization options:", error);
          }
        };
        fetchCustomizationOptions();
      }
    }, [selectedCustomization]); // Dependency on selectedCustomization
    
  useEffect(() => {
    if (selectedItem && quantity > 0) {
      const basePrice = selectedItem.basePrice || 0;
      const sizeAdjustment = sizes.find((size) => size.sizeName === selectedSize)?.priceAdjustment || 0;
      const customizationAdjustment = selectedOption ? selectedOption.additionalCost : 0;

      const calculatedPrice = (basePrice + sizeAdjustment + customizationAdjustment) * quantity;
      setTotalPrice(calculatedPrice);
    } else {
      setTotalPrice(0);
    }
  }, [selectedItem, selectedSize, selectedOption, quantity, sizes]);
  const addItemToOrder = async () => {
    // Ensure orderId is available before proceeding
    if (!orderId) {
      console.error("Order ID is missing.");
      return;
    }
  
    if (!selectedItem || !selectedSize) {
      console.error("Selected item or size is missing.");
      return;
    }
  
    const orderLineDTO = {
      orderId: orderId,  // Ensure orderId is passed
      itemCode: selectedItem.itemCode, // Item code
      sizeId: selectedSize.sizeId, // Size ID
      quantity: quantity, // Quantity
      linePrice: totalPrice, // Total price for the line
      customizations: selectedOption ? [{ optionId: selectedOption.optionId }] : [], // Customizations, if any
    };
  
    // Log the order line JSON before sending it to the server
    console.log("Order Line JSON to send:", JSON.stringify(orderLineDTO));
  
    try {
      const response = await axios.post(
        `http://localhost:8081/api/order-lines`,
        orderLineDTO
      );
      const savedOrderLine = response.data;
      setItemsOrdered([...itemsOrdered, savedOrderLine]);
  
      // If customizations were selected, send them to /api/order-line-customizations
      if (selectedOption) {
        const customizationDTO = {
          orderLineId: savedOrderLine.orderLineId, // Get the ID of the newly created order line
          optionId: selectedOption.optionId, // The selected customization option ID
        };
  
        try {
          await axios.post(`http://localhost:8081/api/order-line-customizations`, customizationDTO);
          console.log("Customization added to order line.");
        } catch (error) {
          console.error("Error adding customization to order line:", error);
        }
      }
  
      resetForm();
    } catch (error) {
      console.error("Error adding item to order:", error);
    }
  };
  
  
  
  
  const resetForm = () => {
    setSelectedCategory(null);
    setSelectedItem(null);
    setSelectedSize(null);
    setSelectedCustomization(null);
    setSelectedOption(null);
    setQuantity(1);
    setTotalPrice(0);
  };
  const handleItemTypeChange = (e) => {
    const selectedType = e.target.value;
    setSelectedItemType(selectedType);
    setSelectedCategory(null); // Clear category when item type changes
    setItems([]); // Clear items when item type changes
    clearSelections(); // Clear other selections (size, customization, etc.)
  };
  const handleDetailsSubmit = async (e) => {
    e.preventDefault();
    let customerDTO = {};
    let memberDTO = null;
  
    if (isMember === "guest") {
      if (!guestName) {
        setError("Please enter your first name.");
        return;
      }
  
      customerDTO = {
        type: "Guest",
        firstName: guestName,
        lastName: "",
      };
    } else if (isMember === "member") {
      if (!memberDetails.name) {
        setError("Please enter your full name.");
        return;
      }
  
      if (!memberDetails.phoneNumber && !memberDetails.email) {
        setError("Please provide either a phone number or an email.");
        return;
      }
  
      customerDTO = {
        type: "Member",
        firstName: memberDetails.name.split(" ")[0],
        lastName: memberDetails.name.split(" ")[1] || "",
      };
  
      memberDTO = {
        fullName: memberDetails.name,
        email: memberDetails.email,
        phoneNumber: memberDetails.phoneNumber,
        dateOfBirth: memberDetails.dateOfBirth,
      };
    }
  
    try {
      // Create the customer first
      const customerResponse = await axios.post("http://localhost:8081/api/customers", customerDTO);
  
      if (isMember === "member") {
        // Create the member if it's a member
        await axios.post("http://localhost:8081/api/members", {
          ...memberDTO,
          customerId: customerResponse.data.customerId,
        });
      }
  
      // Assuming cashierId is available (replace with actual logic)
      const cashierId = 1; // Get the logged-in cashier's userId
  
      // Now, define the orderDTO object
      const orderDTO = {
        customerId: customerResponse.data.customerId, // Created customerId
        cashierId: cashierId, // Assuming cashierId is available
        orderDate: new Date().toISOString().split('T')[0], // Current date
        totalPrice: totalPrice, // Use the calculated total price
        orderLines: itemsOrdered.map((orderLine) => ({
          itemCode: orderLine.itemCode,
          sizeId: orderLine.sizeId,
          quantity: orderLine.quantity,
          linePrice: orderLine.linePrice,
          customizations: orderLine.customizations || [],
        })),
      };
  
      // Create the customer order with the orderDTO
      const orderResponse = await axios.post("http://localhost:8081/api/customer-orders", orderDTO);
  
      // Set the orderId once the order is created
      const createdOrderId = orderResponse.data.orderId;
      setOrderId(createdOrderId); // Store the orderId
  
      // Now you can use this orderId when adding order lines (it will be passed with the order line DTO)
      setIsDetailsSubmitted(true);
      setError(""); // Clear any previous errors
  
    } catch (error) {
      console.error("Error creating order:", error);
      setError("An error occurred while creating the customer/member or the order.");
    }
  };
  
  const handleGuestNameChange = (e) => {
    setGuestName(e.target.value);
  };

  const handleMemberDetailsChange = (e) => {
    const { name, value } = e.target;
    setMemberDetails((prevDetails) => ({
      ...prevDetails,
      [name]: value,
    }));
  };

  const clearSelections = () => {
    setSelectedSize(null);
    setSelectedCustomization(null);
    setSelectedOption(null);
    setQuantity(1);
  };

  return (
    <div>
      <h1>Place Your Order</h1>

      {/* Guest/Member selection */}
      {isMember === null && !isDetailsSubmitted && (
        <div>
          <label>Are you a member or guest?</label>
          <select onChange={(e) => setIsMember(e.target.value)} value={isMember || ""}>
            <option value="" disabled>Select an option</option>
            <option value="guest">Guest</option>
            <option value="member">Member</option>
          </select>
        </div>
      )}

      {isMember === "guest" && !isDetailsSubmitted && (
        <div>
          <h3>Enter your name</h3>
          <label>Name:</label>
          <input
            type="text"
            name="guestName"
            value={guestName}
            onChange={handleGuestNameChange}
            required
          />
          <button onClick={handleDetailsSubmit}>Submit</button>
        </div>
      )}

      {isMember === "member" && !isDetailsSubmitted && (
        <div>
          <h3>Enter your details</h3>
          <label>Name:</label>
          <input
            type="text"
            name="name"
            value={memberDetails.name}
            onChange={handleMemberDetailsChange}
            required
          />
          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={memberDetails.email}
            onChange={handleMemberDetailsChange}
          />
          <label>Phone Number:</label>
          <input
            type="tel"
            name="phoneNumber"
            value={memberDetails.phoneNumber}
            onChange={handleMemberDetailsChange}
          />
          <label>Date of Birth:</label>
          <input
            type="date"
            name="dateOfBirth"
            value={memberDetails.dateOfBirth}
            onChange={handleMemberDetailsChange}
          />
          {error && <div style={{ color: "red" }}>{error}</div>}
          <button onClick={handleDetailsSubmit}>Submit</button>
        </div>
      )}

      {isDetailsSubmitted && (
        <div>
             {isDetailsSubmitted && (
      <div>
        <label>Select Item Type:</label>
        <select
          onChange={handleItemTypeChange} // Use the handleItemTypeChange here
          value={selectedItemType || ""}
        >
          <option value="" disabled>Select an item type</option>
          {itemTypes.map((type) => (
            <option key={type} value={type}>{type}</option>
          ))}
        </select>
      </div>
    )}
        {selectedItemType && categories.length > 0 && (
  <div>
    <label>Select Category:</label>
    <select
      onChange={(e) => {
        setSelectedCategory(e.target.value);
        setItems([]); // Clear items when category changes
        clearSelections();
      }}
      value={selectedCategory || ""}
    >
      <option value="" disabled>Select a category</option>
      {categories.map((category) => (
        <option key={category.categoryId} value={category.categoryId}>{category.name}</option>
      ))}
    </select>
  </div>
)}

          {selectedCategory && (
            <div>
              <label>Select Item:</label>
              <select
                onChange={(e) => {
                  const selectedItem = items.find(item => item.itemCode === e.target.value);
                  setSelectedItem(selectedItem);
                  clearSelections();
                }}
                value={selectedItem?.itemCode || ""}
              >
                <option value="" disabled>Select an item</option>
                {items.map((item) => (
                  <option key={item.itemCode} value={item.itemCode}>{item.name}</option>
                ))}
              </select>
            </div>
          )}

{selectedItem && sizes.length > 0 && (
  <div>
    <label>Select Size:</label>
    <select onChange={(e) => {
    const selectedSize = sizes.find(size => size.sizeName === e.target.value);
    setSelectedSize(selectedSize);
    console.log("Selected Size:", JSON.stringify(selectedSize)); // Log the selected size in JSON
}} value={selectedSize?.sizeName || ""}>

      <option value="" disabled>Select a size</option>
      {sizes.map((size) => (
        <option key={size.sizeName} value={size.sizeName}>
          {size.sizeName} (+${size.priceAdjustment.toFixed(2)})
        </option>
      ))}
    </select>
  </div>
)}


          {selectedItem && (
            <div>
              <label>Select Customization:</label>
              <select onChange={(e) => setSelectedCustomization(e.target.value)} value={selectedCustomization || ""}>
                <option value="" disabled>Select a customization</option>
                {customizations.map((customization) => (
                  <option key={customization.customizationId} value={customization.customizationId}>
                    {customization.name}
                  </option>
                ))}
              </select>
            </div>
          )}

{selectedCustomization && (
  <div>
    <label>Select Option:</label>
    <select
      onChange={(e) => {
        // Find the selected option by optionId
        const selectedOption = customizationOptions.find(option => option.optionId === parseInt(e.target.value));
        setSelectedOption(selectedOption); // Set the selected option object
      }}
      value={selectedOption ? selectedOption.optionId : ""} // Set the optionId for value binding
    >
      <option value="" disabled>Select an option</option>
      {customizationOptions.map((option) => (
        <option key={option.optionId} value={option.optionId}>
          {option.optionName} (+${option.additionalCost.toFixed(2)})
        </option>
      ))}
    </select>
  </div>
)}
          <div>
            <label>Quantity:</label>
            <input
              type="number"
              value={quantity}
              onChange={(e) => setQuantity(parseInt(e.target.value, 10))}
              min="1"
            />
          </div>

          <div>Total Price: ${totalPrice.toFixed(2)}</div>

          <button onClick={addItemToOrder}>Add to Order</button>

          <h3>Ordered Items:</h3>
          <ul>
            {itemsOrdered.map((orderLine) => (
              <li key={orderLine.orderLineId}>
                {orderLine.itemName} - {orderLine.quantity} - ${orderLine.linePrice.toFixed(2)}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default PlaceOrder;
