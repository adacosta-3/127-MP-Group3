// shop.component.ts
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-shop',
  templateUrl: './shop.component.html',
  styleUrls: ['./shop.component.scss']
})
export class ShopComponent implements OnInit {
  items = [
    {
      name: 'Americano',
      type: 'Drink',
      category: 'Espresso Drinks',
      basePrice: 100,
      sizes: { small: 100, medium: 150, large: 200 }
    },
    {
      name: 'Spaghetti Bolognese',
      type: 'Food',
      category: 'Pastas',
      basePrice: 250,
      sizes: {}
    },
    {
      name: 'Hell Week Coffee Mug',
      type: 'Merchandise',
      category: 'Mugs',
      basePrice: 500,
      sizes: {}
    }
  ];
Object: any;

  constructor() {}

  ngOnInit(): void {}
}
