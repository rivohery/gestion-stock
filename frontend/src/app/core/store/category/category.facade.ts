import { inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { CategoryAction } from './category.action';
import { Category } from '../../../features/stock/models/product.model';
import { categoryFeature } from './category.reducer';

export function injectCategoryStore() {
  const store = inject(Store);

  return {
    findAll: () => store.dispatch(CategoryAction.findAll()),
    create: (request: Category) =>
      store.dispatch(CategoryAction.create({ request })),
    findById: (categoryId: number) =>
      store.dispatch(CategoryAction.findById({ categoryId })),
    deleteById: (categoryId: number) =>
      store.dispatch(CategoryAction.deleteById({ categoryId })),
    update: (request: Category) =>
      store.dispatch(CategoryAction.update({ request })),
    loading: store.selectSignal(categoryFeature.selectLoading),
    errorMsg: store.selectSignal(categoryFeature.selectErrorMsg),
    successMsg: store.selectSignal(categoryFeature.selectSuccessMsg),
    categoryChecked: store.selectSignal(categoryFeature.selectCategoryChecked),
    categories: store.selectSignal(categoryFeature.selectCategories),
    submiting: store.selectSignal(categoryFeature.selectSubmiting),
  };
}
