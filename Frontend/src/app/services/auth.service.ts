import { Injectable } from '@angular/core';
import { Route, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
// Example function to retrieve the current user's role
  getUserRole(): string {
    const user = JSON.parse(localStorage.getItem('currentUser') || '{}'); // Replace with your logic
    return user.role || '';
  }

  constructor(private router:Router) { }

  public isAuthenticated():boolean{
    const token = localStorage.getItem('token');
    //console.log("token is : " + token);
      if(!token){
      this.router.navigate(['/']);
      return false;
    }else{
      return true;
    }
  }

}
