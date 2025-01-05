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
  const [memberId, setMemberId] = useState("");
  const [orderId, setOrderId] = useState(null);

  const [memberDetails, setMemberDetails] = useState(null);
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
      const sizeAdjustment = selectedSize ? sizes.find((size) => size.sizeId === selectedSize)?.priceAdjustment : 0;
      const customizationAdjustment = selectedOption ? selectedOption.additionalCost : 0;
  
      const calculatedPrice = (basePrice + sizeAdjustment + customizationAdjustment) * quantity;
      setTotalPrice(calculatedPrice);  // Set the calculated total price
    } else {
      setTotalPrice(0);  // Reset total price if no item is selected or quantity is 0
    }
  }, [selectedItem, selectedSize, selectedOption, quantity, sizes]);  // Ensure the effect runs when these dependencies change
  
  const addItemToOrder = async () => {
    if (!orderId || !selectedItem) {
      console.error("Order ID or selected item is missing.");
      alert("Please select an item before adding it to the order.");
      return;
    }
  
    console.log("Selected Item:", selectedItem);  // Debugging the structure
  
    const basePrice = selectedItem?.basePrice ?? 0;
    const sizeAdjustment = sizes?.find((size) => size.sizeName === selectedSize)?.priceAdjustment ?? 0;
    const customizationAdjustment = selectedOption ? selectedOption.additionalCost : 0;
  
    const linePrice = (basePrice + sizeAdjustment + customizationAdjustment) * quantity;
  
    const orderLineDTO = {
      orderId: orderId,
      itemCode: selectedItem.itemCode,
      itemName: selectedItem?.name ?? "Unknown Item", // Default to 'Unknown Item' if name is missing
      quantity: quantity,
      linePrice: linePrice,
      customizations: selectedOption ? [{ optionId: selectedOption.optionId }] : [],
    };
  
    // Add sizeId if selectedSize is valid
    if (selectedSize) {
      orderLineDTO.sizeId = selectedSize?.sizeId;
    }
  
    console.log("Order Line DTO:", orderLineDTO);
  
    try {
      const response = await axios.post(`http://localhost:8081/api/order-lines`, orderLineDTO);
      const savedOrderLine = response.data;
  
      setItemsOrdered([
        ...itemsOrdered,
        { ...savedOrderLine, linePrice: linePrice },
      ]);
  
      if (selectedOption) {
        const customizationDTO = {
          orderLineId: savedOrderLine.orderLineId,
          optionId: selectedOption.optionId,
        };
  
        try {
          await axios.post(`http://localhost:8081/api/order-line-customizations`, customizationDTO);
          console.log("Customization added to order line.");
        } catch (error) {
          console.error("Error adding customization:", error.response?.data || error.message);
        }
      }
  
      resetForm();
  
    } catch (error) {
      console.error("Error adding item to order:", error.response?.data || error.message);
      alert("Failed to add item to the order. Please try again.");
    }
  };
  

const resetForm = () => {
  setSelectedItem(null);
  setSelectedSize(null);
  setSelectedCustomization(null);
  setSelectedOption(null);
  setQuantity(1);
  setTotalPrice(0);
};

// When finalizing the order
const finalizeOrder = async () => {
  if (!itemsOrdered || itemsOrdered.length === 0) {
    console.error("No items in the order to finalize.");
    return;
  }

  try {
    const response = await axios.post(
      `http://localhost:8081/api/customer-orders/${orderId}/complete`
    );

    console.log("Server Response:", response);
    const orderDetails = {
      orderId: response.data.orderId,
      customer: guestName || memberDetails?.name,
      date: new Date(response.data.orderDate).toISOString().split('T')[0],
      items: itemsOrdered.map((item) => ({
        itemCode: item.itemCode,
        name: item.itemName,
        size: item.size,
        quantity: item.quantity,
        customizations: item.customizations.map((custom) => ({
          name: custom.customizationName,
          additionalCost: custom.additionalCost,
        })),
        linePrice: item.linePrice,
      })),
      totalPrice: response.data.totalPrice,
    };

    // Check if orderDetails are correct
    console.log("Order Details being passed to receipt:", orderDetails);

    // Navigate to the receipt page with the order details
    navigate('/receipt', { state: { orderDetails } });
  } catch (error) {
    console.error("Error finalizing order:", error);
  }
};



  const handleItemTypeChange = (e) => {
    const selectedType = e.target.value;
    setSelectedItemType(selectedType);
    setSelectedCategory(null);
    setItems([]);
    clearSelections();
  };

  const handleMemberCheck = async (e) => {
    e.preventDefault();

    if (!/^\d{5}$/.test(memberId)) {
      setError("Please enter a valid 5-digit Member ID.");
      return;
    }

    try {
      const response = await axios.get(`http://localhost:8081/api/members/${memberId}`);
      setMemberDetails(response.data);
      setError("");
    } catch (error) {
      console.error("Error verifying member ID:", error);
      setError("Member not found or incorrect Member ID.");
    }
  };

  const handleDetailsSubmit = async (e) => {
    e.preventDefault();
    let customerDTO = {};

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
      if (!memberDetails) {
        setError("Please verify your Member ID.");
        return;
      }

      customerDTO = {
        type: "Member",
        firstName: memberDetails.fullName.split(" ")[0],
        lastName: memberDetails.fullName.split(" ")[1] || "",
      };
    }

    try {
      const customerResponse = await axios.post("http://localhost:8081/api/customers", customerDTO);

      if (isMember === "member") {
        await axios.post("http://localhost:8081/api/members", {
          ...memberDetails,
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
        })),
      };

      const orderResponse = await axios.post("http://localhost:8081/api/customer-orders", orderDTO);
      setOrderId(orderResponse.data.orderId);
      setIsDetailsSubmitted(true);
    } catch (error) {
      console.error("Error submitting details:", error.response?.data || error.message);
    }
  };

  const handleBack = () => {
    setIsMember(null);
    setGuestName("");
    setMemberId("");
    setMemberDetails(null);
    setError("");
  };

  const clearSelections = () => {
    setSelectedItem(null);
    setSelectedSize(null);
    setSelectedCustomization(null);
    setSelectedOption(null);
  };

  return (
    <div>
      <h1>Place Order</h1>
      {!isDetailsSubmitted ? (
        <div>
          {isMember === null && (
            <div>
              <button onClick={() => setIsMember("guest")}>Guest</button>
              <button onClick={() => setIsMember("member")}>Member</button>
            </div>
          )}
          {isMember === "guest" && (
            <div>
              <form onSubmit={handleDetailsSubmit}>
                <label>Guest Name:</label>
                <input
                  type="text"
                  value={guestName}
                  onChange={(e) => setGuestName(e.target.value)}
                />
                <button type="submit">Submit</button>
              </form>
            </div>
          )}
          {isMember === "member" && (
            <div>
              <form onSubmit={handleMemberCheck}>
                <label>Member ID:</label>
                <input
                  type="text"
                  value={memberId}
                  onChange={(e) => setMemberId(e.target.value)}
                />
                <button type="submit">Check Membership</button>
              </form>
              {memberDetails && (
                <div>
                  <p>Member Name: {memberDetails.fullName}</p>
                  <button onClick={handleDetailsSubmit}>Submit Details</button>
                </div>
              )}
              {error && <p style={{ color: "red" }}>{error}</p>}
            </div>
          )}
          <button onClick={handleBack}>Back</button>
        </div>
      ) : (
        <div>
          <div>
            <h2>Select Items</h2>
            <label>Item Type:</label>
            <select onChange={handleItemTypeChange}>
              <option value="">Select Item Type</option>
              {itemTypes.map((type, index) => (
                <option key={index} value={type}>{type}</option>
              ))}
            </select>

            {categories.length > 0 && (
              <>
                <label>Category:</label>
                <select onChange={(e) => setSelectedCategory(e.target.value)}>
                  <option value="">Select Category</option>
                  {categories.map((category) => (
                    <option key={category.categoryId} value={category.categoryId}>
                      {category.name}
                    </option>
                  ))}
                </select>
              </>
            )}

            {items.length > 0 && (
              <>
                <label>Item:</label>
                <select onChange={(e) => setSelectedItem(items.find(item => item.itemCode === e.target.value))}>
                  <option value="">Select Item</option>
                  {items.map((item) => (
                    <option key={item.itemCode} value={item.itemCode}>
                      {item.name} - ${item.basePrice}
                    </option>
                  ))}
                </select>
              </>
            )}

            {sizes.length > 0 && (
              <>
                <label>Size:</label>
                <select onChange={(e) => setSelectedSize(sizes.find(size => size.sizeId === e.target.value))}>
  <option value="">Select Size</option>
  {sizes.map((size) => (
    <option key={size.sizeId} value={size.sizeId}>
      {size.sizeName} (+${size.priceAdjustment})
    </option>
  ))}
</select>

              </>
            )}

            {customizations.length > 0 && (
              <>
                <label>Customization:</label>
                <select onChange={(e) => setSelectedCustomization(e.target.value)}>
                  <option value="">Select Customization</option>
                  {customizations.map((custom) => (
                    <option key={custom.customizationId} value={custom.customizationId}>
                      {custom.name}
                    </option>
                  ))}
                </select>
              </>
            )}

            {customizationOptions.length > 0 && (
              <>
                <label>Customization Option:</label>
                <select onChange={(e) => setSelectedOption(customizationOptions.find(option => option.optionId === e.target.value))}>
                  <option value="">Select Option</option>
                  {customizationOptions.map((option) => (
                    <option key={option.optionId} value={option.optionId}>
                      {option.optionName} (+${option.additionalCost})
                    </option>
                  ))}
                </select>
              </>
            )}

            <label>Quantity:</label>
            <input
              type="number"
              min="1"
              value={quantity}
              onChange={(e) => setQuantity(Number(e.target.value))}
            />

            <p>Total Price: ${totalPrice.toFixed(2)}</p>

            <button onClick={addItemToOrder} disabled={!selectedItem || quantity <= 0}>
              Add Item to Order
            </button>

            {itemsOrdered.length > 0 && (
              <div>
                <h3>Order Summary:</h3>
                <ul>
                  {itemsOrdered.map((item, index) => (
                    <li key={index}>
                      {item.itemName} (x{item.quantity}) - ${item.linePrice.toFixed(2)}
                    </li>
                  ))}
                </ul>
                <button onClick={finalizeOrder}>Finalize Order</button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default PlaceOrder;
