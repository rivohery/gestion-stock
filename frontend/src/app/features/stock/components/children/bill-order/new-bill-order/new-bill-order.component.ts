import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { BillOrderService } from '../bill-order.service';
import {
  CategoryMinResponse,
  ProductResponse,
} from '../../../../models/product.model';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormGroupDirective,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { LoaderComponent } from '../../../../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../../../../shared/components/message-box/message-box.component';
import { CurrencyPipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { SnackbarService } from '../../../../../../shared/services/snackbar-service';
import { BillOrderRequest } from '../bill-order.model';
import { Router } from '@angular/router';

type OrderItemType = {
  productId: number;
  productName: string;
  quantity: number;
  totalItems: number;
};

@Component({
  selector: 'app-new-bill-order',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    LoaderComponent,
    MessageBoxComponent,
    NgIf,
    NgFor,
    DecimalPipe,
    CurrencyPipe,
  ],
  templateUrl: './new-bill-order.component.html',
  styleUrl: './new-bill-order.component.css',
})
export class NewBillOrderComponent implements OnInit {
  billOrderService = inject(BillOrderService);
  fb = inject(FormBuilder);
  snackbar = inject(SnackbarService);
  router = inject(Router);

  categories: CategoryMinResponse[] = [];
  productsByCategory: ProductResponse[] = [];
  selectedProduct?: ProductResponse;
  totalAmount: number = 0;
  loadingPage = signal<boolean>(false);
  errorMsg = signal<string>('');
  panier: OrderItemType[] = [];
  total: number = 0;
  sendingOrder = signal<boolean>(false);

  invoiceNo: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  customer: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  phoneNu: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  email: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.email],
  });
  paymentMethod: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  categoryId: FormControl<number> = new FormControl(-1, {
    nonNullable: true,
    validators: [Validators.required],
  });
  productId: FormControl<number> = new FormControl(-1, {
    nonNullable: true,
    validators: [Validators.required],
  });
  salesPrice: FormControl<number> = new FormControl(0, {
    nonNullable: true,
  });
  qtyStock: FormControl<number> = new FormControl(0, {
    nonNullable: true,
  });
  quantity: FormControl<number> = new FormControl(0, {
    nonNullable: true,
    validators: [Validators.required],
  });
  totalItems: FormControl<number> = new FormControl(0, {
    nonNullable: true,
    validators: [Validators.required],
  });

  billOrderForm: FormGroup<any> = this.fb.nonNullable.group({
    invoiceNo: this.invoiceNo,
    customer: this.customer,
    phoneNu: this.phoneNu,
    email: this.email,
    paymentMethod: this.paymentMethod,
    categoryId: this.categoryId,
    productId: this.productId,
    salesPrice: this.salesPrice,
    qtyStock: this.qtyStock,
    quantity: this.quantity,
    totalItems: this.totalItems,
  });

  constructor() {
    effect(() => {
      const createBillOrderState =
        this.billOrderService.createBillOrderState$();
      if (createBillOrderState.status === 'OK') {
        const billOrderResponse = createBillOrderState.value;
        this.sendingOrder.set(false);
        this.snackbar.openSnackBar(
          'La commande a été enregistré avec succées',
          'success'
        );
        this.router.navigateByUrl(
          '/stock/bill-order/view/' + billOrderResponse?.id
        );
      }
      if (createBillOrderState.status === 'ERROR') {
        this.sendingOrder.set(false);
        this.errorMsg.set(createBillOrderState.error || '');
      }
    });
  }

  ngOnInit(): void {
    this.billOrderService.initCreateBillOrderState();
    this.loadingPage.set(true);
    this.billOrderService.checkUniqueInvoiceNo().subscribe({
      next: (resp) => {
        const invoiceNo: string = resp.invoiceNo;
        this.billOrderForm.patchValue({
          invoiceNo: invoiceNo,
        });
        this.fetchAllCategory();
      },
      error: (err) => {
        this.loadingPage.set(false);
        this.errorMsg.set(err.message);
      },
    });
  }

  private fetchAllCategory(): void {
    this.billOrderService.findAllCategory().subscribe({
      next: (resp) => {
        this.loadingPage.set(false);
        this.categories = resp;
      },
      error: (err) => {
        this.loadingPage.set(false);
        this.errorMsg.set(err.message);
      },
    });
  }

  closeMessageBox(): void {
    this.errorMsg.set('');
  }

  onSelectCategory(categoryId: number): void {
    console.log(categoryId);
    this.billOrderService.findAllProductByCategoryId(categoryId).subscribe({
      next: (resp) => {
        console.log(resp);
        if (resp.length === 0) {
          this.snackbar.openSnackBar(
            `Pas de produit actif sur cette categorie`,
            'error'
          );
        }
        this.productsByCategory = resp;
      },
      error: (err) => console.log(err),
    });
  }

  onSelectProduct(productId: number): void {
    this.selectedProduct = this.productsByCategory.find(
      (product) => product.id === productId
    );
    this.billOrderForm.patchValue({
      salesPrice: this.selectedProduct?.salesPrice,
      qtyStock: this.selectedProduct?.qtyStock,
    });
  }

  calculTotalItems(): void {
    const totalItems =
      this.billOrderForm.get('salesPrice')?.value *
      this.billOrderForm.get('quantity')?.value;
    this.billOrderForm.patchValue({
      totalItems: totalItems,
    });
  }

  addOrderItem(formDirective: FormGroupDirective): void {
    if (this.billOrderForm.invalid) {
      this.snackbar.openSnackBar('Certains champs sont invalides', 'error');
      return;
    }
    const currentInvoiceNo = this.billOrderForm.get('invoiceNo')?.value;
    const currentCustomer = this.billOrderForm.get('customer')?.value;
    const currentPhoneNu = this.billOrderForm.get('phoneNu')?.value;
    const currentEmail = this.billOrderForm.get('email')?.value;
    const currentPaymentMethod = this.billOrderForm.get('paymentMethod')?.value;

    if (
      !this.panier.find(
        (item: OrderItemType) => item.productId === this.selectedProduct?.id
      )
    ) {
      this.panier.push({
        productId: this.billOrderForm.get('productId')?.value,
        productName: this.selectedProduct?.name || '',
        quantity: this.billOrderForm.get('quantity')?.value,
        totalItems: this.billOrderForm.get('totalItems')?.value,
      } as OrderItemType);
      this.total = this.panier.reduce((acc, item) => acc + item.totalItems, 0);
    }
    formDirective.resetForm(); // resetForm() réinitialise les validateurs et l'état visuel
    this.selectedProduct = undefined;

    this.billOrderForm.patchValue({
      invoiceNo: currentInvoiceNo,
      customer: currentCustomer,
      phoneNu: currentPhoneNu,
      email: currentEmail,
      paymentMethod: currentPaymentMethod,
    });
  }

  sendBillOrder(): void {
    const billOrderRequest: BillOrderRequest = {
      invoiceNo: this.billOrderForm.get('invoiceNo')?.value,
      customer: this.billOrderForm.get('customer')?.value,
      phoneNu: this.billOrderForm.get('phoneNu')?.value,
      email: this.billOrderForm.get('email')?.value,
      paymentMethod: this.billOrderForm.get('paymentMethod')?.value,
      total: this.total,
      items: this.panier,
    };
    this.sendingOrder.set(true);
    this.billOrderService.createBillOrder(billOrderRequest);
  }
}
