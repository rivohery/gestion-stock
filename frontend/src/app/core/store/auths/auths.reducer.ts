import { createFeature, createReducer, on } from '@ngrx/store';
import { initialState } from './auths.state';
import { AuthsAction } from './auths.action';

export const authFeature = createFeature({
  name: 'auths',
  reducer: createReducer(
    initialState,
    on(AuthsAction.refreshUserDetails, (state, action) => {
      return {
        ...state,
        successMsg: '',
        errorMsg: '',
        loading: false,
        userDetails: action.userDetails,
      };
    }),
    on(AuthsAction.loginByPswd, (state) => {
      return {
        ...state,
        loading: true,
        successMsg: '',
        errorMsg: '',
      };
    }),
    on(AuthsAction.loginByPswdSuccess, (state) => {
      return {
        ...state,
        errorMsg: '',
        successMsg: '',
      };
    }),
    on(AuthsAction.loginByPswdFailed, (state, action) => {
      return {
        ...state,
        loading: false,
        errorMsg: action.error,
        successMsg: '',
      };
    }),
    on(AuthsAction.loginByOTP, (state, action) => {
      return {
        ...state,
        loading: true,
        successMsg: '',
        errorMsg: '',
        userDetails: null,
      };
    }),
    on(AuthsAction.loginByOTPSuccess, (state) => {
      return {
        ...state,
        errorMsg: '',
        successMsg: '',
      };
    }),
    on(AuthsAction.loginByOTPFailed, (state, action) => {
      return {
        ...state,
        errorMsg: action.error,
        successMsg: '',
        loading: false,
        userDetails: null,
      };
    }),
    on(AuthsAction.getUserAuthenticatedSuccess, (state, action) => {
      return {
        ...state,
        userDetails: action.userDetails,
        loading: false,
        errorMsg: '',
        successMsg: 'Welcome,login successfully!',
      };
    }),
    on(AuthsAction.getUserAuthenticatedFailed, (state, action) => {
      return {
        ...state,
        errorMsg: action.error,
        successMsg: '',
        loading: false,
      };
    }),
    on(AuthsAction.logout, (state, action) => {
      return {
        ...state,
        errorMsg: '',
        successMsg: '',
      };
    }),
    on(AuthsAction.logoutSuccess, (state, action) => {
      return {
        ...state,
        successMsg: action.success,
        userDetails: null,
        errorMsg: '',
      };
    }),
    on(AuthsAction.logoutFailed, (state, action) => {
      return {
        ...state,
        errorMsg: action.error,
        successMsg: '',
      };
    })
  ),
});
