import { createActionGroup, emptyProps, props } from '@ngrx/store';
import {
  LoginRequest,
  OTPTokenRequest,
  UserDetailsResponse,
} from '../../../features/auths/models/auths.model';
export const AuthsAction = createActionGroup({
  source: 'Auths',
  events: {
    verifyLocalStorage: emptyProps,
    refreshUserDetails: props<{ userDetails: UserDetailsResponse | null }>(),
    loginByPswd: props<{ request: LoginRequest }>(),
    loginByPswdSuccess: emptyProps,
    loginByPswdFailed: props<{ error: string }>(),
    loginByOTP: props<{ request: OTPTokenRequest }>(),
    loginByOTPSuccess: emptyProps,
    loginByOTPFailed: props<{ error: string }>(),
    getUserAuthenticated: emptyProps,
    getUserAuthenticatedSuccess: props<{ userDetails: UserDetailsResponse }>(),
    getUserAuthenticatedFailed: props<{ error: string }>(),
    logout: emptyProps,
    logoutSuccess: props<{ success: string }>(),
    logoutFailed: props<{ error: string }>(),
  },
});
