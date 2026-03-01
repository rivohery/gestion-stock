import { Component, effect, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { AuthsService } from '../../../services/auths.service';
import {
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NgIf } from '@angular/common';
import { NgxControlError } from 'ngxtension/control-error';
import { ForgotPasswordRequest } from '../../../models/auths.model';
import { SnackbarService } from '../../../../../shared/services/snackbar-service';

@Component({
  selector: 'app-forgot-password',
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    NgIf,
    NgxControlError,
  ],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent {
  authService = inject(AuthsService);
  snackbar = inject(SnackbarService);

  loading = signal<boolean>(false);

  constructor() {
    effect(() => {
      if (this.authService.forgotPswdState$().status === 'OK') {
        this.loading.set(false);
        this.snackbar.openSnackBar(
          this.authService.forgotPswdState$().value?.message || '',
          'success'
        );
      }
      if (this.authService.forgotPswdState$().status === 'ERROR') {
        this.loading.set(false);
        this.snackbar.openSnackBar(
          this.authService.forgotPswdState$().error || '',
          'error'
        );
      }
    });
  }

  emailControl: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.email],
  });

  get emailInvalid(): boolean {
    return (
      this.emailControl.invalid &&
      (this.emailControl.touched || this.emailControl.dirty)
    );
  }

  forgotPswdProcess(): void {
    if (this.emailControl.invalid) {
      return;
    }
    const request: ForgotPasswordRequest = {
      email: this.emailControl.value,
    };
    this.loading.set(true);
    this.authService.forgotPswdProcess(request);
  }
}
