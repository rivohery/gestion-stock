export interface PurchaseOrderItemRequest {
  quantity: number;
  productId: number;
  totalItems: number;
  productName: string;
}

export interface PurchaseOrderRequest {
  invoiceNo: string;
  supplierId: number;
  receiveDate: string;
  totalAmounts: number;
  items: PurchaseOrderItemRequest[];
}

export type PurchaseOrderStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'DELIVERED'
  | 'CANCELLED';

export interface PurchaseOrderItemResponse {
  itemName: string;
  costPrice: number;
  quantity: number;
  totalItems: number;
}

export interface PurchaseOrderResponse {
  id: number;
  invoiceNo: string;
  status: PurchaseOrderStatus;
  receiveDate: Date;
  createdDate: Date;
  totalAmounts: number;
  supplierId: number;
  supplierName: string;
  employee: string;
  items: PurchaseOrderItemResponse[];
}

export interface PurchaseOrderMinResponse {
  id: number;
  invoiceNo: string;
  status: PurchaseOrderStatus;
  receiveDate: Date;
  createdDate: Date;
  totalAmounts: number;
  supplierName: string;
  employee: string;
}

export interface UpdateStatusPurchaseOrderRequest {
  purchaseOrderId: number;
  status: PurchaseOrderStatus;
}
