export interface Category {
  id?: number;
  reference: string;
  name: string;
}

export interface CategoryMinResponse {
  id: number;
  name: string;
}

export interface Supplier {
  id?: number;
  name: string;
  email: string;
  phoneNu: string;
  address: string;
  createdAt?: Date;
}

export interface SupplierMinResponse {
  id: number;
  name: string;
}

export interface ProductRequest {
  id: number;
  code: string;
  name: string;
  salesPrice: number;
  costPrice: number;
  alertStock: number;
  unity: string;
  categoryId: number;
  supplierId: number;
}

export interface ProductResponse {
  id: number;
  code: string;
  name: string;
  salesPrice: number;
  costPrice: number;
  alertStock: number;
  qtyStock: number;
  unity: string;
  isActive: boolean;
  photos: string;
  categoryId: number;
  categoryName: string;
  supplierId: number;
  supplierName: string;
}

export interface StockResponse {
  id: number;
  name: string;
  alert: 'OK' | 'ALERT';
  qtyStock: number;
  isActive: boolean;
  photos: string;
}

export interface UpdateProductStatusRequest {
  productId: number;
  status: boolean;
}

export type TypeMovement = 'IN' | 'OUT' | 'ADJUSTMENT';

export interface StockMovement {
  id: number;
  quantity: number;
  type: TypeMovement;
  reference: string;
  productId: number;
  productName: string;
  createdDate: Date;
  employeId: number;
  employeName: string;
}

export interface SummaryResponse {
  nbrProductActive: number;
  nbrProductNoActive: number;
  nbrProductEnAlert: number;
  nbrSupplier: number;
  lastMovementStock: StockMovement[];
}
