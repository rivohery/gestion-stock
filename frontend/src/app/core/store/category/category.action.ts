import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { Category } from '../../../features/stock/models/product.model';
import { GlobalResponse } from '../../../shared/models/shared.model';

export const CategoryAction = createActionGroup({
  source: 'Categories',
  events: {
    create: props<{ request: Category }>(),
    createSuccess: props<{ resp: Category }>(),
    createFailed: props<{ error: string }>(),
    findAll: emptyProps,
    findAllSuccess: props<{ resp: Category[] }>(),
    findAllFailed: props<{ error: string }>(),
    findById: props<{ categoryId: number }>(),
    findByIdSuccess: props<{ category: Category }>(),
    findByIdFailed: props<{ error: string }>(),
    deleteById: props<{ categoryId: number }>(),
    deleteByIdSuccess: props<{ resp: GlobalResponse }>(),
    deleteByIdFailed: props<{ error: string }>(),
    update: props<{ request: Category }>(),
    updateSuccess: props<{ resp: Category }>(),
    updateFailed: props<{ error: string }>(),
  },
});
