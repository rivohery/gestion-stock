import { inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { authFeature } from './auths.reducer';
import {
  LoginRequest,
  OTPTokenRequest,
  UserDetailsResponse,
} from '../../../features/auths/models/auths.model';
import { AuthsAction } from './auths.action';

export function injectAuthsStore() {
  const store = inject(Store);

  return {
    loginByPswd: (request: LoginRequest) =>
      store.dispatch(AuthsAction.loginByPswd({ request })),
    logout: () => store.dispatch(AuthsAction.logout()),
    refreshUserDetails: (userDetails: UserDetailsResponse) =>
      store.dispatch(AuthsAction.refreshUserDetails({ userDetails })),
    loginByOTP: (request: OTPTokenRequest) =>
      store.dispatch(AuthsAction.loginByOTP({ request })),
    userDetails: store.selectSignal(authFeature.selectUserDetails),
    loading: store.selectSignal(authFeature.selectLoading),
    errorMsg: store.selectSignal(authFeature.selectErrorMsg),
    successMsg: store.selectSignal(authFeature.selectSuccessMsg),
  };
}
