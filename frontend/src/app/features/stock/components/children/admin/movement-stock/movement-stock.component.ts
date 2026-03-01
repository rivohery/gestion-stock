import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { StockMovementService } from '../../../../services/stock-movement.service';
import { StockMovement } from '../../../../models/product.model';
import { LoaderComponent } from '../../../../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../../../../shared/components/message-box/message-box.component';
import { PaginationComponent } from '../../../../../../shared/components/pagination/pagination.component';
import { DatePipe, NgClass, NgFor, NgIf } from '@angular/common';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { provideNativeDateAdapter } from '@angular/material/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-movement-stock',
  providers: [provideNativeDateAdapter()],
  imports: [
    FormsModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    LoaderComponent,
    MessageBoxComponent,
    PaginationComponent,
    NgIf,
    NgFor,
    NgClass,
    DatePipe,
  ],
  templateUrl: './movement-stock.component.html',
  styleUrl: './movement-stock.component.css',
})
export class MovementStockComponent implements OnInit {
  stockMovementService = inject(StockMovementService);

  currentDateSearch: string = '';
  currentPage: number = 0;
  totalPages: number = 0;
  stockMovementList: StockMovement[] = [];

  loadingPage = signal<boolean>(false);
  errorMsg = signal<string>('');

  loadingPdfFile = signal<boolean>(false);
  loadingExcelFile = signal<boolean>(false);
  loadingCsvFile = signal<boolean>(false);

  constructor() {
    effect(() => {
      const findAllMovementStockState =
        this.stockMovementService.findAllStockMovementState$();
      if (findAllMovementStockState.status === 'OK') {
        this.loadingPage.set(false);
        this.stockMovementList = findAllMovementStockState.value?.content || [];
        this.totalPages = findAllMovementStockState.value?.totalPages || 0;
      }
      if (findAllMovementStockState.status === 'ERROR') {
        this.loadingPage.set(false);
        this.errorMsg.set(findAllMovementStockState.error || '');
      }
    });
  }

  ngOnInit(): void {
    this.loadingPage.set(true);
    this.findAllMovementStock();
  }

  private findAllMovementStock(): void {
    this.stockMovementService.findAllStockMovement(
      this.currentDateSearch,
      this.currentPage
    );
  }

  closeMessageBox(): void {
    this.errorMsg.set('');
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.findAllMovementStock();
  }

  onDateChange(event: any) {
    const selectedDate = event.value;
    console.log(selectedDate);
    if (!selectedDate) {
      this.currentDateSearch = '';
    } else {
      // Conversion manuelle au format YYYY-MM-DD pour eviter le décalage de fuseau horaire
      // On utilise le format 'en-CA' (Canada) qui génère nativement du YYYY-MM-DD
      this.currentDateSearch = new Intl.DateTimeFormat('en-CA').format(
        selectedDate
      );
    }
    this.currentPage = 0;
    this.findAllMovementStock();
  }

  exportToPDF() {
    console.log('Exportation en cours vers PDF...');
    this.loadingPdfFile.set(true);
    this.stockMovementService.exportPdf(this.currentDateSearch).subscribe({
      next: (blob) => {
        if (typeof window !== 'undefined') {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'stock-movement.pdf';
          a.click();
          window.URL.revokeObjectURL(url);
        }
      },
      error: (err) => console.log(err),
      complete: () => this.loadingPdfFile.set(false),
    });
  }

  exportToExcel() {
    console.log('Exportation en cours vers Excel...');
    this.loadingExcelFile.set(true);
    this.stockMovementService.exportExcel(this.currentDateSearch).subscribe({
      next: (blob) => {
        if (typeof window !== 'undefined') {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'stock-movement.xlsx';
          a.click();
          window.URL.revokeObjectURL(url);
        }
      },
      error: (err) => console.log(err),
      complete: () => this.loadingExcelFile.set(false),
    });
  }

  exportToCsv() {
    console.log('Exportation en cours vers csv...');
    this.loadingCsvFile.set(true);
    this.stockMovementService.exportCsv(this.currentDateSearch).subscribe({
      next: (blob) => {
        if (typeof window !== 'undefined') {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'stock-movement.csv';
          a.click();
          window.URL.revokeObjectURL(url);
        }
      },
      error: (err) => console.log(err),
      complete: () => this.loadingCsvFile.set(false),
    });
  }
}
