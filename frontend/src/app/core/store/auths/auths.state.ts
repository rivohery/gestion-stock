import { UserDetailsResponse } from '../../../features/auths/models/auths.model';

export interface AuthsState {
  userDetails: UserDetailsResponse | null;
  loading: boolean;
  errorMsg: string;
  successMsg: string;
}

export const initialState = {
  userDetails: null,
  loading: false,
  errorMsg: '',
  successMsg: '',
} as AuthsState;
