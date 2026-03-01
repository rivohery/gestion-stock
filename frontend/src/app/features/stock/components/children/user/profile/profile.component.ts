import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { injectAuthsStore } from '../../../../../../core/store/auths/auths.facade';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { UserDetailsResponse } from '../../../../../auths/models/auths.model';
import { NgIf } from '@angular/common';
import { NgxControlError } from 'ngxtension/control-error';
import { UserService } from '../../../../../auths/services/user.service';
import { UpdateUserInfoRequest } from '../../../../../auths/models/user.model';
import { SnackbarService } from '../../../../../../shared/services/snackbar-service';
import { Router } from '@angular/router';
import { ImageUploadComponent } from '../../../../../../shared/components/image-upload/image-upload.component';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { GlobalResponse } from '../../../../../../shared/models/shared.model';
import { environment } from '../../../../../../../environments/environment.dev';

@Component({
  selector: 'app-profile',
  imports: [
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatCardModule,
    FormsModule,
    ReactiveFormsModule,
    NgIf,
    NgxControlError,
    ImageUploadComponent,
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class ProfileComponent implements OnInit {
  authStore = injectAuthsStore();
  fb = inject(FormBuilder);
  userService = inject(UserService);
  snackbar = inject(SnackbarService);
  router = inject(Router);

  uploadProgress: number = 0;
  isUploading: boolean = false;
  loading = signal<boolean>(false);
  image: string = '';

  userId: FormControl<number> = new FormControl(1, {
    nonNullable: true,
    validators: [Validators.required],
  });
  email: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.email],
  });
  firstName: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  lastName: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  phoneNu: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  profileForm: FormGroup<any> = this.fb.nonNullable.group({
    userId: this.userId,
    email: this.email,
    firstName: this.firstName,
    lastName: this.lastName,
    phoneNu: this.phoneNu,
  });

  get firstNameInvalid(): boolean {
    return (
      this.firstName.invalid && (this.firstName.touched || this.firstName.dirty)
    );
  }
  get lastNameInvalid(): boolean {
    return (
      this.lastName.invalid && (this.lastName.touched || this.lastName.dirty)
    );
  }
  get emailInvalid(): boolean {
    return this.email.invalid && (this.email.touched || this.email.dirty);
  }
  get phoneNuInvalid(): boolean {
    return this.phoneNu.invalid && (this.phoneNu.touched || this.phoneNu.dirty);
  }

  constructor() {
    effect(() => {
      if (this.userService.updateUserInfosState().status === 'OK') {
        this.loading.set(false);
        this.snackbar.openSnackBar(
          this.userService.updateUserInfosState().value?.message || '',
          'success'
        );
      }
      if (this.userService.updateUserInfosState().status === 'ERROR') {
        this.loading.set(false);
        this.snackbar.openSnackBar(
          this.userService.updateUserInfosState().error || '',
          'error'
        );
      }
    });
  }

  ngOnInit(): void {
    if (this.authStore.userDetails()) {
      const userDetails: UserDetailsResponse =
        this.authStore.userDetails() as UserDetailsResponse;
      this.profileForm.setValue({
        userId: userDetails.userId,
        email: userDetails.email,
        firstName: userDetails.fullName.split(' ')[0],
        lastName: userDetails.fullName.split(' ')[1],
        phoneNu: userDetails.phoneNu,
      });
      if (userDetails.profileImageUrl) {
        this.userService
          .downloadProfile(userDetails.profileImageUrl)
          .subscribe({
            next: (resp) => (this.image = resp as string),
            error: (err) => console.log(err.message),
          });
      }
    }
  }

  updateUserInfos(): void {
    if (this.profileForm.invalid) {
      console.log(this.profileForm.value);
      return;
    }
    const request: UpdateUserInfoRequest = { ...this.profileForm.value };
    this.loading.set(true);
    this.userService.updateUserInfos(request);
  }

  cancel(): void {
    this.router.navigateByUrl('/stock/admin/dashboard');
  }

  uploadImage(file: File): void {
    if (!file) {
      console.log('No file checked!');
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', '' + this.authStore.userDetails()?.userId);
    this.isUploading = true;
    this.userService.uploadImage(formData).subscribe({
      next: (resp) => {
        this.isUploading = false;
        this.snackbar.openSnackBar(resp?.message || '', 'success');
      },
      error: (err) => {
        this.isUploading = false;
        this.snackbar.openSnackBar(err.message, 'error');
      },
    });
  }
}
