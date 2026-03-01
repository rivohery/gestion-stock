import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { PaginationComponent } from '../../../../../../shared/components/pagination/pagination.component';
import { MatIconModule } from '@angular/material/icon';
import { DatePipe, NgClass, NgFor, NgIf } from '@angular/common';
import { PurchaseOrderService } from '../purchase-order.service';
import { PurchaseOrderMinResponse } from '../puchase-order.model';
import { LoaderComponent } from '../../../../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../../../../shared/components/message-box/message-box.component';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogModule,
} from '@angular/material/dialog';
import { ConfirmAlertDialogComponent } from '../../../../../../shared/components/confirm-alert-dialog/confirm-alert-dialog.component';
import { SnackbarService } from '../../../../../../shared/services/snackbar-service';
import { Router } from '@angular/router';
import { injectAuthsStore } from '../../../../../../core/store/auths/auths.facade';

@Component({
  selector: 'app-purchase-order-list',
  imports: [
    MatIconModule,
    MatDialogModule,
    PaginationComponent,
    LoaderComponent,
    MessageBoxComponent,
    NgIf,
    NgFor,
    NgClass,
    DatePipe,
  ],
  templateUrl: './purchase-order-list.component.html',
  styleUrl: './purchase-order-list.component.css',
})
export class PurchaseOrderListComponent implements OnInit {
  purchaseOrderService = inject(PurchaseOrderService);
  authStore = injectAuthsStore();
  dialog = inject(MatDialog);
  snackbar = inject(SnackbarService);
  router = inject(Router);

  loadingPurchaseOrderList = signal<boolean>(false);
  errorMsg = signal<string>('');
  purchaseOrderList: PurchaseOrderMinResponse[] = [];
  currentPage: number = 0;
  totalPages: number = 0;
  size: number = 6;

  orderDeletingId?: number;

  constructor() {
    effect(() => {
      const findAllPurchaseOrderState =
        this.purchaseOrderService.findAllPurchaseOrderState$();
      if (findAllPurchaseOrderState.status === 'OK') {
        this.loadingPurchaseOrderList.set(false);
        this.purchaseOrderList = findAllPurchaseOrderState.value?.content || [];
        this.totalPages = findAllPurchaseOrderState.value?.totalPages || 0;
        this.currentPage = findAllPurchaseOrderState.value?.number || 0;
      }
      if (findAllPurchaseOrderState.status === 'ERROR') {
        this.loadingPurchaseOrderList.set(false);
        this.errorMsg.set(findAllPurchaseOrderState.error || '');
      }
    });

    effect(() => {
      const deletePurchaseOrderState =
        this.purchaseOrderService.deletePurchaseOrderIdState$();
      if (deletePurchaseOrderState.status === 'OK') {
        const succesMsg: string = deletePurchaseOrderState.value?.message || '';
        this.snackbar.openSnackBar(succesMsg, 'success');
        this.currentPage = 0;
        this.fetchAllPurchaseOrder();
      }
      if (deletePurchaseOrderState.status === 'ERROR') {
        const errorMsg: string = deletePurchaseOrderState.error || '';
        this.errorMsg.set(errorMsg);
      }
    });
  }

  ngOnInit(): void {
    this.currentPage = 0;
    this.loadingPurchaseOrderList.set(true);
    this.purchaseOrderService.initDeletePurchaseOrderIdState();
    this.fetchAllPurchaseOrder();
  }

  fetchAllPurchaseOrder(): void {
    this.purchaseOrderService.findAllPurchaseOrder(this.currentPage, this.size);
  }

  close(): void {
    this.errorMsg.set('');
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.fetchAllPurchaseOrder();
  }

  deletePurchaseOrder(purchaseOrder: PurchaseOrderMinResponse): void {
    if (purchaseOrder && purchaseOrder.id) {
      const dialogConfig = new MatDialogConfig();
      dialogConfig.disableClose = true; // Empêche la fermeture en cliquant à l'extérieur
      dialogConfig.width = '100%';
      dialogConfig.maxWidth = '400px';

      const dialogRef = this.dialog.open(
        ConfirmAlertDialogComponent,
        dialogConfig
      );

      dialogRef.afterClosed().subscribe({
        next: (confirm) => {
          if (confirm) {
            this.orderDeletingId = purchaseOrder.id;
            this.purchaseOrderService.deletePurchaseOrderId(purchaseOrder.id);
          }
        },
      });
    }
  }

  ckeckPurchaseOrderDetails(purchaseOrder: PurchaseOrderMinResponse): void {
    console.log(purchaseOrder);
    if (purchaseOrder && purchaseOrder.id) {
      this.router.navigateByUrl(
        '/stock/purchase-order/view/' + purchaseOrder.id
      );
    }
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
}
