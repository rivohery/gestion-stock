import { Component, EventEmitter, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-confirm-alert-dialog',
  imports: [MatDialogModule, MatButtonModule, MatIconModule],
  templateUrl: './confirm-alert-dialog.component.html',
  styleUrl: './confirm-alert-dialog.component.css',
})
export class ConfirmAlertDialogComponent {
  @Output()
  onConfirmEvent = new EventEmitter<boolean>();
  constructor(public dialogRef: MatDialogRef<ConfirmAlertDialogComponent>) {}

  onConfirm(): void {
    this.onConfirmEvent.emit(true);
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
