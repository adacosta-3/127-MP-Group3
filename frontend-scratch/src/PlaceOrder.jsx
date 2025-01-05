import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';

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
  const [guestName, setGuestName] = useState("");
  const [orderId, setOrderId] = useState(null);

  const [memberDetails, setMemberDetails] = useState({
    name: "",
    email: "",
    phoneNumber: "",
    dateOfBirth: "",
  });
  const [isDetailsSubmitted, setIsDetailsSubmitted] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();
  useEffect(() => {
    if (selectedItemType) {
      const fetchCategories = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8081/api/categories/item-type/${selectedItemType}`
          );
          console.log("Fetched Categories:", response.data);
          setCategories(response.data);
        } catch (error) {
          console.error("Error fetching categories:", error);
          setCategories([]);
        }
      };

      fetchCategories();
    }
  }, [selectedItemType]);

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
    setCustomizationOptions([]);
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
    if (!orderId || !selectedItem) {
      console.error("Order ID or selected item is missing.");
      return;
    }

    const orderLineDTO = {
      orderId: orderId,
      itemCode: selectedItem.itemCode,
      quantity: quantity,
      customizations: selectedOption ? [{ optionId: selectedOption.optionId }] : [],
    };

    if (selectedSize) {
      orderLineDTO.sizeId = selectedSize.sizeId;
    }

    console.log("Order Line DTO to send:", orderLineDTO);

    try {
      const response = await axios.post(`http://localhost:8081/api/order-lines`, orderLineDTO);
      const savedOrderLine = response.data;
      setItemsOrdered([...itemsOrdered, savedOrderLine]);

      if (selectedOption) {
        const customizationDTO = {
          orderLineId: savedOrderLine.orderLineId,
          optionId: selectedOption.optionId,
        };

        try {
          await axios.post(`http://localhost:8081/api/order-line-customizations`, customizationDTO);
          console.log("Customization added to order line.");
        } catch (error) {
          console.error("Error adding customization:", error);
        }
      }

      resetForm();
    } catch (error) {
      console.error("Error adding item to order:", error.response?.data || error.message);
    }
  };
  const finalizeOrder = async () => {
    try {
      const response = await axios.post(
        `http://localhost:8081/api/customer-orders/${orderId}/complete`
      );
      console.log("Full API Response:", response.data);
  
      // Prepare the order details for the receipt page
      const orderDetails = {
        orderId: response.data.orderId,
        customer: guestName || memberDetails.name,
        date: new Date(response.data.orderDate).toISOString().split('T')[0],
        items: response.data.items.map((item) => ({
          itemCode: item.itemCode, // Assuming you have an itemCode
          name: item.itemName,
          size: item.size, // Assuming you have size information
          quantity: item.quantity,
          customizations: item.customizations.map((custom) => ({
            name: custom.customizationName,
            additionalCost: custom.additionalCost,
          })),
          totalPrice: item.totalPrice,
        })),
        totalPrice: response.data.totalPrice,
      };
  
      console.log("Prepared Order Details:", orderDetails);
      navigate('/receipt', { state: { orderDetails } });
    } catch (error) {
      console.error("Error finalizing order:", error);
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
    setSelectedCategory(null);
    setItems([]); // Clear items when item type changes
    clearSelections();
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
      const customerResponse = await axios.post("http://localhost:8081/api/customers", customerDTO);

      if (isMember === "member") {
        await axios.post("http://localhost:8081/api/members", {
          ...memberDTO,
          customerId: customerResponse.data.customerId,
        });
      }

      const cashierId = 1; // Assume cashier ID is 1 for now.

      const orderDTO = {
        customerId: customerResponse.data.customerId,
        cashierId: cashierId,
        orderDate: new Date().toISOString().split('T')[0],
        totalPrice: totalPrice,
        orderLines: itemsOrdered.map((orderLine) => ({
          itemCode: orderLine.itemCode,
          sizeId: orderLine.sizeId,
          quantity: orderLine.quantity,
          linePrice: orderLine.linePrice,
          customizations: orderLine.customizations || [],
        })),
      };

      const orderResponse = await axios.post("http://localhost:8081/api/customer-orders", orderDTO);

      const createdOrderId = orderResponse.data.orderId;
      setOrderId(createdOrderId);

      setIsDetailsSubmitted(true);
      setError("");
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

      {/* Member/Guest Section */}
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
            required
          />
          <label>Phone Number:</label>
          <input
            type="text"
            name="phoneNumber"
            value={memberDetails.phoneNumber}
            onChange={handleMemberDetailsChange}
            required
          />
          <label>Date of Birth:</label>
          <input
            type="date"
            name="dateOfBirth"
            value={memberDetails.dateOfBirth}
            onChange={handleMemberDetailsChange}
          />
          <button onClick={handleDetailsSubmit}>Submit</button>
        </div>
      )}

      {/* Item Selection */}
      {isDetailsSubmitted && (
        <div>
          <h3>Item Selection</h3>
          <label>Select Item Type:</label>
          <select onChange={handleItemTypeChange} value={selectedItemType || ""}>
            <option value="" disabled>Select an item type</option>
            {itemTypes.map((type) => (
              <option key={type} value={type}>{type}</option>
            ))}
          </select>

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

          {selectedCategory && items.length > 0 && (
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

          {selectedItem && customizations.length > 0 && (
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

          {selectedCustomization && customizationOptions.length > 0 && (
            <div>
              <label>Select Option:</label>
              <select
                onChange={(e) => {
                  const selectedOption = customizationOptions.find(option => option.optionId === parseInt(e.target.value));
                  setSelectedOption(selectedOption);
                }}
                value={selectedOption ? selectedOption.optionId : ""}
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

          <label>Quantity:</label>
          <input type="number" value={quantity} onChange={(e) => setQuantity(e.target.value)} />

          <div>Total Price: ${totalPrice.toFixed(2)}</div>
          <button onClick={addItemToOrder}>Add Item to Order</button>

          <h3>Order Summary</h3>
          <ul>
            {itemsOrdered.map((item, index) => (
              <li key={index}>{item.itemCode}</li>
            ))}
          </ul>

          <button onClick={finalizeOrder}>Finalize Order</button>
        </div>
      )}
    </div>
  );
};

export default PlaceOrder;
