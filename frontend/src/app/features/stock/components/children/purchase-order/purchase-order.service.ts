import {
  Injectable,
  WritableSignal,
  computed,
  inject,
  signal,
} from '@angular/core';
import { environment } from '../../../../../../environments/environment.dev';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, catchError, take, tap } from 'rxjs';
import { HandleErrorService } from '../../../../../shared/services/handle-error.service';
import {
  PurchaseOrderMinResponse,
  PurchaseOrderRequest,
  PurchaseOrderResponse,
  UpdateStatusPurchaseOrderRequest,
} from './puchase-order.model';
import { State } from '../../../../../shared/models/state.model';
import {
  ProductResponse,
  SupplierMinResponse,
} from '../../../models/product.model';
import {
  GlobalResponse,
  PageResponse,
} from '../../../../../shared/models/shared.model';

@Injectable({
  providedIn: 'root',
})
export class PurchaseOrderService extends HandleErrorService {
  purchaseOrderUrl: string = `${environment.server_url}/stock/purchase-order`;
  http = inject(HttpClient);

  private invoiceNoCheckedSignal: WritableSignal<State<any, string>> = signal(
    State.builder<any, string>().forInit().build()
  );
  invoiceNumberCheckedState$ = computed(() => this.invoiceNoCheckedSignal());

  private createPurchaseOrderSignal: WritableSignal<
    State<PurchaseOrderResponse, string>
  > = signal(State.builder<PurchaseOrderResponse, string>().forInit().build());
  createPurchaseOrderState$ = computed(() => this.createPurchaseOrderSignal());

  private findAllPurchaseOrderSignal: WritableSignal<
    State<PageResponse<PurchaseOrderMinResponse>, string>
  > = signal(
    State.builder<PageResponse<PurchaseOrderMinResponse>, string>()
      .forInit()
      .build()
  );
  findAllPurchaseOrderState$ = computed(() =>
    this.findAllPurchaseOrderSignal()
  );

  private deletePurchaseOrderIdSignal: WritableSignal<
    State<GlobalResponse, string>
  > = signal(State.builder<GlobalResponse, string>().forInit().build());
  deletePurchaseOrderIdState$ = computed(() =>
    this.deletePurchaseOrderIdSignal()
  );

  initGenerateUniqueInvoiceNoState(): void {
    this.invoiceNoCheckedSignal.set(
      State.builder<any, string>().forInit().build()
    );
  }

  generateUniqueInvoiceNo(): void {
    this.initGenerateUniqueInvoiceNoState();
    this.http
      .get(`${this.purchaseOrderUrl}/check-invoice-no`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.invoiceNoCheckedSignal.set(
            State.builder<any, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.invoiceNoCheckedSignal.set(
            State.builder<any, string>().forError(err.message).build()
          ),
      });
  }

  initCreatePurchaseOrderState(): void {
    this.createPurchaseOrderSignal.set(
      State.builder<PurchaseOrderResponse, string>().forInit().build()
    );
  }

  createPurchaseOrder(request: PurchaseOrderRequest): void {
    this.initCreatePurchaseOrderState();
    this.http
      .post<PurchaseOrderResponse>(`${this.purchaseOrderUrl}/create`, request, {
        headers: new HttpHeaders().append('Content-Type', 'application/json'),
      })
      .pipe(
        take(1),
        tap((resp) => console.log(resp)),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.createPurchaseOrderSignal.set(
            State.builder<PurchaseOrderResponse, string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.createPurchaseOrderSignal.set(
            State.builder<PurchaseOrderResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  getSupplierList(): Observable<SupplierMinResponse[]> {
    return this.http
      .get<SupplierMinResponse[]>(`${this.purchaseOrderUrl}/get-supplier-list`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  getProductsBySupplierId(supplierId: number): Observable<ProductResponse[]> {
    return this.http
      .get<ProductResponse[]>(
        `${this.purchaseOrderUrl}/get-product-by-supplierId`,
        {
          params: new HttpParams().append('supplierId', supplierId),
        }
      )
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  findPurchaseOrderById(
    purchaseOrderId: number
  ): Observable<PurchaseOrderResponse> {
    return this.http
      .get<PurchaseOrderResponse>(`${this.purchaseOrderUrl}/${purchaseOrderId}`)
      .pipe(
        take(1),
        tap((resp) => console.log(resp)),
        catchError((err) => this.handleError(err))
      );
  }

  initDeletePurchaseOrderIdState(): void {
    this.deletePurchaseOrderIdSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  deletePurchaseOrderId(purchaseOrderId: number): void {
    this.initDeletePurchaseOrderIdState();
    this.http
      .delete<GlobalResponse>(`${this.purchaseOrderUrl}/${purchaseOrderId}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) => {
          this.deletePurchaseOrderIdSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          );
        },
        error: (err) => {
          this.deletePurchaseOrderIdSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  findAllPurchaseOrder(page: number = 0, size: number = 6): void {
    this.findAllPurchaseOrderSignal.set(
      State.builder<PageResponse<PurchaseOrderMinResponse>, string>()
        .forInit()
        .build()
    );
    this.http
      .get<PageResponse<PurchaseOrderMinResponse>>(`${this.purchaseOrderUrl}`, {
        params: new HttpParams().append('page', page).append('size', size),
      })
      .pipe(
        take(1),
        tap((resp) => console.log(resp)),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.findAllPurchaseOrderSignal.set(
            State.builder<PageResponse<PurchaseOrderMinResponse>, string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.findAllPurchaseOrderSignal.set(
            State.builder<PageResponse<PurchaseOrderMinResponse>, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  updateStatusOfPurchaseOrder(
    request: UpdateStatusPurchaseOrderRequest
  ): Observable<GlobalResponse> {
    return this.http
      .patch<GlobalResponse>(
        `${this.purchaseOrderUrl}/update-status`,
        request,
        {
          headers: new HttpHeaders().set('Content-Type', 'application/json'),
        }
      )
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }
}
