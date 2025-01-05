import React, { useState, useEffect } from "react";
import axios from "axios";

const Customization = () => {
    const [categories, setCategories] = useState([]);
    const [customizations, setCustomizations] = useState([]);
    const [customizationOptions, setCustomizationOptions] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [selectedCustomization, setSelectedCustomization] = useState(null);
    const [selectedOption, setSelectedOption] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [totalPrice, setTotalPrice] = useState(0);
    const [itemsOrdered, setItemsOrdered] = useState([]);
    const [isMember, setIsMember] = useState(false); // Guest or Member
    const [showReceipt, setShowReceipt] = useState(false);

    // Fetch categories
    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const response = await axios.get("http://localhost:8081/api/categories");
                console.log(response.data);  // Ensure you're logging the response data to verify its structure
                setCategories(response.data);
            } catch (error) {
                console.error("Error fetching categories:", error);
            }
        };
        fetchCategories();
    }, []);

    // Fetch customizations for the selected category
    useEffect(() => {
        if (selectedCategory) {
            const fetchCustomizations = async () => {
                try {
                    const response = await axios.get(
                        `http://localhost:8081/api/customizations?categoryId=${selectedCategory}`
                    );
                    setCustomizations(response.data);
                } catch (error) {
                    console.error("Error fetching customizations:", error);
                }
            };
            fetchCustomizations();
        }
    }, [selectedCategory]);

    // Fetch options for the selected customization
    useEffect(() => {
        if (selectedCustomization) {
            const fetchCustomizationOptions = async () => {
                try {
                    const response = await axios.get(
                        `http://localhost:8081/api/customizations/${selectedCustomization}`
                    );
                    if (response.data && Array.isArray(response.data.options)) {
                        setCustomizationOptions(response.data.options);
                    } else {
                        setCustomizationOptions([]);
                    }
                } catch (error) {
                    console.error("Error fetching customization options:", error);
                    setCustomizationOptions([]);
                }
            };
            fetchCustomizationOptions();
        }
    }, [selectedCustomization]);

    // Update total price
    useEffect(() => {
        if (selectedOption && quantity > 0) {
            const option = customizationOptions.find(
                (opt) => opt.optionId === selectedOption
            );
            setTotalPrice(option ? option.additionalCost * quantity : 0);
        }
    }, [selectedOption, quantity, customizationOptions]);

    const addItemToOrder = () => {
        const item = {
            categoryId: selectedCategory,
            customizationId: selectedCustomization,
            optionId: selectedOption,
            quantity,
            price: totalPrice,
        };

        // Check if the same item with same customizations already exists
        const existingItemIndex = itemsOrdered.findIndex(
            (orderedItem) =>
                orderedItem.categoryId === item.categoryId &&
                orderedItem.customizationId === item.customizationId &&
                orderedItem.optionId === item.optionId
        );

        if (existingItemIndex !== -1) {
            // Increase quantity and update price for existing item
            const updatedItems = [...itemsOrdered];
            updatedItems[existingItemIndex].quantity += quantity;
            updatedItems[existingItemIndex].price += totalPrice;
            setItemsOrdered(updatedItems);
        } else {
            // Add new item to the order
            setItemsOrdered([...itemsOrdered, item]);
        }

        // Reset selection after adding
        setSelectedCategory(null);
        setSelectedCustomization(null);
        setSelectedOption(null);
        setQuantity(1);
        setTotalPrice(0);
    };

    const calculateTotalTransactionPrice = () => {
        return itemsOrdered.reduce((acc, item) => acc + item.price, 0);
    };

    const handleTransactionComplete = () => {
        setShowReceipt(true);
    };

    return (
        <div>
            <h1>Customize Your Order</h1>

            {/* Start New Transaction */}
            <div>
                <label>Select Transaction Type:</label>
                <select
                    onChange={(e) => setIsMember(e.target.value === "member")}
                >
                    <option value="guest">Guest</option>
                    <option value="member">Member</option>
                </select>
            </div>

            {/* Item Type, Customization, Quantity Selection */}
            <div>
                <label>Select Category:</label>
                <select
                    onChange={(e) => setSelectedCategory(e.target.value)}
                    value={selectedCategory || ""}
                >
                    <option value="" disabled>Select a category</option>
                    {categories.map((category) => (
                        <option key={category.categoryId} value={category.categoryId}>
                            {category.name}
                        </option>
                    ))}
                </select>
            </div>

            {selectedCategory && (
                <div>
                    <label>Select Customization:</label>
                    <select
                        onChange={(e) => setSelectedCustomization(e.target.value)}
                        value={selectedCustomization || ""}
                    >
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
                        onChange={(e) => setSelectedOption(e.target.value)}
                        value={selectedOption || ""}
                    >
                        <option value="" disabled>Select an option</option>
                        {customizationOptions.map((option) => (
                            <option key={option.optionId} value={option.optionId}>
                                {option.optionName} - ${option.additionalCost.toFixed(2)}
                            </option>
                        ))}
                    </select>
                </div>
            )}

            {selectedOption && (
                <div>
                    <label>Quantity:</label>
                    <input
                        type="number"
                        value={quantity}
                        min="1"
                        onChange={(e) => setQuantity(Number(e.target.value))}
                    />
                    <p>Total Price: ${totalPrice.toFixed(2)}</p>
                </div>
            )}

            <button onClick={addItemToOrder}>Add Item to Order</button>

            {/* Display Receipt */}
            {showReceipt && (
                <div>
                    <h2>Receipt</h2>
                    {itemsOrdered.map((item, index) => (
                        <div key={index}>
                            <p>Item: {item.categoryId} - Customization: {item.customizationId}</p>
                            <p>Option: {item.optionId} - Quantity: {item.quantity}</p>
                            <p>Price: ${item.price.toFixed(2)}</p>
                        </div>
                    ))}
                    <h3>Total: ${calculateTotalTransactionPrice().toFixed(2)}</h3>
                </div>
            )}

            <button onClick={handleTransactionComplete}>Complete Transaction</button>
        </div>
    );
};

export default Customization;
