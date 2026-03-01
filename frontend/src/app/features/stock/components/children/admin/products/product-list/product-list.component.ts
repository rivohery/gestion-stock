import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { ProductService } from '../../../../../services/product.service';
import { SearchComponent } from '../../../../../../../shared/components/search/search.component';
import { ProductResponse } from '../../../../../models/product.model';
import { SnackbarService } from '../../../../../../../shared/services/snackbar-service';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NgIf } from '@angular/common';
import { PaginationComponent } from '../../../../../../../shared/components/pagination/pagination.component';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogModule,
} from '@angular/material/dialog';
import { ConfirmAlertDialogComponent } from '../../../../../../../shared/components/confirm-alert-dialog/confirm-alert-dialog.component';
import { MessageBoxComponent } from '../../../../../../../shared/components/message-box/message-box.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product-list',
  imports: [
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    SearchComponent,
    PaginationComponent,
    MessageBoxComponent,
    NgIf,
  ],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css',
})
export class ProductListComponent implements OnInit {
  productService = inject(ProductService);
  snackbar = inject(SnackbarService);
  dialog = inject(MatDialog);
  router = inject(Router);

  search: string = '';
  currentPage: number = 0;
  size: number = 6;

  loading = signal<boolean>(false);
  errorMsg: string = '';
  deletingId: number = -1;

  productList: ProductResponse[] = [];
  totalPages: number = 0;

  constructor() {
    effect(() => {
      if (this.productService.findAllProductState$().status === 'OK') {
        this.loading.set(false);
        this.productList =
          this.productService.findAllProductState$().value?.content || [];
        this.totalPages =
          this.productService.findAllProductState$().value?.totalPages || 0;
      }
      if (this.productService.findAllProductState$().status === 'ERROR') {
        this.loading.set(false);
        this.errorMsg = this.productService.findAllProductState$().error || '';
      }
    });
  }

  ngOnInit(): void {
    this.loading.set(true);
    this.loadAllProduct();
  }

  loadAllProduct(): void {
    this.productService.findAllProduct(
      this.search,
      this.currentPage,
      this.size
    );
  }

  doSearch(value: string): void {
    this.search = value;
    this.currentPage = 0;
    this.loadAllProduct();
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadAllProduct();
  }

  onCreate(): void {
    this.router.navigateByUrl('/stock/admin/new-product');
  }

  onEdit(product: ProductResponse): void {
    console.log(product);
    if (product) {
      this.router.navigateByUrl('/stock/admin/edit-product/' + product.id);
    }
  }

  onDelete(product: ProductResponse): void {
    if (product && product.id) {
      const dialogConfig = new MatDialogConfig();
      dialogConfig.disableClose = true; // Empêche la fermeture en cliquant à l'extérieur
      dialogConfig.width = '100%';
      dialogConfig.maxWidth = '400px';

      const dialogRef = this.dialog.open(
        ConfirmAlertDialogComponent,
        dialogConfig
      );

      dialogRef.afterClosed().subscribe((confirm) => {
        if (confirm && product.id) {
          this.deletingId = product.id;
          this.productService.deleteProductById(product.id).subscribe({
            next: (resp) => {
              this.snackbar.openSnackBar(resp.message || '', 'success');
              this.currentPage = 0;
              this.deletingId = -1;
              this.loadAllProduct();
            },
            error: (err) => {
              this.errorMsg = err.message || '';
              this.deletingId = -1;
            },
          });
        }
      });
    }
  }

  close(): void {
    this.errorMsg = '';
  }
}
