import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { PurchaseOrderService } from '../purchase-order.service';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormGroupDirective,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import {
  CurrencyPipe,
  DatePipe,
  DecimalPipe,
  JsonPipe,
  NgFor,
  NgIf,
} from '@angular/common';
import {
  ProductResponse,
  SupplierMinResponse,
} from '../../../../models/product.model';
import { MessageBoxComponent } from '../../../../../../shared/components/message-box/message-box.component';
import { MatSelectModule } from '@angular/material/select';
import { SnackbarService } from '../../../../../../shared/services/snackbar-service';
import {
  PurchaseOrderItemRequest,
  PurchaseOrderRequest,
} from '../puchase-order.model';
import { Router } from '@angular/router';
import { formatISO } from 'date-fns';
import { LoaderComponent } from '../../../../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-create-purchase',
  providers: [provideNativeDateAdapter()],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    NgIf,
    NgFor,
    DatePipe,
    JsonPipe,
    DecimalPipe,
    CurrencyPipe,
    MessageBoxComponent,
    LoaderComponent,
  ],
  templateUrl: './create-purchase.component.html',
  styleUrl: './create-purchase.component.css',
})
export class CreatePurchaseComponent implements OnInit {
  purchaseOrderService = inject(PurchaseOrderService);
  fb = inject(FormBuilder);
  snackbar = inject(SnackbarService);
  router = inject(Router);

  invoiceNumberChecking = signal<boolean>(false);
  errorMsg = signal<string>('');
  supplierList: SupplierMinResponse[] = [];
  productListBySupplier: ProductResponse[] = [];
  checkedProduct?: ProductResponse;
  orderItems: PurchaseOrderItemRequest[] = [];
  totalAmounts: number = 0;
  sendingOrder = signal<boolean>(false);

  invoiceNo: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  supplierId: FormControl<number | null> = new FormControl(null, {
    nonNullable: true,
    validators: [Validators.required],
  });
  receiveDate: FormControl<Date> = new FormControl(new Date(), {
    nonNullable: true,
    validators: [Validators.required],
  });
  productId: FormControl<number | null> = new FormControl(null, {
    nonNullable: true,
    validators: [Validators.required],
  });
  quantity: FormControl<number> = new FormControl(0, {
    nonNullable: true,
    validators: [Validators.required, Validators.min(1)],
  });
  itemsPrice: FormControl<number> = new FormControl(0.0, {
    nonNullable: true,
    validators: [Validators.required, Validators.min(0.1)],
  });

  purchaseOrderForm: FormGroup<any> = this.fb.nonNullable.group({
    invoiceNo: this.invoiceNo,
    supplierId: this.supplierId,
    receiveDate: this.receiveDate,
    productId: this.productId,
    quantity: this.quantity,
    itemsPrice: this.itemsPrice,
  });

  constructor() {
    effect(() => {
      if (
        this.purchaseOrderService.invoiceNumberCheckedState$().status === 'OK'
      ) {
        let invoiceNo =
          this.purchaseOrderService.invoiceNumberCheckedState$().value
            .invoiceNo;
        this.purchaseOrderForm.get('invoiceNo')?.setValue(invoiceNo);
        this.getListSupplier();
        this.invoiceNumberChecking.set(true);
      }
      if (
        this.purchaseOrderService.invoiceNumberCheckedState$().status ===
        'ERROR'
      ) {
        this.invoiceNumberChecking.set(true);
        this.errorMsg.set(
          this.purchaseOrderService.invoiceNumberCheckedState$().error || ''
        );
      }
    });

    effect(() => {
      const createPurchaseOrderState =
        this.purchaseOrderService.createPurchaseOrderState$();
      if (createPurchaseOrderState.status === 'OK') {
        this.sendingOrder.set(false);
        this.snackbar.openSnackBar(
          'Votre commande a été enregistrée avec succées',
          'success'
        );
        this.router.navigate([
          '/stock/purchase-order/view',
          createPurchaseOrderState.value?.id,
        ]);
      }
      if (createPurchaseOrderState.status === 'ERROR') {
        this.sendingOrder.set(false);
        this.errorMsg.set(createPurchaseOrderState?.error || '');
      }
    });
  }

  ngOnInit(): void {
    console.log('initialisation du composant');
    this.purchaseOrderService.initCreatePurchaseOrderState();
    this.purchaseOrderService.generateUniqueInvoiceNo();
  }

  getListSupplier(): void {
    this.purchaseOrderService.getSupplierList().subscribe({
      next: (resp) => {
        this.supplierList = resp;
      },
      error: (err) => console.log(err),
    });
  }

  close(): void {
    this.errorMsg.set('');
  }

  getProductsBySupplierId(supplierId: number) {
    console.log(supplierId);
    this.purchaseOrderService.getProductsBySupplierId(supplierId).subscribe({
      next: (resp) => {
        if (resp.length === 0) {
          this.snackbar.openSnackBar(
            'Zéro produit enrégistré pour ce fournisseur dans BD',
            'error'
          );
        } else {
          this.productListBySupplier = resp;
        }
      },
      error: (err) => console.log(err.message),
    });
  }

  checkProduct(productId: number): void {
    this.checkedProduct = this.productListBySupplier.find(
      (product) => product.id === productId
    );
    console.log(this.checkedProduct);
  }

  calculTotalItems(): void {
    if (this.checkedProduct) {
      this.purchaseOrderForm.patchValue({
        itemsPrice:
          this.checkedProduct?.costPrice *
          this.purchaseOrderForm.get('quantity')?.value,
      });
    }
  }

  addPurchaseItem(formDirective: FormGroupDirective): void {
    if (this.purchaseOrderForm.invalid) {
      this.snackbar.openSnackBar('Certains champs sont invalides', 'error');
      return;
    }
    console.log(this.purchaseOrderForm.value);
    const currentInvoice = this.purchaseOrderForm.get('invoiceNo')?.value;
    const currentReceiveDate = this.purchaseOrderForm.get('receiveDate')?.value;
    const currentSupplierId = this.purchaseOrderForm.get('supplierId')?.value;

    if (
      !this.orderItems.find(
        (item) => item.productId === this.checkedProduct?.id
      )
    ) {
      this.orderItems.push({
        productId: this.purchaseOrderForm.get('productId')?.value,
        productName: this.checkedProduct?.name,
        quantity: this.purchaseOrderForm.get('quantity')?.value,
        totalItems: this.purchaseOrderForm.get('itemsPrice')?.value,
      } as PurchaseOrderItemRequest);
      this.totalAmounts = this.orderItems.reduce(
        (acc, item) => acc + item.totalItems,
        0
      );
    }

    formDirective.resetForm(); // resetForm() réinitialise les validateurs et l'état visuel
    this.checkedProduct = undefined;

    this.purchaseOrderForm.patchValue({
      invoiceNo: currentInvoice,
      receiveDate: currentReceiveDate,
      supplierId: currentSupplierId,
    });
  }

  removeItem(productId: number): void {
    this.orderItems = this.orderItems.filter(
      (item) => item.productId !== productId
    );
  }

  sendPurchaseOrder(): void {
    if (this.orderItems.length === 0 || this.totalAmounts === 0) {
      this.snackbar.openSnackBar(
        'Votre panier des commandes est vide',
        'error'
      );
      return;
    }
    if (
      !this.purchaseOrderForm.get('invoiceNo')?.value ||
      !this.purchaseOrderForm.get('supplierId')?.value ||
      !this.purchaseOrderForm.get('receiveDate')?.value
    ) {
      this.snackbar.openSnackBar(
        'Certains champs de formulaire des commandes est invalide',
        'error'
      );
      return;
    }
    // Conversion manuelle au format YYYY-MM-DD pour eviter le décalage de fuseau horaire
    // On utilise le format 'en-CA' (Canada) qui génère nativement du YYYY-MM-DD
    const formattedDate = new Intl.DateTimeFormat('en-CA').format(
      this.purchaseOrderForm.get('receiveDate')?.value
    );
    const purchaseOrderRequest: PurchaseOrderRequest = {
      invoiceNo: this.purchaseOrderForm.get('invoiceNo')?.value,
      supplierId: this.purchaseOrderForm.get('supplierId')?.value,
      receiveDate: formattedDate,
      totalAmounts: this.totalAmounts,
      items: this.orderItems,
    };
    console.log(purchaseOrderRequest);
    this.sendingOrder.set(true);
    this.purchaseOrderService.createPurchaseOrder(purchaseOrderRequest);
  }
}
