import {
  Component,
  Input,
  OnInit,
  effect,
  inject,
  signal,
} from '@angular/core';
import {
  CategoryMinResponse,
  ProductRequest,
  ProductResponse,
  SupplierMinResponse,
} from '../../../../../../models/product.model';
import { ProductService } from '../../../../../../services/product.service';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ImageUploadComponent } from '../../../../../../../../shared/components/image-upload/image-upload.component';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { NgFor, NgIf } from '@angular/common';
import { NgxControlError } from 'ngxtension/control-error';
import { Router } from '@angular/router';
import { SnackbarService } from '../../../../../../../../shared/services/snackbar-service';

@Component({
  selector: 'app-product-form',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    ImageUploadComponent,
    NgFor,
    NgIf,
    NgxControlError,
  ],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.css',
})
export class ProductFormComponent implements OnInit {
  @Input()
  editProduct: ProductResponse | undefined;

  categories = signal<CategoryMinResponse[]>([]);
  suppliers = signal<SupplierMinResponse[]>([]);

  submitting = signal<boolean>(false);
  errorMsg: string = '';

  currentPhoto?: string;
  isUploading: boolean = false;

  productServcie = inject(ProductService);
  fb = inject(FormBuilder);
  router = inject(Router);
  snackbar = inject(SnackbarService);

  code: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  name: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  salesPrice: FormControl<number> = new FormControl(0, {
    nonNullable: true,
    validators: [Validators.required],
  });
  costPrice: FormControl<number> = new FormControl(0, {
    nonNullable: true,
    validators: [Validators.required],
  });
  alertStock: FormControl<number> = new FormControl(0, {
    nonNullable: true,
    validators: [Validators.required],
  });
  unity: FormControl<string> = new FormControl('none', {
    nonNullable: true,
    validators: [Validators.required],
  });
  categoryId: FormControl<number> = new FormControl(-1, {
    nonNullable: true,
    validators: [Validators.required],
  });
  supplierId: FormControl<number> = new FormControl(-1, {
    nonNullable: true,
    validators: [Validators.required],
  });

  productForm: FormGroup<any> = this.fb.nonNullable.group({
    code: this.code,
    name: this.name,
    salesPrice: this.salesPrice,
    costPrice: this.costPrice,
    alertStock: this.alertStock,
    unity: this.unity,
    categoryId: this.categoryId,
    supplierId: this.supplierId,
  });

  constructor() {
    effect(() => {
      if (this.productServcie.gatCategoryListState$().status === 'OK') {
        this.categories.set(
          this.productServcie.gatCategoryListState$().value ?? []
        );
      }
      if (this.productServcie.gatCategoryListState$().status === 'ERROR') {
        console.log(this.productServcie.gatCategoryListState$().error);
      }
    });

    effect(() => {
      if (this.productServcie.getSupplierListState$().status === 'OK') {
        this.suppliers.set(
          this.productServcie.getSupplierListState$().value ?? []
        );
      }
      if (this.productServcie.getSupplierListState$().status === 'ERROR') {
        console.log(this.productServcie.getSupplierListState$().error);
      }
    });

    effect(() => {
      if (this.productServcie.createProductState$().status === 'OK') {
        this.submitting.set(false);
        this.snackbar.openSnackBar(
          this.productServcie.createProductState$().value?.message || '',
          'success'
        );
        this.productForm.reset();
      }
      if (this.productServcie.createProductState$().status === 'ERROR') {
        this.submitting.set(false);
        this.errorMsg = this.productServcie.createProductState$().error || '';
      }
    });

    effect(() => {
      if (this.productServcie.updateProductState$().status === 'OK') {
        this.submitting.set(false);
        this.snackbar.openSnackBar(
          this.productServcie.updateProductState$().value?.message || '',
          'success'
        );
      }
      if (this.productServcie.updateProductState$().status === 'ERROR') {
        this.submitting.set(false);
        this.errorMsg = this.productServcie.updateProductState$().error || '';
      }
    });
  }

  initProductState(): void {
    this.productServcie.initCreateProductState();
    this.productServcie.initUpdateProductState();
  }

  ngOnInit(): void {
    this.initProductState();
    if (this.editProduct) {
      this.productForm.setValue({
        code: this.editProduct.code,
        name: this.editProduct.name,
        salesPrice: this.editProduct.salesPrice,
        costPrice: this.editProduct.costPrice,
        alertStock: this.editProduct.alertStock,
        unity: this.editProduct.unity,
        categoryId: this.editProduct.categoryId,
        supplierId: this.editProduct.supplierId,
      });
      this.currentPhoto = this.editProduct.photos;
    }
  }

  uploadImageProduct(file: File): void {
    if (!file) {
      console.log('No file checked!');
      return;
    }
    if (this.editProduct && this.editProduct.id) {
      const formData = new FormData();
      formData.append('productId', '' + this.editProduct?.id);
      formData.append('file', file);
      this.isUploading = true;
      this.productServcie.uploadPhoto(formData).subscribe({
        next: (resp) => {
          this.isUploading = false;
          this.snackbar.openSnackBar(resp.message || '', 'success');
        },
        error: (err) => {
          this.isUploading = false;
          console.log(err.message);
        },
      });
    }
  }

  onSubmit(): void {
    if (this.productForm.invalid) {
      console.log(this.productForm.value);
      return;
    }
    this.submitting.set(true);
    const request: ProductRequest = this.productForm.value;
    if (this.editProduct && this.editProduct.id) {
      request.id = this.editProduct.id;
      this.productServcie.updateProduct(request);
    } else {
      this.productServcie.createProduct(request);
    }
  }

  cancel(): void {
    this.productForm.reset();
    this.router.navigateByUrl('/stock/admin/products');
  }
}
