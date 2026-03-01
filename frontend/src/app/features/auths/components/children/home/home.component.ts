import { CurrencyPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ProductResponse } from '../../../../stock/models/product.model';
import { HomeService } from '../../../services/home.service';
import { LoaderComponent } from '../../../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../../../shared/components/message-box/message-box.component';

@Component({
  selector: 'app-home',
  imports: [
    LoaderComponent,
    MessageBoxComponent,
    FormsModule,
    MatIconModule,
    NgIf,
    NgFor,
    NgClass,
    CurrencyPipe,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  products?: ProductResponse[];
  homeService = inject(HomeService);

  filteredProducts: ProductResponse[] = [];
  searchTerm: string = '';
  statusFilter: string = 'all'; // all, available, outOfStock

  // Pagination simple
  currentPage: number = 1;
  itemsPerPage: number = 8;

  loading = signal<boolean>(false);
  errorMsg = signal<string>('');

  constructor() {
    effect(() => {
      const fetchListState = this.homeService.fetchListState$();
      if (fetchListState.status === 'OK') {
        this.loading.set(false);
        this.products = fetchListState.value;
        this.applyFilters();
      }
      if (fetchListState.status === 'ERROR') {
        this.loading.set(false);
        this.errorMsg.set(fetchListState.error || '');
      }
    });
  }

  ngOnInit() {
    this.loading.set(true);
    this.homeService.fetchList();
  }

  applyFilters() {
    if (this.products) {
      this.filteredProducts = this.products.filter((p) => {
        const matchesSearch = p.name
          .toLowerCase()
          .includes(this.searchTerm.toLowerCase());
        const matchesStatus =
          this.statusFilter === 'all'
            ? p.id !== -1
            : this.statusFilter === 'available'
            ? p.isActive === true
            : p.isActive === false;
        return matchesSearch && matchesStatus;
      });
      this.currentPage = 1; // Reset pagination au filtrage
    }
  }

  get paginatedProducts() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredProducts.slice(start, start + this.itemsPerPage);
  }

  get totalPages() {
    return Math.ceil(this.filteredProducts.length / this.itemsPerPage);
  }

  close(): void {
    this.errorMsg.set('');
  }
}
