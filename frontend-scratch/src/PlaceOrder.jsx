
import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

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

  const [isMember, setIsMember] = useState(null); // This determines if the user is a guest or a member
  const [guestName, setGuestName] = useState(""); // Used for guest
  const [orderId, setOrderId] = useState(null);

  const [memberDetails, setMemberDetails] = useState({
    name: "",
    email: "",
    phoneNumber: "",
    dateOfBirth: "",
  });

  const [isDetailsSubmitted, setIsDetailsSubmitted] = useState(false);
  const [error, setError] = useState(""); // Used to display error messages if there are issues

  const navigate = useNavigate();

  const handleCustomerTypeSelection = (type) => {
    setIsMember(type); // Sets if the user is a guest or a member
    setError(""); // Reset error
    if (type === "member") {
      // Ask for Member ID if they choose Member
      setMemberDetails({ id: "" }); // Clear previous member details
    }
  };

  const handleMemberIdSubmit = async (e) => {
    e.preventDefault();
    const memberId = memberDetails.id.trim();
  
    // Validate member ID
    if (!memberId || memberId.length !== 5 || !/^[A-Za-z0-9]+$/.test(memberId)) {
      setError("Please enter a valid 5-character alphanumeric Member ID.");
      return;
    }
  
    try {
      // Check if member ID exists
      const response = await axios.get(`http://localhost:8081/api/members/${memberId}`);
      const memberData = response.data;
  
      // Auto-fill member details
      setMemberDetails({
        ...memberData,
        id: memberId, // Keep the ID as it is
      });
  
      setError(""); // Clear any error
    } catch (error) {
      setError("Member ID does not exist.");
    }
  };


  const handleDetailsSubmit = async (e) => {
    e.preventDefault();
    if (isMember === null) {
      setError("Please select whether you are a guest or member.");
      return;
    }

    if (isMember === "guest") {
      if (!guestName) {
        setError("Please enter your name.");
        return;
      }
    } else if (isMember === "member") {
      if (!memberDetails.name || !memberDetails.email) {
        setError("Please provide both name and email.");
        return;
      }
    }

    // Proceed with creating the customer (guest or member) and order
    let customerDTO = {};
    let memberDTO = null;

    if (isMember === "guest") {
      customerDTO = {
        type: "Guest",
        firstName: guestName,
        lastName: "",
      };
    } else if (isMember === "member") {
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
      const customerResponse = await axios.post("http://localhost:8081/api/customers", customerDTO);

      if (isMember === "member") {
        await axios.post("http://localhost:8081/api/members", {
          ...memberDTO,
          customerId: customerResponse.data.customerId,
        });
      }

      const cashierId = 1; // Assuming cashierId is 1 for now

      const orderDTO = {
        customerId: customerResponse.data.customerId,
        cashierId: cashierId,
        orderDate: new Date().toISOString().split('T')[0],
        totalPrice: totalPrice,
      };

      const orderResponse = await axios.post("http://localhost:8081/api/customer-orders", orderDTO);
      setOrderId(orderResponse.data.orderId);
      setIsDetailsSubmitted(true); // Set that the details have been submitted
      setError("");
    } catch (error) {
      console.error("Error creating order:", error);
      setError("An error occurred while creating the customer/member or the order.");
    }
  };

  // Fetch categories based on selected item type
  useEffect(() => {
    if (selectedItemType) {
      const fetchCategories = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8081/api/categories/item-type/${selectedItemType}`
          );
          setCategories(response.data);
        } catch (error) {
          console.error("Error fetching categories:", error);
          setCategories([]);
        }
      };
      fetchCategories();
    }
  }, [selectedItemType]);

  // Fetch items based on selected category
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

  // Fetch sizes and customizations when an item is selected
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

  // Fetch customization options based on selected customization
  useEffect(() => {
    if (selectedCustomization) {
      const fetchCustomizationOptions = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8081/api/customization-options/customization/${selectedCustomization}`
          );
          setCustomizationOptions(response.data);
        } catch (error) {
          console.error("Error fetching customization options:", error);
        }
      };
      fetchCustomizationOptions();
    }
  }, [selectedCustomization]);

  const handleItemTypeChange = (e) => {
    setSelectedItemType(e.target.value);
    setSelectedCategory(null);
    setItems([]);
  };

  const handleItemSelection = (e) => {
    setSelectedItem(items.find((item) => item.itemCode === e.target.value));
  };

  const handleSizeSelection = (e) => {
    setSelectedSize(sizes.find((size) => size.sizeName === e.target.value));
  };

  const handleCustomizationSelection = (e) => {
    setSelectedCustomization(e.target.value);
    setSelectedOption(null); // Reset selected option when customization changes
  };

  const handleCustomizationOptionSelection = (e) => {
    setSelectedOption(customizationOptions.find((option) => option.optionId === e.target.value));
  };

  const addItemToOrder = async () => {
    if (!orderId || !selectedItem) {
      console.error("Order ID or selected item is missing.");
      return;
    }

    const basePrice = selectedItem.basePrice || 0;
    const sizeAdjustment = sizes.find((size) => size.sizeName === selectedSize)?.priceAdjustment || 0;
    const customizationAdjustment = selectedOption ? selectedOption.additionalCost : 0;

    const linePrice = (basePrice + sizeAdjustment + customizationAdjustment) * quantity;

    const orderLineDTO = {
      orderId: orderId,
      itemCode: selectedItem.itemCode,
      quantity: quantity,
      linePrice: linePrice,
      customizations: selectedOption ? [{ optionId: selectedOption.optionId }] : [],
    };

    if (selectedSize) {
      orderLineDTO.sizeId = selectedSize.sizeId;
    }

    try {
      const response = await axios.post(`http://localhost:8081/api/order-lines`, orderLineDTO);
      const savedOrderLine = response.data;
      setItemsOrdered([ ...itemsOrdered, { ...savedOrderLine, linePrice: linePrice } ]);
      resetForm();
    } catch (error) {
      console.error("Error adding item to order:", error.response?.data || error.message);
    }
  };

  const completeOrderTransaction = async () => {
    try {
      const response = await axios.post(
        `http://localhost:8081/api/customer-orders/${orderId}/complete`
      );
      console.log('Order Completion Response:', response.data); // Log the response data
  
      setTotalPrice(response.data.totalPrice); // Update total price
      // Navigate to Receipt page after order completion
      navigate('/receipt', { state: { totalPrice: response.data.totalPrice } });
    } catch (error) {
      console.error("Error completing order:", error.response?.data || error.message);
      setError("An error occurred while completing the order.");
    }
  };
  
  
  const resetForm = () => {
    setSelectedCategory(null);
    setSelectedItem(null);
    setSelectedSize(null);
    setSelectedCustomization(null);
    setSelectedOption(null);
    setQuantity(1);
  };

  return (
    <div>
      <h1>Place Order</h1>

      {error && <div>{error}</div>}

      {/* Step 1: Ask if Guest or Member */}
      {!isDetailsSubmitted && !orderId && (
        <div>
          <h2>Select Customer Type</h2>
          <button onClick={() => handleCustomerTypeSelection("guest")}>Guest</button>
          <button onClick={() => handleCustomerTypeSelection("member")}>Member</button>
        </div>
      )}

{isMember && !isDetailsSubmitted && (
  <div>
    {/* Member ID for Member selection */}
    {isMember === "guest" ? (
      <div>
        <label>
          Name
          <input
            type="text"
            value={guestName}
            onChange={(e) => setGuestName(e.target.value)}
          />
        </label>
      </div>
    ) : (
      <div>
        <label>
          Member ID
          <input
            type="text"
            value={memberDetails.id}
            onChange={(e) => setMemberDetails({ ...memberDetails, id: e.target.value })}
            maxLength={5}
          />
        </label>
        <button onClick={handleMemberIdSubmit}>Submit Member ID</button>

        {/* Display Member details once ID is verified */}
        {memberDetails.name && (
          <div>
            <label>
              Full Name
              <input
                type="text"
                value={memberDetails.name}
                onChange={(e) => setMemberDetails({ ...memberDetails, name: e.target.value })}
              />
            </label>
            <label>
              Email
              <input
                type="email"
                value={memberDetails.email}
                onChange={(e) => setMemberDetails({ ...memberDetails, email: e.target.value })}
              />
            </label>
            <label>
              Phone Number
              <input
                type="text"
                value={memberDetails.phoneNumber}
                onChange={(e) => setMemberDetails({ ...memberDetails, phoneNumber: e.target.value })}
              />
            </label>
          </div>
        )}
      </div>
    )}
    <button onClick={handleDetailsSubmit}>Submit</button>
  </div>
)}

      {/* Step 3: Show Order Form after details submission */}
      {isDetailsSubmitted && (
        <div>
          {/* Select item type */}
          <label>
            Item Type
            <select value={selectedItemType} onChange={handleItemTypeChange}>
              <option value="">Select Item Type</option>
              {itemTypes.map((type) => (
                <option key={type} value={type}>
                  {type}
                </option>
              ))}
            </select>
          </label>

          {/* Select category */}
          <label>
            Category
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
            >
              <option value="">Select Category</option>
              {categories.map((category) => (
                <option key={category.categoryId} value={category.categoryId}>
                  {category.name}
                </option>
              ))}
            </select>
          </label>

          {/* Select item */}
          <label>
            Item
            <select
              value={selectedItem ? selectedItem.itemCode : ""}
              onChange={handleItemSelection}
            >
              <option value="">Select Item</option>
              {items.map((item) => (
                <option key={item.itemCode} value={item.itemCode}>
                  {item.name}
                </option>
              ))}
            </select>
          </label>

          {/* Select size */}
          {selectedItem && (
            <label>
              Size
              <select value={selectedSize ? selectedSize.sizeName : ""} onChange={handleSizeSelection}>
                <option value="">Select Size</option>
                {sizes.map((size) => (
                  <option key={size.sizeName} value={size.sizeName}>
                    {size.sizeName}
                  </option>
                ))}
              </select>
            </label>
          )}

          {/* Select customization */}
          {selectedItem && (
            <label>
              Customization
              <select value={selectedCustomization} onChange={handleCustomizationSelection}>
                <option value="">Select Customization</option>
                {customizations.map((customization) => (
                  <option key={customization.customizationId} value={customization.customizationId}>
                    {customization.name}
                  </option>
                ))}
              </select>
            </label>
          )}

          {/* Select customization option */}
          {selectedCustomization && (
            <label>
              Customization Option
              <select
                value={selectedOption ? selectedOption.optionId : ""}
                onChange={handleCustomizationOptionSelection}
              >
                <option value="">Select Option</option>
                {customizationOptions.map((option) => (
                  <option key={option.optionId} value={option.optionId}>
                    {option.optionName}
                  </option>
                ))}
              </select>
            </label>
          )}

          {/* Quantity input */}
          <label>
            Quantity
            <input
              type="number"
              value={quantity}
              onChange={(e) => setQuantity(Number(e.target.value))}
              min="1"
            />
          </label>

          {/* Add item to order */}
          <button onClick={addItemToOrder}>Add Item</button>

          {/* Finalize Order */}
          <button onClick={completeOrderTransaction}>Finalize Order</button>

          {/* Show items added */}
          <div>
            <h2>Order Items</h2>
            {itemsOrdered.map((line, index) => (
              <div key={index}>
                <p>{line.itemName}</p>
                <p>{line.linePrice}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Receipt */}
      {showReceipt && (
        <div>
          <h1>Receipt</h1>
          <p>Total Price: {totalPrice}</p>
        </div>
      )}
    </div>
  );
};

export default PlaceOrder;
