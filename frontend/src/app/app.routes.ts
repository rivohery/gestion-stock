import { Routes } from '@angular/router';
import { MainComponent } from './features/auths/components/wrapper/main/main.component';
import { LoginComponent } from './features/auths/components/children/login/login.component';
import { HomeComponent } from './features/auths/components/children/home/home.component';
import { ResetPasswordComponent } from './features/auths/components/children/reset-password/reset-password.component';
import { ActivateAccountComponent } from './features/auths/components/children/activate-account/activate-account.component';
import { ForgotPasswordComponent } from './features/auths/components/children/forgot-password/forgot-password.component';
import { StockMainComponent } from './features/stock/components/wrapper/stock-main/stock-main.component';
import { DashboardComponent } from './features/stock/components/children/admin/dashboard/dashboard.component';
import { CategoriesComponent } from './features/stock/components/children/admin/categories/categories.component';
import { ProfileComponent } from './features/stock/components/children/user/profile/profile.component';
import { UserListComponent } from './features/stock/components/children/user/user-list/user-list.component';
import { provideState } from '@ngrx/store';
import { categoryFeature } from './core/store/category/category.reducer';
import { provideEffects } from '@ngrx/effects';
import {
  create$,
  deleteById$,
  findAll$,
  findById$,
  update$,
} from './core/store/category/category.effect';
import { SupplierListComponent } from './features/stock/components/children/admin/supplier/supplier-list/supplier-list.component';
import { SupplierCreateComponent } from './features/stock/components/children/admin/supplier/supplier-create/supplier-create.component';
import { ProductListComponent } from './features/stock/components/children/admin/products/product-list/product-list.component';
import { CreateProductComponent } from './features/stock/components/children/admin/products/create-product/create-product.component';
import { EditProductComponent } from './features/stock/components/children/admin/products/edit-product/edit-product.component';
import { CreatePurchaseComponent } from './features/stock/components/children/purchase-order/create-purchase/create-purchase.component';
import { PurchaseOrderListComponent } from './features/stock/components/children/purchase-order/purchase-order-list/purchase-order-list.component';
import { PurchaseOrderDetailsComponent } from './features/stock/components/children/purchase-order/purchase-order-details/purchase-order-details.component';
import { StockComponent } from './features/stock/components/children/admin/stock/stock.component';
import { NewBillOrderComponent } from './features/stock/components/children/bill-order/new-bill-order/new-bill-order.component';
import { BillOrderListComponent } from './features/stock/components/children/bill-order/bill-order-list/bill-order-list.component';
import { BillOrderDetailsComponent } from './features/stock/components/children/bill-order/bill-order-details/bill-order-details.component';
import { MovementStockComponent } from './features/stock/components/children/admin/movement-stock/movement-stock.component';
import { adminGuard } from './core/guards/admin.guard';
import { viewerGuard } from './core/guards/viewer.guard';
import { stockManagerGuard } from './core/guards/stock-manager.guard';
import { salesManagerGuard } from './core/guards/sales-manager.guard';

export const routes: Routes = [
  {
    path: '',
    component: MainComponent,
    children: [
      {
        path: 'home',
        component: HomeComponent,
      },
      {
        path: 'login',
        component: LoginComponent,
      },
      {
        path: 'reset-password',
        component: ResetPasswordComponent,
      },
      {
        path: 'forgot-password',
        component: ForgotPasswordComponent,
      },
      {
        path: 'activate-account',
        component: ActivateAccountComponent,
      },
      {
        path: '',
        redirectTo: '/home',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: 'stock',
    component: StockMainComponent,
    children: [
      {
        path: 'user',
        children: [
          {
            path: 'profile',
            canActivate: [viewerGuard],
            component: ProfileComponent,
          },
          {
            path: 'list',
            canActivate: [adminGuard],
            component: UserListComponent,
          },
        ],
      },
      {
        path: 'admin',
        canActivate: [adminGuard],
        children: [
          { path: 'dashboard', component: DashboardComponent },
          {
            path: 'categories',
            component: CategoriesComponent,
            providers: [
              provideState(categoryFeature),
              provideEffects({
                create$,
                update$,
                findById$,
                findAll$,
                deleteById$,
              }),
            ],
          },
          { path: 'products', component: ProductListComponent },
          { path: 'new-product', component: CreateProductComponent },
          { path: 'edit-product/:productId', component: EditProductComponent },
          { path: 'suppliers', component: SupplierListComponent },
          { path: 'supplier', component: SupplierCreateComponent },
          { path: 'movement-stock', component: MovementStockComponent },
        ],
      },
      {
        path: 'manage',
        canActivate: [stockManagerGuard],
        component: StockComponent,
      },
      {
        path: 'purchase-order',
        canActivate: [stockManagerGuard],
        children: [
          { path: 'new', component: CreatePurchaseComponent },
          { path: 'list', component: PurchaseOrderListComponent },
          {
            path: 'view/:purchaseOrderId',
            component: PurchaseOrderDetailsComponent,
          },
        ],
      },
      {
        path: 'bill-order',
        canActivate: [salesManagerGuard],
        children: [
          { path: 'new', component: NewBillOrderComponent },
          { path: 'list', component: BillOrderListComponent },
          { path: 'view/:billOrderId', component: BillOrderDetailsComponent },
        ],
      },
    ],
  },
];
