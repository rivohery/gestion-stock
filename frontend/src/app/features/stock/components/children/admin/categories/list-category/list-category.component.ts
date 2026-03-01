import { Component, OnInit, effect, inject } from '@angular/core';
import { injectCategoryStore } from '../../../../../../../core/store/category/category.facade';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Category } from '../../../../../models/product.model';
import { NgIf } from '@angular/common';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { ConfirmAlertDialogComponent } from '../../../../../../../shared/components/confirm-alert-dialog/confirm-alert-dialog.component';

@Component({
  selector: 'app-list-category',
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    NgIf,
  ],
  templateUrl: './list-category.component.html',
  styleUrl: './list-category.component.css',
})
export class ListCategoryComponent implements OnInit {
  categoryStore = injectCategoryStore();
  dialog = inject(MatDialog);
  categories?: Category[];

  constructor() {
    effect(() => {
      if (this.categoryStore.categories()) {
        this.categories = this.categoryStore.categories() || [];
      }
    });
  }

  ngOnInit(): void {
    this.categoryStore.findAll();
  }

  onEdit(category: Category): void {
    if (category && category.id) {
      this.categoryStore.findById(category.id);
    }
  }

  onDelete(category: Category): void {
    if (category && category.id) {
      const dialogConfig = new MatDialogConfig();
      dialogConfig.disableClose = true; // Empêche la fermeture en cliquant à l'extérieur
      dialogConfig.width = '100%';
      dialogConfig.maxWidth = '400px';

      const dialogRef = this.dialog.open(
        ConfirmAlertDialogComponent,
        dialogConfig
      );

      dialogRef.afterClosed().subscribe((confirm) => {
        if (confirm && category.id) {
          this.categoryStore.deleteById(category?.id);
        }
      });
    }
  }
}
