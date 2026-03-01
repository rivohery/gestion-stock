import { Component, OnInit, inject } from '@angular/core';
import { ProductService } from '../../../../../services/product.service';
import { ActivatedRoute } from '@angular/router';
import { ProductResponse } from '../../../../../models/product.model';
import { ProductFormComponent } from '../common/product-form/product-form.component';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-edit-product',
  imports: [ProductFormComponent, NgIf],
  templateUrl: './edit-product.component.html',
  styleUrl: './edit-product.component.css',
})
export class EditProductComponent implements OnInit {
  productService = inject(ProductService);
  activateRoute = inject(ActivatedRoute);

  editProduct?: ProductResponse;

  ngOnInit(): void {
    const productId: number =
      Number(this.activateRoute.snapshot.paramMap.get('productId')) || -1;
    this.productService.getCategoryList();
    this.productService.getSupplierList();
    if (productId !== -1) {
      this.productService.findProductById(productId).subscribe({
        next: (resp) => (this.editProduct = resp),
        error: (err) => console.log(err.message),
      });
    }
  }
}
