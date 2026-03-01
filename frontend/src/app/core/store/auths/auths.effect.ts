import { inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { AuthsAction } from './auths.action';
import { catchError, exhaustMap, of, map } from 'rxjs';
import {
  LoginResponse,
  UserDetailsResponse,
} from '../../../features/auths/models/auths.model';
import { AuthsService } from '../../../features/auths/services/auths.service';
import { TokenService } from '../../../features/auths/services/token.service';
import { UserService } from '../../../features/auths/services/user.service';
import { GlobalResponse } from '../../../shared/models/shared.model';

export const loginByPswd$ = createEffect(
  () => {
    const authsService = inject(AuthsService);
    const actions = inject(Actions);
    const tokenService = inject(TokenService);

    return actions.pipe(
      ofType(AuthsAction.loginByPswd),
      exhaustMap((action) => {
        return authsService.loginByPswd(action.request).pipe(
          exhaustMap((resp: LoginResponse) => {
            tokenService.saveTokenInLocaleStorage(resp.token);
            return of(
              AuthsAction.loginByPswdSuccess(),
              AuthsAction.getUserAuthenticated()
            );
          }),
          catchError((err) =>
            of(AuthsAction.loginByPswdFailed({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const getUserAuthenticated$ = createEffect(
  () => {
    const userService = inject(UserService);
    const tokenService = inject(TokenService);
    const actions = inject(Actions);

    return actions.pipe(
      ofType(AuthsAction.getUserAuthenticated),
      exhaustMap((action) => {
        return userService.getUserAuthenticated().pipe(
          map((userDetails: UserDetailsResponse) => {
            tokenService.saveUserAuthenticatedInLS(
              userDetails as UserDetailsResponse
            );
            return AuthsAction.getUserAuthenticatedSuccess({ userDetails });
          }),
          catchError((err) =>
            of(AuthsAction.getUserAuthenticatedFailed({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const logout$ = createEffect(
  () => {
    const authsService = inject(AuthsService);
    const actions = inject(Actions);
    const tokenService = inject(TokenService);

    return actions.pipe(
      ofType(AuthsAction.logout),
      exhaustMap((action) => {
        return authsService.logout().pipe(
          map((resp: GlobalResponse) => {
            tokenService.clearLocalStorage();
            return AuthsAction.logoutSuccess({ success: resp.message! });
          }),
          catchError((err) =>
            of(AuthsAction.logoutFailed({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const loginByOTP$ = createEffect(
  () => {
    const authsService = inject(AuthsService);
    const actions = inject(Actions);
    const tokenService = inject(TokenService);

    return actions.pipe(
      ofType(AuthsAction.loginByOTP),
      exhaustMap((action) => {
        return authsService.loginByOTP(action.request).pipe(
          exhaustMap((resp: LoginResponse) => {
            tokenService.saveTokenInLocaleStorage(resp.token);
            return of(
              AuthsAction.loginByOTPSuccess(),
              AuthsAction.getUserAuthenticated()
            );
          }),
          catchError((err) =>
            of(AuthsAction.loginByOTPFailed({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);
