import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import {
  PurchaseOrderResponse,
  UpdateStatusPurchaseOrderRequest,
} from '../puchase-order.model';
import { PurchaseOrderService } from '../purchase-order.service';
import { LoaderComponent } from '../../../../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../../../../shared/components/message-box/message-box.component';
import { CurrencyPipe, DatePipe, NgClass, NgIf } from '@angular/common';
import { SnackbarService } from '../../../../../../shared/services/snackbar-service';
import { OrderDetailsManageComponent } from './fragments/order-details-manage/order-details-manage.component';
import { InvoiceViewComponent } from './fragments/invoice-view/invoice-view.component';

@Component({
  selector: 'app-purchase-order-details',
  imports: [
    LoaderComponent,
    MessageBoxComponent,
    OrderDetailsManageComponent,
    InvoiceViewComponent,
    NgIf,
    NgClass,
    RouterLink,
  ],
  templateUrl: './purchase-order-details.component.html',
  styleUrl: './purchase-order-details.component.css',
})
export class PurchaseOrderDetailsComponent implements OnInit {
  route = inject(ActivatedRoute);
  purchaseOrderService = inject(PurchaseOrderService);
  purchaseOrdeDetails?: PurchaseOrderResponse;
  snackbar = inject(SnackbarService);
  errorMsg = signal<string>('');
  loading = signal<boolean>(false);

  mode: string = 'details';

  updateStatusLoading = signal<boolean>(false);

  ngOnInit(): void {
    const purchaseOrderId = this.route.snapshot.paramMap.get('purchaseOrderId');
    if (purchaseOrderId) {
      this.loading.set(true);
      this.fetchPurchaseOrderById(Number(purchaseOrderId));
    }
  }

  fetchPurchaseOrderById(purchaseOrderId: number): void {
    this.purchaseOrderService.findPurchaseOrderById(purchaseOrderId).subscribe({
      next: (resp) => {
        this.loading.set(false);
        this.purchaseOrdeDetails = resp;
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMsg.set(err.message);
      },
    });
  }

  close(): void {
    this.errorMsg.set('');
  }

  updateStatusOfPurchaseOrder(request: UpdateStatusPurchaseOrderRequest): void {
    this.updateStatusLoading.set(true);
    this.purchaseOrderService.updateStatusOfPurchaseOrder(request).subscribe({
      next: (resp) => {
        this.updateStatusLoading.set(false);
        this.snackbar.openSnackBar(resp.message || '', 'success');
        this.fetchPurchaseOrderById(this.purchaseOrdeDetails?.id || -1);
      },
      error: (err) => {
        this.updateStatusLoading.set(false);
        this.errorMsg.set(err.message);
      },
    });
  }

  generateInvoice(mode: string): void {
    this.mode = mode;
  }

  goBackToDetails(mode: string): void {
    this.mode = mode;
  }
}
