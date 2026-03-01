import { Component, OnInit } from '@angular/core';
import { CreateCategoryComponent } from './create-category/create-category.component';
import { ListCategoryComponent } from './list-category/list-category.component';

@Component({
  selector: 'app-categories',
  imports: [CreateCategoryComponent, ListCategoryComponent],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.css',
})
export class CategoriesComponent {}
