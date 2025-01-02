// order.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms'; // Import FormBuilder

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss']
})
export class OrderComponent {
  isGuest = true; // Track whether the user is ordering as a guest or member
  guestForm: FormGroup; // Declare the form group for the guest form
  memberForm: FormGroup; // Declare the form group for the member form

  // Inject FormBuilder into the constructor
  constructor(private router: Router, private fb: FormBuilder) {
    // Initialize the forms in the constructor
    this.guestForm = this.fb.group({
      firstName: ['', Validators.required] // Create the guest form with validation
    });

    this.memberForm = this.fb.group({
      membershipId: ['', [Validators.required, Validators.pattern('^[A-Za-z0-9]{5}$')]] // Create the member form with validation
    });
  }

  // Proceed method for Guest
  onGuestSubmit(): void {
    if (this.guestForm.valid) {
      // Navigate to ShopComponent on form submission
      this.router.navigate(['/shop']);
    }
  }

  // Proceed method for Member
  onMemberSubmit(): void {
    if (this.memberForm.valid) {
      // Handle member submission logic here
      this.router.navigate(['/shop']);
    }
  }
}
