// item.service.ts
import { Injectable } from '@angular/core';
import { Item } from './item.model';

@Injectable({
  providedIn: 'root'
})
export class ItemService {
  private items: Item[] = [];

  constructor() {}

  // Method to generate item code
  generateItemCode(category: string, name: string): string {
    const categoryInitials = category.slice(0, 1).toUpperCase() + category.slice(-1).toUpperCase();
    const namePrefix = name.slice(0, 4).toUpperCase();
    const itemCount = this.items.filter(item => item.category === category).length + 1;
    const itemCountPadded = itemCount.toString().padStart(3, '0'); // Ensures a 3-digit number

    return `${categoryInitials}-${namePrefix}-${itemCountPadded}`;
  }

  // Method to add an item
  addItem(item: Item): void {
    const itemCode = this.generateItemCode(item.category, item.name);
    item.itemCode = itemCode;
    this.items.push(item);
  }

  // Method to get all items
  getItems(): Item[] {
    return this.items;
  }

  // Method to get items by category
  getItemsByCategory(category: string): Item[] {
    return this.items.filter(item => item.category === category);
  }
}
