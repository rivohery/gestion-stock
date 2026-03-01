import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { DashboardService } from '../../../../services/dashboard.service';
import { SummaryResponse } from '../../../../models/product.model';
import { LoaderComponent } from '../../../../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../../../../shared/components/message-box/message-box.component';
import { DatePipe, NgClass, NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  imports: [
    LoaderComponent,
    MessageBoxComponent,
    NgIf,
    NgFor,
    NgClass,
    DatePipe,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  dashboardService = inject(DashboardService);

  summary?: SummaryResponse;

  errorMsg = signal<string>('');
  loading = signal<boolean>(false);

  constructor() {
    effect(() => {
      const getDashboardInfoState =
        this.dashboardService.getDashboardInfoState$();
      if (getDashboardInfoState.status === 'OK') {
        this.loading.set(false);
        this.summary = getDashboardInfoState.value;
      }
      if (getDashboardInfoState.status === 'ERROR') {
        this.loading.set(false);
        this.errorMsg.set(getDashboardInfoState.error || '');
      }
    });
  }

  ngOnInit(): void {
    this.loading.set(true);
    this.dashboardService.getDashboardInfo();
  }

  closeMsgBox(): void {
    this.errorMsg.set('');
  }
}
