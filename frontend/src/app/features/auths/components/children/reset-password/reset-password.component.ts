import { NgIf } from '@angular/common';
import { Component, effect, inject, signal } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NgxControlError } from 'ngxtension/control-error';
import { AuthsService } from '../../../services/auths.service';
import { UpdatePasswordRequest } from '../../../models/auths.model';
import { SnackbarService } from '../../../../../shared/services/snackbar-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-reset-password',
  imports: [
    MatCardModule,
    MatIconModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    NgIf,
    NgxControlError,
  ],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css',
})
export class ResetPasswordComponent {
  fb = inject(FormBuilder);
  loading = signal<boolean>(false);
  authService = inject(AuthsService);
  snackbar = inject(SnackbarService);
  router = inject(Router);

  email: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.email],
  });
  oldPassword: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  newPassword: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(4)],
  });

  resetPswdForm: FormGroup<any> = this.fb.nonNullable.group({
    email: this.email,
    oldPassword: this.oldPassword,
    newPassword: this.newPassword,
  });

  constructor() {
    effect(() => {
      if (this.authService.resetPswdState$().status === 'OK') {
        this.loading.set(false);
        this.snackbar.openSnackBar(
          this.authService.resetPswdState$().value?.message || '',
          'success'
        );
        this.router.navigateByUrl('/login');
      }
      if (this.authService.resetPswdState$().status === 'ERROR') {
        this.loading.set(false);
        this.snackbar.openSnackBar(
          this.authService.resetPswdState$().error || '',
          'error'
        );
      }
    });
  }

  get emailInvalid(): boolean {
    return this.email.invalid && (this.email.touched || this.email.dirty);
  }

  get oldPswdInvalid(): boolean {
    return (
      this.oldPassword.invalid &&
      (this.oldPassword.touched || this.oldPassword.dirty)
    );
  }

  get newPswdInvalid(): boolean {
    return (
      this.newPassword.invalid &&
      (this.newPassword.touched || this.newPassword.dirty)
    );
  }

  updatePswdProcess(): void {
    if (this.resetPswdForm.invalid) {
      console.log('Data invalid');
      return;
    }
    const request: UpdatePasswordRequest = this.resetPswdForm.value;
    this.loading.set(true);
    this.authService.updatePswdProcess(request);
  }

  cancel(): void {
    this.resetPswdForm.reset();
    this.router.navigateByUrl('/login');
  }
}
