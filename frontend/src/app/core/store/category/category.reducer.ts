import { createFeature, createReducer, on } from '@ngrx/store';
import { initialCategoryState } from './category.state';
import { CategoryAction } from './category.action';

export const categoryFeature = createFeature({
  name: 'categories',
  reducer: createReducer(
    initialCategoryState,
    on(CategoryAction.findAll, (state, action) => {
      return {
        ...state,
        loading: true,
        errorMsg: '',
        successMsg: '',
        categories: [],
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.findAllSuccess, (state, action) => {
      return {
        ...state,
        loading: false,
        errorMsg: '',
        categories: action.resp,
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.findAllFailed, (state, action) => {
      return {
        ...state,
        loading: false,
        errorMsg: '',
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.create, (state, action) => {
      return {
        ...state,
        submiting: true,
        errorMsg: '',
        successMsg: '',
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.createSuccess, (state, action) => {
      return {
        ...state,
        submiting: false,
        errorMsg: '',
        successMsg: `Category was created successfully: ${action.resp?.id}`,
        categories: [action.resp, ...state.categories],
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.createFailed, (state, action) => {
      return {
        ...state,
        submiting: false,
        errorMsg: `${action.error}`,
        successMsg: '',
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.deleteById, (state) => {
      return {
        ...state,
        loading: true,
        errorMsg: '',
        successMsg: '',
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.deleteByIdSuccess, (state, action) => {
      return {
        ...state,
        errorMsg: '',
        successMsg: `${action.resp.message}`,
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.deleteByIdFailed, (state, action) => {
      return {
        ...state,
        errorMsg: `${action.error}`,
        loading: false,
        successMsg: '',
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.findById, (state, action) => {
      return {
        ...state,
        loading: true,
        errorMsg: '',
        successMsg: '',
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.findByIdSuccess, (state, action) => {
      return {
        ...state,
        loading: false,
        errorMsg: '',
        categoryChecked: action.category,
      };
    }),
    on(CategoryAction.findByIdFailed, (state, action) => {
      return {
        ...state,
        loading: false,
        errorMsg: `${action.error}`,
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.update, (state, action) => {
      return {
        ...state,
        submiting: true,
        errorMsg: '',
        successMsg: '',
      };
    }),
    on(CategoryAction.updateSuccess, (state, action) => {
      return {
        ...state,
        submiting: false,
        errorMsg: '',
        successMsg: `Category was updated successfully: ${action.resp?.id}`,
        categories: state.categories.map((c) => {
          if (c.id === action.resp?.id) return action.resp;
          else return c;
        }),
        categoryChecked: undefined,
      };
    }),
    on(CategoryAction.updateFailed, (state, action) => {
      return {
        ...state,
        submiting: false,
        errorMsg: `${action.error}`,
        successMsg: '',
      };
    })
  ),
});
