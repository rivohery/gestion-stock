import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class SnackbarService {
  snackbar = inject(MatSnackBar);

  private readonly defaultConfig: any = {
    horizontalPosition: 'center',
    verticalPosition: 'top',
    duration: 2000,
  };

  openSnackBar(message: string, action: string): void {
    if (action === 'error') {
      this.snackbar.open(message, action, {
        ...this.defaultConfig,
        panelClass: ['bg-black', 'text-white'],
      });
    } else {
      this.snackbar.open(message, action, {
        ...this.defaultConfig,
        panelClass: ['green-snackbar'],
      });
    }
  }
}
