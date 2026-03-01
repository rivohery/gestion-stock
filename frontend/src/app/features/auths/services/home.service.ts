import {
  Injectable,
  WritableSignal,
  computed,
  inject,
  signal,
} from '@angular/core';
import { HandleErrorService } from '../../../shared/services/handle-error.service';
import { environment } from '../../../../environments/environment.dev';
import { HttpClient } from '@angular/common/http';
import { State } from '../../../shared/models/state.model';
import { ProductResponse } from '../../stock/models/product.model';
import { catchError, take } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class HomeService extends HandleErrorService {
  homeUrl: string = `${environment.server_url}/home`;
  http = inject(HttpClient);

  private fetchListSignal: WritableSignal<State<ProductResponse[], string>> =
    signal(State.builder<ProductResponse[], string>().forInit().build());
  fetchListState$ = computed(() => this.fetchListSignal());

  fetchList(): void {
    this.fetchListSignal.set(
      State.builder<ProductResponse[], string>().forInit().build()
    );
    this.http
      .get<ProductResponse[]>(`${this.homeUrl}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) => {
          this.fetchListSignal.set(
            State.builder<ProductResponse[], string>().forSuccess(resp).build()
          );
        },
        error: (err) => {
          this.fetchListSignal.set(
            State.builder<ProductResponse[], string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }
}
