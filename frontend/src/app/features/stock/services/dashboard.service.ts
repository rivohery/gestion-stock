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
import { SummaryResponse } from '../models/product.model';
import { HttpClient } from '@angular/common/http';
import { catchError, take } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DashboardService extends HandleErrorService {
  dashboardUrl: string = `${environment.server_url}/admin/dashboard`;
  http = inject(HttpClient);

  private getDashboardInfoSignal: WritableSignal<
    State<SummaryResponse, string>
  > = signal(State.builder<SummaryResponse, string>().forInit().build());
  getDashboardInfoState$ = computed(() => this.getDashboardInfoSignal());

  getDashboardInfo(): void {
    this.getDashboardInfoSignal.set(
      State.builder<SummaryResponse, string>().forInit().build()
    );
    this.http
      .get<SummaryResponse>(`${this.dashboardUrl}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) => {
          this.getDashboardInfoSignal.set(
            State.builder<SummaryResponse, string>().forSuccess(resp).build()
          );
        },
        error: (err) => {
          this.getDashboardInfoSignal.set(
            State.builder<SummaryResponse, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }
}
