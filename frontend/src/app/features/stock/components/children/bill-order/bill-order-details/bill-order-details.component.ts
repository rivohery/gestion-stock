import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { BillOrderResponse } from '../bill-order.model';
import { BillOrderService } from '../bill-order.service';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MessageBoxComponent } from '../../../../../../shared/components/message-box/message-box.component';
import { CurrencyPipe, DatePipe, NgIf } from '@angular/common';
import { LoaderComponent } from '../../../../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-bill-order-details',
  imports: [
    RouterLink,
    LoaderComponent,
    MessageBoxComponent,
    DatePipe,
    CurrencyPipe,
    NgIf,
  ],
  templateUrl: './bill-order-details.component.html',
  styleUrl: './bill-order-details.component.css',
})
export class BillOrderDetailsComponent implements OnInit {
  billOrderDetails?: BillOrderResponse;
  billOrderService = inject(BillOrderService);
  activateRoute = inject(ActivatedRoute);

  errorMsg = signal<string>('');
  orderDetailLoading = signal<boolean>(false);
  invoiceLoading = signal<boolean>(false);

  constructor() {
    effect(() => {
      const findBillOrderByIdState =
        this.billOrderService.findBillOrderByIdState$();
      if (findBillOrderByIdState.status === 'OK') {
        this.orderDetailLoading.set(false);
        this.billOrderDetails = findBillOrderByIdState.value;
      }
      if (findBillOrderByIdState.status === 'ERROR') {
        this.orderDetailLoading.set(false);
        this.errorMsg.set(findBillOrderByIdState.error || '');
      }
    });
  }

  ngOnInit(): void {
    const billOrderId = this.activateRoute.snapshot.paramMap.get('billOrderId');
    if (billOrderId) {
      this.orderDetailLoading.set(true);
      this.billOrderService.findBillOrderById(Number(billOrderId));
    }
  }

  closeMsgBox(): void {
    this.errorMsg.set('');
  }

  getInvoicePdf(): void {
    if (this.billOrderDetails) {
      this.invoiceLoading.set(true);
      this.billOrderService
        .getInvoicePdf(this.billOrderDetails.invoiceNo)
        .subscribe({
          next: (blob) => {
            if (typeof window !== 'undefined') {
              const url = window.URL.createObjectURL(blob);
              const a = document.createElement('a');
              a.href = url;
              a.download = 'facture.pdf';
              a.click();
              window.URL.revokeObjectURL(url);
            }
          },
          error: (err) => console.log(err),
          complete: () => this.invoiceLoading.set(false),
        });
    }
  }
}
