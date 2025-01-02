import { Component, AfterViewInit } from '@angular/core';
import { DashboardService } from '../services/dashboard.service';
import { SnackbarService } from '../snackbar.service';
// import { NgxUiLoaderService } from 'ngx-ui-loader';
import { GlobalConstants } from '../shared/global-constants';
import { AuthService } from '../services/auth.service'; // Add AuthService

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements AfterViewInit {

  responseMessage: any;
  data: any;
  isUserRole: boolean = false; // Track if the user has the 'user' role

  ngAfterViewInit() {}

  constructor(
    private dashboardService: DashboardService,
    // private ngxService: NgxUiLoaderService,
    private snackbarService: SnackbarService,
    private authService: AuthService // Inject AuthService
  ) {
    // this.ngxService.start();
    this.dashboardData();
    this.checkUserRole(); // Check user's role
  }

  dashboardData() {
    this.dashboardService.getDetails().subscribe(
      (response: any) => {
        // this.ngxService.stop();
        this.data = response;
      },
      (error: any) => {
        // this.ngxService.stop();
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        } else {
          this.responseMessage = GlobalConstants.genericError;
        }
        this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
      }
    );
  }

  checkUserRole() {
    // Retrieve the current user role
    this.isUserRole = this.authService.getUserRole() === 'user';
  }
}
