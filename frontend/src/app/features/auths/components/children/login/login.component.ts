import { Component, effect, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterLink } from '@angular/router';
import { NgxControlError } from 'ngxtension/control-error';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { NgIf } from '@angular/common';
import { LoginRequest } from '../../../models/auths.model';
import { injectAuthsStore } from '../../../../../core/store/auths/auths.facade';
import { SnackbarService } from '../../../../../shared/services/snackbar-service';

@Component({
  selector: 'app-login',
  imports: [
    MatCardModule,
    MatIconModule,
    MatInputModule,
    MatButtonModule,
    RouterLink,
    FormsModule,
    ReactiveFormsModule,
    NgIf,
    NgxControlError,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  authStore = injectAuthsStore();
  fb = inject(FormBuilder);
  router = inject(Router);
  snackbar = inject(SnackbarService);

  email = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required, Validators.email],
  });
  password = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(4)],
  });
  loginForm: FormGroup = this.fb.nonNullable.group({
    email: this.email,
    password: this.password,
  });

  constructor() {
    effect(() => {
      //login failed
      if (this.authStore.errorMsg()) {
        this.snackbar.openSnackBar(this.authStore.errorMsg() || '', 'error');
      }
      //login success
      if (this.authStore.userDetails()) {
        this.snackbar.openSnackBar(
          this.authStore.successMsg() || '',
          'success'
        );
        this.router.navigateByUrl('/stock/user/profile');
      }
    });
  }

  get emailInvalid(): boolean {
    return (
      this.loginForm.controls?.['email'].invalid &&
      (this.loginForm.controls?.['email'].touched ||
        this.loginForm.controls?.['email'].dirty)
    );
  }

  get passwordInvalid(): boolean {
    return (
      this.loginForm.controls?.['password'].invalid &&
      (this.loginForm.controls?.['password'].touched ||
        this.loginForm.controls?.['password'].dirty)
    );
  }

  doLogin(): void {
    if (this.loginForm.invalid) {
      console.log('Formulaire invalide');
      return;
    }
    let loginRequest: LoginRequest = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password,
    };
    console.log(loginRequest);
    this.authStore.loginByPswd(loginRequest);
  }
}
