import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { SearchComponent } from '../../../../../../../shared/components/search/search.component';
import { PaginationComponent } from '../../../../../../../shared/components/pagination/pagination.component';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { SupplierService } from '../../../../../services/supplier.service';
import { PageResponse } from '../../../../../../../shared/models/shared.model';
import { Supplier } from '../../../../../models/product.model';
import { Router } from '@angular/router';
import { NgIf } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogModule,
} from '@angular/material/dialog';
import { ConfirmAlertDialogComponent } from '../../../../../../../shared/components/confirm-alert-dialog/confirm-alert-dialog.component';
import { SnackbarService } from '../../../../../../../shared/services/snackbar-service';
import { MessageBoxComponent } from '../../../../../../../shared/components/message-box/message-box.component';

@Component({
  selector: 'app-supplier-list',
  imports: [
    SearchComponent,
    PaginationComponent,
    MessageBoxComponent,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    NgIf,
  ],
  templateUrl: './supplier-list.component.html',
  styleUrl: './supplier-list.component.css',
})
export class SupplierListComponent implements OnInit {
  supplierService = inject(SupplierService);
  router = inject(Router);
  dialog = inject(MatDialog);
  snackbar = inject(SnackbarService);

  search: string = '';
  currentPage: number = 0;
  size: number = 6;

  loading = signal<boolean>(false);
  errorMsg = signal<string>('');
  suppliers: Supplier[] = [];
  totalPages: number = 0;

  deletingId: number = -1;

  constructor() {
    effect(() => {
      if (this.supplierService.findAllState$().status === 'OK') {
        this.loading.set(false);
        this.suppliers =
          this.supplierService.findAllState$().value?.content || [];
        this.totalPages =
          this.supplierService.findAllState$().value?.totalPages || 0;
      }
      if (this.supplierService.findAllState$().status === 'ERROR') {
        this.loading.set(false);
        this.errorMsg.set(this.supplierService.findAllState$().error || '');
      }
    });

    effect(() => {
      if (this.supplierService.deleteByIdState$().status === 'OK') {
        this.deletingId = -1;
        const successMsg =
          this.supplierService.deleteByIdState$().value?.message || '';
        this.snackbar.openSnackBar(successMsg, 'success');
        this.currentPage = 0;
        this.loadSuppliers();
      }
      if (this.supplierService.deleteByIdState$().status === 'OK') {
        this.deletingId = -1;
        const errorMsg = this.supplierService.deleteByIdState$().error || '';
      }
    });
  }

  private loadSuppliers(): void {
    this.supplierService.findAll(this.search, this.currentPage, this.size);
  }

  ngOnInit(): void {
    this.loading.set(true);
    this.loadSuppliers();
  }

  doSearch(value: string): void {
    console.log(value);
    this.search = value;
    this.currentPage = 0;
    this.loadSuppliers();
  }

  initAllState(): void {
    this.supplierService.initCreateState();
    this.supplierService.initUpdateState();
    this.supplierService.initFindByIdState();
    this.supplierService.initDeleteByIdState();
  }

  addSupplier(): void {
    this.initAllState();
    this.router.navigateByUrl('/stock/admin/supplier');
  }

  onEdit(supplier: Supplier): void {
    if (supplier?.id) {
      this.initAllState();
      this.supplierService.findById(supplier?.id);
      this.router.navigateByUrl('/stock/admin/supplier');
    }
  }

  onDelete(supplier: Supplier): void {
    this.supplierService.initDeleteByIdState();
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true; // Empêche la fermeture en cliquant à l'extérieur
    dialogConfig.width = '100%';
    dialogConfig.maxWidth = '400px';

    const dialogRef = this.dialog.open(
      ConfirmAlertDialogComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe((confirm) => {
      if (confirm && supplier?.id) {
        this.deletingId = supplier?.id;
        this.supplierService.deleteById(supplier?.id);
      }
    });
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadSuppliers();
  }

  close(): void {
    this.errorMsg.set('');
  }
}
