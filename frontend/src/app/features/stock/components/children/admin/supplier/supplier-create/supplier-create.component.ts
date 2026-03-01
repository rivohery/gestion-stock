import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { SupplierService } from '../../../../../services/supplier.service';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Supplier } from '../../../../../models/product.model';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { NgxControlError } from 'ngxtension/control-error';
import { Router } from '@angular/router';
import { SnackbarService } from '../../../../../../../shared/services/snackbar-service';
import { MessageBoxComponent } from '../../../../../../../shared/components/message-box/message-box.component';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-supplier-create',
  imports: [
    MatButtonModule,
    MatInputModule,
    MatIconModule,
    FormsModule,
    ReactiveFormsModule,
    NgxControlError,
    MessageBoxComponent,
    NgIf,
  ],
  templateUrl: './supplier-create.component.html',
  styleUrl: './supplier-create.component.css',
})
export class SupplierCreateComponent implements OnInit {
  title: string = 'Ajout fournisseur';
  supplierService = inject(SupplierService);
  router = inject(Router);
  snackbar = inject(SnackbarService);

  submitting = signal<boolean>(false);
  mode: string = 'add';
  errorMsg: string = '';

  name: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  email: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.email],
  });
  phoneNu: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  address: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  supplierForm: FormGroup<any> = new FormGroup({
    name: this.name,
    email: this.email,
    phoneNu: this.phoneNu,
    address: this.address,
  });

  constructor() {
    effect(() => {
      if (this.supplierService.findByIdState$().value) {
        this.title = 'Modifier fournisseur';
        this.mode = 'edit';
        const edit: Supplier = this.supplierService.findByIdState$()
          .value as Supplier;
        this.supplierForm.setValue({
          name: edit.name,
          email: edit.email,
          phoneNu: edit.phoneNu,
          address: edit.address,
        });
      }
    });

    effect(() => {
      if (
        this.supplierService.updateState$().status === 'OK' ||
        this.supplierService.createState$().status === 'OK'
      ) {
        const successMsg: string =
          this.mode === 'add'
            ? this.supplierService.createState$().value?.message || ''
            : this.supplierService.updateState$().value?.message || '';
        this.snackbar.openSnackBar(successMsg, 'success');
        this.router.navigateByUrl('/stock/admin/suppliers');
      }

      if (
        this.supplierService.updateState$().status === 'ERROR' ||
        this.supplierService.createState$().status === 'ERROR'
      ) {
        this.errorMsg =
          this.mode === 'add'
            ? this.supplierService.createState$().error || ''
            : this.supplierService.updateState$().error || '';
      }
    });
  }

  ngOnInit(): void {}

  onCancel(): void {
    this.supplierForm.reset();
    this.router.navigateByUrl('/stock/admin/suppliers');
  }

  onSubmit(): void {
    if (this.supplierForm.invalid) {
      console.log(this.supplierForm.value);
      return;
    }
    this.submitting.set(true);
    const supplier: Supplier = {
      name: this.supplierForm.value.name,
      email: this.supplierForm.value.email,
      address: this.supplierForm.value.address,
      phoneNu: this.supplierForm.value.phoneNu,
    };
    if (this.mode === 'add') {
      this.supplierService.create(supplier);
    }
    if (this.mode === 'edit') {
      supplier.id = this.supplierService.findByIdState$().value?.id;
      this.supplierService.update(supplier);
    }
  }

  close(): void {
    this.errorMsg = '';
  }
}
