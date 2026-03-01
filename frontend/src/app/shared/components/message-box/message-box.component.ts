import { NgClass, NgIf } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-message-box',
  imports: [NgClass, NgIf, MatIconModule],
  templateUrl: './message-box.component.html',
  styleUrl: './message-box.component.css',
})
export class MessageBoxComponent {
  @Input()
  type: 'success' | 'error' = 'success';
  @Input()
  title?: string = '';
  @Input()
  message: string = '';
  @Output()
  closeEvent = new EventEmitter<void>();

  close(): void {
    this.closeEvent.emit();
  }

  get icon(): string {
    return this.type === 'success' ? 'check_circle' : 'error';
  }
}
