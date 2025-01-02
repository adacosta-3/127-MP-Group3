import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent {
  signupForm: FormGroup;

  // For toggling password visibility
  password: boolean = true;
  confirmPassword: boolean = true;

  constructor(private fb: FormBuilder) {
    // Initialize the form group
    this.signupForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    });
  }

  // Function to validate if the passwords match
  validateSubmit(): boolean {
    const password = this.signupForm.get('password')?.value;
    const confirmPassword = this.signupForm.get('confirmPassword')?.value;
    return password !== confirmPassword;
  }

  // Function to handle form submission
  handleSubmit(): void {
    if (this.signupForm.valid) {
      // Simulate submitting additional data
      const formData = {
        ...this.signupForm.value,
        role: 'user',       // Adding role as 'user'
        status: 'true'        // Adding status as true
      };

      console.log('Form Submitted:', formData);

      // Example: Display success message
      alert('Sign-up successful!');

      // Example: Clear the form after submission
      this.signupForm.reset();

      // Example: Navigate to another page (requires Angular Router)
      // this.router.navigate(['/welcome']);
    } else {
      console.error('Form is invalid');
    }
  }

}
