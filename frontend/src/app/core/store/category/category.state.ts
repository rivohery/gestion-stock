import { Category } from '../../../features/stock/models/product.model';

export interface CategoryState {
  categories: Category[];
  categoryChecked: Category | undefined;
  loading: boolean;
  submiting: boolean;
  errorMsg: string;
  successMsg: string;
}

export const initialCategoryState = {
  categories: [],
  submiting: false,
  categoryChecked: undefined,
  loading: false,
  errorMsg: '',
  successMsg: '',
} as CategoryState;
