import { Component, EventEmitter, Output } from '@angular/core';
import {
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { debounceTime } from 'rxjs';

@Component({
  selector: 'app-search',
  imports: [MatCardModule, MatInputModule, FormsModule, ReactiveFormsModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css',
})
export class SearchComponent {
  @Output()
  searchEvent: EventEmitter<string> = new EventEmitter();

  searchControl: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  doSearch(): void {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(3000) // Attente de 3 secondes sans nouvelle saisie
      )
      .subscribe({
        next: (value) => this.searchEvent.emit(value),
      });
  }
}
