import {
  Injectable,
  WritableSignal,
  computed,
  inject,
  signal,
} from '@angular/core';
import { HandleErrorService } from '../../../../../shared/services/handle-error.service';
import { environment } from '../../../../../../environments/environment.dev';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { State } from '../../../../../shared/models/state.model';
import {
  BillOrderMinResponse,
  BillOrderRequest,
  BillOrderResponse,
} from './bill-order.model';
import { Observable, catchError, take, tap } from 'rxjs';
import {
  GlobalResponse,
  PageResponse,
} from '../../../../../shared/models/shared.model';
import {
  CategoryMinResponse,
  ProductResponse,
} from '../../../models/product.model';

@Injectable({
  providedIn: 'root',
})
export class BillOrderService extends HandleErrorService {
  billOrderUrl: string = `${environment.server_url}/sales/bill-order`;
  http = inject(HttpClient);

  private createBillOrderSignal: WritableSignal<
    State<BillOrderResponse, string>
  > = signal(State.builder<BillOrderResponse, string>().forInit().build());
  createBillOrderState$ = computed(() => this.createBillOrderSignal());

  private findAllOrderSignal: WritableSignal<
    State<PageResponse<BillOrderMinResponse>, string>
  > = signal(
    State.builder<PageResponse<BillOrderMinResponse>, string>()
      .forInit()
      .build()
  );
  findAllOrderState$ = computed(() => this.findAllOrderSignal());

  private findBillOrderByIdSignal: WritableSignal<
    State<BillOrderResponse, string>
  > = signal(State.builder<BillOrderResponse, string>().forInit().build());
  findBillOrderByIdState$ = computed(() => this.findBillOrderByIdSignal());

  checkUniqueInvoiceNo(): Observable<any> {
    return this.http.get(`${this.billOrderUrl}/check-unique-invoiceNo`).pipe(
      take(1),
      catchError((err) => this.handleError(err))
    );
  }

  initCreateBillOrderState(): void {
    this.createBillOrderSignal.set(
      State.builder<BillOrderResponse, string>().forInit().build()
    );
  }

  createBillOrder(request: BillOrderRequest): void {
    this.initCreateBillOrderState();
    this.http
      .post<BillOrderResponse>(`${this.billOrderUrl}`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        take(1),
        tap((resp) => console.log(resp)),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) => {
          this.createBillOrderSignal.set(
            State.builder<BillOrderResponse, string>().forSuccess(resp).build()
          );
        },
        error: (err) => {
          this.createBillOrderSignal.set(
            State.builder<BillOrderResponse, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  initFindAllOrderState(): void {
    this.findAllOrderSignal.set(
      State.builder<PageResponse<BillOrderMinResponse>, string>()
        .forInit()
        .build()
    );
  }

  findAllOrder(page: number = 0, size: number = 6): void {
    this.initFindAllOrderState();
    this.http
      .get<PageResponse<BillOrderMinResponse>>(`${this.billOrderUrl}`, {
        params: new HttpParams().append('page', page).append('size', size),
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) => {
          this.findAllOrderSignal.set(
            State.builder<PageResponse<BillOrderMinResponse>, string>()
              .forSuccess(resp)
              .build()
          );
        },
        error: (err) => {
          this.findAllOrderSignal.set(
            State.builder<PageResponse<BillOrderMinResponse>, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  findAllCategory(): Observable<CategoryMinResponse[]> {
    return this.http
      .get<CategoryMinResponse[]>(`${this.billOrderUrl}/get-category-list`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  findAllProductByCategoryId(
    categoryId: number
  ): Observable<ProductResponse[]> {
    return this.http
      .get<ProductResponse[]>(
        `${this.billOrderUrl}/get-all-product-by-category`,
        {
          params: new HttpParams().append('categoryId', categoryId),
        }
      )
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  findBillOrderById(billOrderId: number): void {
    this.findBillOrderByIdSignal.set(
      State.builder<BillOrderResponse, string>().forInit().build()
    );
    this.http
      .get<BillOrderResponse>(`${this.billOrderUrl}/${billOrderId}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) => {
          this.findBillOrderByIdSignal.set(
            State.builder<BillOrderResponse, string>().forSuccess(resp).build()
          );
        },
        error: (err) => {
          this.findBillOrderByIdSignal.set(
            State.builder<BillOrderResponse, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  getInvoicePdf(invoiceNo: string): Observable<Blob> {
    return this.http.get(`${this.billOrderUrl}/get-invoice-pdf/${invoiceNo}`, {
      responseType: 'blob',
    });
  }

  deleteBillOrderById(billOrderId: number): Observable<GlobalResponse> {
    return this.http
      .delete<GlobalResponse>(`${this.billOrderUrl}/${billOrderId}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }
}
