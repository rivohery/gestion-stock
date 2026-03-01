import { NgClass, NgFor, NgIf } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-pagination',
  imports: [MatIconModule, NgClass, NgFor, NgIf],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css',
})
export class PaginationComponent implements OnInit, OnChanges {
  @Input() totalPages: number = 0;
  @Input() currentPage: number = 0;
  @Output() pageChanged = new EventEmitter<number>();

  pages: number[] = [];

  private buildPages(): void {
    this.pages = new Array(this.totalPages).fill(0).map((_, index) => index);
  }

  ngOnInit(): void {
    this.buildPages();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['totalPages']) {
      this.buildPages();
    }
  }

  goToPage(page: number): void {
    if (page >= 0 && page <= this.totalPages - 1) {
      this.currentPage = page;
      this.pageChanged.emit(this.currentPage);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.goToPage(this.currentPage + 1);
    }
  }

  previousPage(): void {
    if (this.currentPage >= 1) {
      this.goToPage(this.currentPage - 1);
    }
  }
}
