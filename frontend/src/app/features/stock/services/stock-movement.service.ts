import { HttpClient, HttpParams } from '@angular/common/http';
import {
  Injectable,
  WritableSignal,
  computed,
  inject,
  signal,
} from '@angular/core';
import { environment } from '../../../../environments/environment.dev';
import { State } from '../../../shared/models/state.model';
import { PageResponse } from '../../../shared/models/shared.model';
import { StockMovement } from '../models/product.model';
import { Observable, catchError, take, tap } from 'rxjs';
import { HandleErrorService } from '../../../shared/services/handle-error.service';

@Injectable({
  providedIn: 'root',
})
export class StockMovementService extends HandleErrorService {
  http = inject(HttpClient);
  stockMovementUrl: string = `${environment.server_url}/admin/movement-stock`;

  private findAllStockMovementSignal: WritableSignal<
    State<PageResponse<StockMovement>, string>
  > = signal(
    State.builder<PageResponse<StockMovement>, string>().forInit().build()
  );
  findAllStockMovementState$ = computed(() =>
    this.findAllStockMovementSignal()
  );

  initFindAllStockMovementState(): void {
    this.findAllStockMovementSignal.set(
      State.builder<PageResponse<StockMovement>, string>().forInit().build()
    );
  }

  findAllStockMovement(
    createdDate: string = '',
    page: number = 0,
    size: number = 6
  ): void {
    this.initFindAllStockMovementState();
    this.http
      .get<PageResponse<StockMovement>>(`${this.stockMovementUrl}`, {
        params: new HttpParams()
          .append('createdDate', createdDate)
          .append('page', page)
          .append('size', size),
      })
      .pipe(
        take(1),
        tap((resp) => console.log(resp)),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) => {
          this.findAllStockMovementSignal.set(
            State.builder<PageResponse<StockMovement>, string>()
              .forSuccess(resp)
              .build()
          );
        },
        error: (err) => {
          this.findAllStockMovementSignal.set(
            State.builder<PageResponse<StockMovement>, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  exportPdf(createdDate: string = ''): Observable<Blob> {
    return this.http.get(`${this.stockMovementUrl}/export/pdf`, {
      params: new HttpParams().append('createdDate', createdDate),
      responseType: 'blob',
    });
  }

  exportExcel(createdDate: string = ''): Observable<Blob> {
    return this.http.get(`${this.stockMovementUrl}/export/excel`, {
      params: new HttpParams().append('createdDate', createdDate),
      responseType: 'blob',
    });
  }

  exportCsv(createdDate: string = ''): Observable<Blob> {
    return this.http.get(`${this.stockMovementUrl}/export/csv`, {
      params: new HttpParams().append('createdDate', createdDate),
      responseType: 'blob',
    });
  }
}
