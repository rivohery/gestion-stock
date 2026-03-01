export interface BillOrderItemRequest {
  productId: number;
  productName?: string;
  quantity: number;
  totalItems: number;
}

export interface BillOrderItemResponse {
  itemName: string;
  quantity: number;
  salesPrice: number;
  totalItems: number;
}

export interface BillOrderMinResponse {
  id: number;
  invoiceNo: string;
  customer: string;
  paymentMethod: string;
  total: number;
  createdDate: Date;
}

export interface BillOrderRequest {
  invoiceNo: string;
  customer: string;
  phoneNu?: string;
  email?: string;
  paymentMethod: string;
  total: number;
  items: BillOrderItemRequest[];
}

export interface BillOrderResponse {
  id: number;
  invoiceNo: string;
  customer: string;
  phoneNu?: string;
  email?: string;
  paymentMethod: string;
  total: number;
  createdDate: Date;
  employee: string;
  items: BillOrderItemResponse[];
}
