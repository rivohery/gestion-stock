import { Component, Inject, inject } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { NgFor, NgIf } from '@angular/common';
import { NgxControlError } from 'ngxtension/control-error';

@Component({
  selector: 'app-create-user-dialog',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatChipsModule,
    NgFor,
    NgIf,
    NgxControlError,
  ],
  templateUrl: './create-user-dialog.component.html',
  styleUrl: './create-user-dialog.component.css',
})
export class CreateUserDialogComponent {
  fb = inject(FormBuilder);
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
  email: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.email],
  });
  roles: FormControl<string[]> = new FormControl([], {
    nonNullable: true,
    validators: [Validators.required],
  });

  roleList: { value: string; label: string }[] = [
    { value: 'ADMIN', label: 'ADMIN' },
    { value: 'STOCK_MANAGER', label: 'STOCK MANAGER' },
    { value: 'SALES_MANAGER', label: 'SALES MANAGER' },
    { value: 'VIEWER', label: 'VIEWER' },
  ];

  createUserForm: FormGroup<any> = this.fb.nonNullable.group({
    firstName: this.firstName,
    lastName: this.lastName,
    phoneNu: this.phoneNu,
    email: this.email,
    roles: this.roles,
  });

  constructor(private dialogRef: MatDialogRef<CreateUserDialogComponent>) {}

  onRoleRemoved(role: string) {
    const roles = this.createUserForm.get('roles') as FormControl;
    const currentValues = roles.value as string[];
    const updatedValues = currentValues.filter((r) => r !== role);
    this.createUserForm.get('roles')?.setValue(updatedValues);
  }

  onSubmit() {
    if (this.createUserForm?.valid) {
      console.log(this.createUserForm.value);
      this.dialogRef.close(this.createUserForm.value);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
