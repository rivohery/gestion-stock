import {
  Injectable,
  WritableSignal,
  computed,
  inject,
  signal,
} from '@angular/core';
import { environment } from '../../../../environments/environment.dev';
import { State } from '../../../shared/models/state.model';
import {
  GlobalResponse,
  PageResponse,
} from '../../../shared/models/shared.model';
import {
  CategoryMinResponse,
  ProductRequest,
  ProductResponse,
  StockResponse,
  SupplierMinResponse,
  UpdateProductStatusRequest,
} from '../models/product.model';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, catchError, take } from 'rxjs';
import { HandleErrorService } from '../../../shared/services/handle-error.service';

@Injectable({
  providedIn: 'root',
})
export class ProductService extends HandleErrorService {
  productUrl: string = `${environment.server_url}/admin`;
  http = inject(HttpClient);

  private findAllProductSignal: WritableSignal<
    State<PageResponse<ProductResponse>, string>
  > = signal(
    State.builder<PageResponse<ProductResponse>, string>().forInit().build()
  );
  findAllProductState$ = computed(() => this.findAllProductSignal());

  private createProductSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  createProductState$ = computed(() => this.createProductSignal());

  private updateProductSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  updateProductState$ = computed(() => this.updateProductSignal());

  private getCategoryListSignal: WritableSignal<
    State<CategoryMinResponse[], string>
  > = signal(State.builder<CategoryMinResponse[], string>().forInit().build());
  gatCategoryListState$ = computed(() => this.getCategoryListSignal());

  private getSupplierListSignal: WritableSignal<
    State<SupplierMinResponse[], string>
  > = signal(State.builder<SupplierMinResponse[], string>().forInit().build());
  getSupplierListState$ = computed(() => this.getSupplierListSignal());

  private getStockListSignal: WritableSignal<
    State<PageResponse<StockResponse>, string>
  > = signal(
    State.builder<PageResponse<StockResponse>, string>().forInit().build()
  );
  getStockListState$ = computed(() => this.getStockListSignal());

  initFindAllProductState(): void {
    this.findAllProductSignal.set(
      State.builder<PageResponse<ProductResponse>, string>().forInit().build()
    );
  }

  initCreateProductState(): void {
    this.createProductSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  initUpdateProductState(): void {
    this.updateProductSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  findAllProduct(
    search: string = '',
    page: number = 0,
    size: number = 6
  ): void {
    this.initFindAllProductState();
    this.http
      .get<PageResponse<ProductResponse>>(`${this.productUrl}/products`, {
        params: new HttpParams()
          .append('search', search)
          .append('page', page)
          .append('size', size),
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.findAllProductSignal.set(
            State.builder<PageResponse<ProductResponse>, string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.findAllProductSignal.set(
            State.builder<PageResponse<ProductResponse>, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  createProduct(request: ProductRequest): void {
    this.initCreateProductState();
    this.http
      .post<GlobalResponse>(`${this.productUrl}/products`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.createProductSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.createProductSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  findProductById(productId: number): Observable<ProductResponse> {
    return this.http
      .get<ProductResponse>(`${this.productUrl}/product/${productId}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  deleteProductById(productId: number): Observable<GlobalResponse> {
    return this.http
      .delete<GlobalResponse>(`${this.productUrl}/products/${productId}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  updateProduct(request: ProductRequest): void {
    this.initUpdateProductState();
    this.http
      .put<GlobalResponse>(
        `${this.productUrl}/products/${request.id}`,
        request,
        {
          headers: new HttpHeaders().set('Content-Type', 'application/json'),
        }
      )
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.updateProductSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.updateProductSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  getCategoryList(): void {
    this.http
      .get<CategoryMinResponse[]>(
        `${this.productUrl}/products/find-all-category`
      )
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.getCategoryListSignal.set(
            State.builder<CategoryMinResponse[], string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.getCategoryListSignal.set(
            State.builder<CategoryMinResponse[], string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  getSupplierList(): void {
    this.http
      .get<SupplierMinResponse[]>(
        `${this.productUrl}/products/find-all-supplier`
      )
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.getSupplierListSignal.set(
            State.builder<SupplierMinResponse[], string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.getSupplierListSignal.set(
            State.builder<SupplierMinResponse[], string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  uploadPhoto(data: FormData): Observable<GlobalResponse> {
    return this.http
      .post<GlobalResponse>(`${this.productUrl}/products/upload-photo`, data, {
        headers: new HttpHeaders().set('Content-Type', 'multipart/form-data'),
        responseType: 'json',
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  findAllStock(search: string = '', page: number = 0, size: number = 6): void {
    this.http
      .get<PageResponse<StockResponse>>(
        `${this.productUrl}/products/find-all-stock`,
        {
          params: new HttpParams()
            .append('search', search)
            .append('page', page)
            .append('size', size),
        }
      )
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) => {
          this.getStockListSignal.set(
            State.builder<PageResponse<StockResponse>, string>()
              .forSuccess(resp)
              .build()
          );
        },
        error: (err) => {
          this.getStockListSignal.set(
            State.builder<PageResponse<StockResponse>, string>()
              .forError(err)
              .build()
          );
        },
      });
  }

  updateStatusStock(
    request: UpdateProductStatusRequest
  ): Observable<GlobalResponse> {
    return this.http
      .patch<GlobalResponse>(
        `${this.productUrl}/products/update-status`,
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
