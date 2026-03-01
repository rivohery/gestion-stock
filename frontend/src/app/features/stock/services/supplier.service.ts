import {
  Injectable,
  WritableSignal,
  computed,
  inject,
  signal,
} from '@angular/core';
import { HandleErrorService } from '../../../shared/services/handle-error.service';
import { environment } from '../../../../environments/environment.dev';
import { State } from '../../../shared/models/state.model';
import {
  GlobalResponse,
  PageResponse,
} from '../../../shared/models/shared.model';
import { Supplier } from '../models/product.model';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError, take } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SupplierService extends HandleErrorService {
  supplierUrl: string = `${environment.server_url}/admin`;
  http = inject(HttpClient);

  private createSignal: WritableSignal<State<GlobalResponse, string>> = signal(
    State.builder<GlobalResponse, string>().forInit().build()
  );
  createState$ = computed(() => this.createSignal());

  private findAllSignal: WritableSignal<State<PageResponse<Supplier>, string>> =
    signal(State.builder<PageResponse<Supplier>, string>().forInit().build());
  findAllState$ = computed(() => this.findAllSignal());

  private findByIdSignal: WritableSignal<State<Supplier, string>> = signal(
    State.builder<Supplier, string>().forInit().build()
  );
  findByIdState$ = computed(() => this.findByIdSignal());

  private deleteByIdSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  deleteByIdState$ = computed(() => this.deleteByIdSignal());

  private updateSignal: WritableSignal<State<GlobalResponse, string>> = signal(
    State.builder<GlobalResponse, string>().forInit().build()
  );
  updateState$ = computed(() => this.updateSignal());

  initCreateState(): void {
    this.createSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }
  initFindAllState(): void {
    this.findAllSignal.set(
      State.builder<PageResponse<Supplier>, string>().forInit().build()
    );
  }

  initFindByIdState(): void {
    this.findByIdSignal.set(
      State.builder<Supplier, string>().forInit().build()
    );
  }

  initDeleteByIdState(): void {
    this.deleteByIdSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  initUpdateState(): void {
    this.updateSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  create(request: Supplier): void {
    this.initCreateState();
    this.http
      .post<GlobalResponse>(`${this.supplierUrl}/suppliers`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.createSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.createSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  findAll(search: string = '', page: number = 0, size: number = 6): void {
    this.initFindAllState();
    this.http
      .get<PageResponse<Supplier>>(`${this.supplierUrl}/suppliers`, {
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
          this.findAllSignal.set(
            State.builder<PageResponse<Supplier>, string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.findAllSignal.set(
            State.builder<PageResponse<Supplier>, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  findById(supplierId: number): void {
    this.initFindByIdState();
    this.http
      .get<Supplier>(`${this.supplierUrl}/supplier/${supplierId}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.findByIdSignal.set(
            State.builder<Supplier, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.findByIdSignal.set(
            State.builder<Supplier, string>().forError(err.message).build()
          ),
      });
  }

  deleteById(supplierId: number): void {
    this.initDeleteByIdState();
    this.http
      .delete<GlobalResponse>(`${this.supplierUrl}/suppliers/${supplierId}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.deleteByIdSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.deleteByIdSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  update(request: Supplier): void {
    this.initUpdateState();
    this.http
      .put<GlobalResponse>(
        `${this.supplierUrl}/suppliers/${request?.id}`,
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
          this.updateSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.updateSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }
}
