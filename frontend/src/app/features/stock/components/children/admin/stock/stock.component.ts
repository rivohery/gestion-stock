import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { ProductService } from '../../../../services/product.service';
import {
  StockResponse,
  UpdateProductStatusRequest,
} from '../../../../models/product.model';
import { SearchComponent } from '../../../../../../shared/components/search/search.component';
import { PaginationComponent } from '../../../../../../shared/components/pagination/pagination.component';
import { MatIconModule } from '@angular/material/icon';
import { NgClass, NgIf } from '@angular/common';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { SnackbarService } from '../../../../../../shared/services/snackbar-service';
import { MessageBoxComponent } from '../../../../../../shared/components/message-box/message-box.component';
import { LoaderComponent } from '../../../../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-stock',
  imports: [
    MatIconModule,
    MatSlideToggleModule,
    SearchComponent,
    PaginationComponent,
    MessageBoxComponent,
    LoaderComponent,
    NgIf,
    NgClass,
  ],
  templateUrl: './stock.component.html',
  styleUrl: './stock.component.css',
})
export class StockComponent implements OnInit {
  productService = inject(ProductService);
  snackbar = inject(SnackbarService);
  loading = signal<boolean>(false);
  errorMsg: string = '';
  stocks: StockResponse[] = [];
  totalPages: number = 0;

  currentPage: number = 0;
  search: string = '';
  size: number = 6;

  constructor() {
    effect(() => {
      if (this.productService.getStockListState$().status === 'OK') {
        this.loading.set(false);
        this.stocks =
          this.productService.getStockListState$().value?.content || [];
        this.totalPages =
          this.productService.getStockListState$().value?.totalPages || 0;
      }
      if (this.productService.getStockListState$().status === 'ERROR') {
        this.loading.set(false);
        this.errorMsg = this.productService.getStockListState$().error || '';
      }
    });
  }

  ngOnInit(): void {
    this.loading.set(true);
    this.loadAllStock();
  }

  private loadAllStock(): void {
    this.productService.findAllStock(this.search, this.currentPage, this.size);
  }

  doSearch(value: string): void {
    this.search = value;
    this.currentPage = 0;
    this.loadAllStock();
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadAllStock();
  }

  close(): void {
    this.errorMsg = '';
  }

  changeStatusStock(value: boolean, stock: StockResponse): void {
    const request: UpdateProductStatusRequest = {
      productId: stock.id,
      status: value,
    };
    console.log(request);
    this.productService.updateStatusStock(request).subscribe({
      next: (resp) => {
        this.snackbar.openSnackBar(resp.message || '', 'success');
        this.loadAllStock();
      },
      error: (err) => {
        this.errorMsg = err.message || '';
      },
    });
  }
}
