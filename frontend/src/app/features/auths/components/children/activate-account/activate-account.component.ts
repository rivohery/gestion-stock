import { Component, effect, inject, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { NgOtpInputComponent } from 'ng-otp-input';
import { injectAuthsStore } from '../../../../../core/store/auths/auths.facade';
import { SnackbarService } from '../../../../../shared/services/snackbar-service';
import { OTPTokenRequest } from '../../../models/auths.model';
import { Router } from '@angular/router';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-activate-account',
  imports: [MatCardModule, MatIconModule, NgOtpInputComponent, NgIf],
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.css',
})
export class ActivateAccountComponent {
  authStore = injectAuthsStore();
  snackbar = inject(SnackbarService);
  router = inject(Router);

  constructor() {
    effect(() => {
      if (this.authStore.userDetails()) {
        this.snackbar.openSnackBar(
          this.authStore.successMsg() || '',
          'success'
        );
        this.router.navigateByUrl('/stock/admin/dashboard');
      }
      if (this.authStore.errorMsg()) {
        this.snackbar.openSnackBar(this.authStore.errorMsg() || '', 'error');
      }
    });
  }

  onOtpChange(token: string): void {
    console.log(token);
    if (token.length === 6) {
      const request: OTPTokenRequest = {
        token,
      };
      this.authStore.loginByOTP(request);
    }
  }
}
