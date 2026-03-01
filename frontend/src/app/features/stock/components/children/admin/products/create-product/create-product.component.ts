import { Component, OnInit, inject } from '@angular/core';
import { ProductService } from '../../../../../services/product.service';
import { ProductFormComponent } from '../common/product-form/product-form.component';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-create-product',
  imports: [ProductFormComponent, NgIf],
  templateUrl: './create-product.component.html',
  styleUrl: './create-product.component.css',
})
export class CreateProductComponent implements OnInit {
  productService = inject(ProductService);

  ngOnInit(): void {
    this.productService.getCategoryList();
    this.productService.getSupplierList();
  }
}
