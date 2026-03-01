import { Component, effect, inject } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { injectCategoryStore } from '../../../../../../../core/store/category/category.facade';
import { NgxControlError } from 'ngxtension/control-error';
import { NgIf } from '@angular/common';
import { Category } from '../../../../../models/product.model';
import { SnackbarService } from '../../../../../../../shared/services/snackbar-service';

@Component({
  selector: 'app-create-category',
  imports: [
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    FormsModule,
    ReactiveFormsModule,
    NgxControlError,
    NgIf,
  ],
  templateUrl: './create-category.component.html',
  styleUrl: './create-category.component.css',
})
export class CreateCategoryComponent {
  fb = inject(FormBuilder);
  snackbar = inject(SnackbarService);
  categoryStore = injectCategoryStore();

  reference: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  name: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  categoryForm: FormGroup<any> = this.fb.nonNullable.group({
    reference: this.reference,
    name: this.name,
  });

  constructor() {
    effect(() => {
      if (this.categoryStore.categoryChecked()) {
        this.categoryForm.setValue({
          reference: this.categoryStore.categoryChecked()?.reference,
          name: this.categoryStore.categoryChecked()?.name,
        });
      }
      if (this.categoryStore.successMsg()) {
        this.categoryForm.reset();
        this.snackbar.openSnackBar(this.categoryStore.successMsg(), 'success');
      }
      if (this.categoryStore.errorMsg()) {
        this.snackbar.openSnackBar(this.categoryStore.errorMsg(), 'error');
      }
    });
  }

  onSubmit(): void {
    if (this.categoryForm.invalid) {
      console.log('Formulaire invalid');
      return;
    }
    const request: Category = {
      ...this.categoryForm.value,
    };
    if (
      this.categoryStore.categoryChecked() &&
      this.categoryStore.categoryChecked()?.id
    ) {
      request.id = this.categoryStore.categoryChecked()?.id;
      this.categoryStore.update(request);
    } else {
      this.categoryStore.create(request);
    }
  }
}
