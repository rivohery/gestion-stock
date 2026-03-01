import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  PurchaseOrderResponse,
  PurchaseOrderStatus,
  UpdateStatusPurchaseOrderRequest,
} from '../../../puchase-order.model';
import { CurrencyPipe, DatePipe, NgClass, NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PurchaseOrderService } from '../../../purchase-order.service';

type orderStatusType = {
  value: PurchaseOrderStatus;
  label: string;
};

@Component({
  selector: 'app-order-details-manage',
  imports: [
    MatFormFieldModule,
    MatSelectModule,
    ReactiveFormsModule,
    FormsModule,
    RouterLink,
    DatePipe,
    CurrencyPipe,
    NgClass,
    NgIf,
  ],
  templateUrl: './order-details-manage.component.html',
  styleUrl: './order-details-manage.component.css',
})
export class OrderDetailsManageComponent implements OnInit {
  @Input()
  purchaseOrdeDetails?: PurchaseOrderResponse;
  @Input()
  updateStatusLoading: boolean = false;

  @Output()
  updateStatusOnChange: EventEmitter<UpdateStatusPurchaseOrderRequest> =
    new EventEmitter();
  @Output()
  onChangeMode: EventEmitter<string> = new EventEmitter();

  listOfStatus: orderStatusType[] = [
    { value: 'PENDING', label: 'En attente' },
    { value: 'CONFIRMED', label: 'Confirmé' },
    { value: 'DELIVERED', label: 'Delivré' },
    { value: 'CANCELLED', label: 'Annulé' },
  ];
  statusFormControl: FormControl<string> = new FormControl('', {
    nonNullable: true,
  });

  ngOnInit(): void {
    if (this.purchaseOrdeDetails)
      this.statusFormControl.setValue(this.purchaseOrdeDetails.status);
  }

  get status(): string {
    return this.purchaseOrdeDetails?.status === 'PENDING'
      ? 'En attente'
      : this.purchaseOrdeDetails?.status === 'CONFIRMED'
      ? 'Confirmé'
      : this.purchaseOrdeDetails?.status === 'DELIVERED'
      ? 'Delivré'
      : 'Annulé';
  }

  updateStatusOfPurchaseOrder(status: PurchaseOrderStatus): void {
    console.log(status);
    if (this.purchaseOrdeDetails) {
      const request: UpdateStatusPurchaseOrderRequest = {
        purchaseOrderId: this.purchaseOrdeDetails.id,
        status: status,
      };
      this.updateStatusOnChange.emit(request);
    }
  }

  generateInvoice(): void {
    this.onChangeMode.emit('invoice');
  }
}
