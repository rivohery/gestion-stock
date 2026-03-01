import { inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { CategoryService } from '../../../features/stock/services/category.service';
import { catchError, exhaustMap, of, switchMap } from 'rxjs';
import { CategoryAction } from './category.action';

export const create$ = createEffect(
  () => {
    const categoryService = inject(CategoryService);
    const actions = inject(Actions);

    return actions.pipe(
      ofType(CategoryAction.create),
      exhaustMap((action) => {
        return categoryService.create(action.request).pipe(
          exhaustMap((resp) => of(CategoryAction.createSuccess({ resp }))),
          catchError((err) =>
            of(CategoryAction.createFailed({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const update$ = createEffect(
  () => {
    const categoryService = inject(CategoryService);
    const actions = inject(Actions);

    return actions.pipe(
      ofType(CategoryAction.update),
      exhaustMap((action) => {
        return categoryService.update(action.request).pipe(
          exhaustMap((resp) => of(CategoryAction.updateSuccess({ resp }))),
          catchError((err) =>
            of(CategoryAction.updateFailed({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const deleteById$ = createEffect(
  () => {
    const categoryService = inject(CategoryService);
    const actions = inject(Actions);

    return actions.pipe(
      ofType(CategoryAction.deleteById),
      exhaustMap((action) => {
        return categoryService.deleteById(action.categoryId).pipe(
          exhaustMap((resp) =>
            of(
              CategoryAction.deleteByIdSuccess({ resp }),
              CategoryAction.findAll()
            )
          ),
          catchError((err) =>
            of(CategoryAction.deleteByIdFailed({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const findAll$ = createEffect(
  () => {
    const categoryService = inject(CategoryService);
    const actions = inject(Actions);

    return actions.pipe(
      ofType(CategoryAction.findAll),
      switchMap((action) => {
        return categoryService.findAll().pipe(
          switchMap((resp) => of(CategoryAction.findAllSuccess({ resp }))),
          catchError((err) =>
            of(CategoryAction.findAllFailed({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const findById$ = createEffect(
  () => {
    const categoryService = inject(CategoryService);
    const actions = inject(Actions);

    return actions.pipe(
      ofType(CategoryAction.findById),
      switchMap((action) => {
        return categoryService.findById(action.categoryId).pipe(
          switchMap((resp) =>
            of(CategoryAction.findByIdSuccess({ category: resp }))
          ),
          catchError((err) =>
            of(CategoryAction.findByIdFailed({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);
