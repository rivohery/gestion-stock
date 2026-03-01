import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { BillOrderService } from '../bill-order.service';
import { BillOrderMinResponse } from '../bill-order.model';
import { PaginationComponent } from '../../../../../../shared/components/pagination/pagination.component';
import { MessageBoxComponent } from '../../../../../../shared/components/message-box/message-box.component';
import { CurrencyPipe, DatePipe, NgFor, NgIf } from '@angular/common';
import { LoaderComponent } from '../../../../../../shared/components/loader/loader.component';
import { MatIconModule } from '@angular/material/icon';
import { SnackbarService } from '../../../../../../shared/services/snackbar-service';
import { injectAuthsStore } from '../../../../../../core/store/auths/auths.facade';

@Component({
  selector: 'app-bill-order-list',
  imports: [
    MatIconModule,
    LoaderComponent,
    MessageBoxComponent,
    PaginationComponent,
    NgIf,
    NgFor,
    DatePipe,
    CurrencyPipe,
  ],
  templateUrl: './bill-order-list.component.html',
  styleUrl: './bill-order-list.component.css',
})
export class BillOrderListComponent implements OnInit {
  billOrderService = inject(BillOrderService);
  router = inject(Router);
  snackbar = inject(SnackbarService);
  authStore = injectAuthsStore();

  billOrderList: BillOrderMinResponse[] = [];
  currentPage: number = 0;
  currentSize: number = 6;
  totalPages: number = 0;

  loadingOrders = signal<boolean>(false);
  errorMsg = signal<string>('');
  orderDeletingId: number = -999;

  constructor() {
    effect(() => {
      const findAllOrderState = this.billOrderService.findAllOrderState$();
      if (findAllOrderState.status === 'OK') {
        this.loadingOrders.set(false);
        this.billOrderList = findAllOrderState.value?.content || [];
        this.totalPages = findAllOrderState.value?.totalPages || 0;
      }
      if (findAllOrderState.status === 'ERROR') {
        this.loadingOrders.set(false);
        this.errorMsg.set(findAllOrderState.error || '');
      }
    });
  }

  ngOnInit(): void {
    this.loadingOrders.set(true);
    this.checkAllOrder();
  }

  private checkAllOrder(): void {
    this.billOrderService.findAllOrder(this.currentPage, this.currentSize);
  }

  get isAdmin(): boolean {
    const roles: string[] = this.authStore.userDetails()?.role.split(',') || [];
    if (roles.length) {
      if (roles.includes('ADMIN')) {
        return true;
      }
      return false;
    }
    return false;
  }

  closeMessageBox(): void {
    this.errorMsg.set('');
  }

  deleteBillOrder(order: BillOrderMinResponse): void {
    if (order) {
      this.orderDeletingId = order.id;
      this.billOrderService.deleteBillOrderById(order.id).subscribe({
        next: (resp) => {
          this.snackbar.openSnackBar(resp.message || '', 'success');
          this.currentPage = 0;
          this.orderDeletingId = -999;
          this.checkAllOrder();
        },
        error: (err) => {
          this.orderDeletingId = -999;
          this.errorMsg.set(err.message || '');
        },
      });
    }
  }

  ckeckBillOrderDetails(order: BillOrderMinResponse): void {
    if (order) {
      this.router.navigateByUrl('/stock/bill-order/view/' + order.id);
    }
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.checkAllOrder();
  }
}
