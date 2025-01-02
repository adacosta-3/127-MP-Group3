// item.model.ts
export interface Item {
    itemCode: string; // Automatically generated
    name: string;
    category: string;  // Category of the item (e.g., Pasta, Bags, Espresso Drinks)
    basePrice: number;  // Base price for the item
    type: 'Food' | 'Drink' | 'Merchandise'; // Type of item
    sizes?: { [size: string]: number };  // Sizes for drinks (optional for food and merchandise)
    price?: number;  // Calculated price based on size for drinks (optional)
  }
  