import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
})
export class SignupComponent {
  signupForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.signupForm = this.fb.group({
      fullName: ['', [Validators.required]],
      dateOfBirth: ['', [Validators.required]],
      email: ['', []],
      phoneNumber: ['', []],
    }, { validators: this.contactMethodValidator });
  }

  // Custom validator to ensure at least one contact method is provided
  contactMethodValidator(formGroup: FormGroup) {
    const email = formGroup.get('email')?.value;
    const phoneNumber = formGroup.get('phoneNumber')?.value;
    return email || phoneNumber ? null : { contactMethod: true };
  }

  onSubmit() {
    if (this.signupForm.valid) {
      const membershipId = this.generateMembershipId();
      const newMember = {
        ...this.signupForm.value,
        membershipId,
      };
      console.log('Member signed up:', newMember);
      alert(`Sign-up successful! Your Membership ID is: ${membershipId}`);
      this.signupForm.reset();
    } else {
      console.log('Form is invalid');
    }
  }

  private generateMembershipId(): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    return Array.from({ length: 5 }, () =>
      chars.charAt(Math.floor(Math.random() * chars.length))
    ).join('');
  }
}
