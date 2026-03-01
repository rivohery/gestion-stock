import {
  ApplicationConfig,
  importProvidersFrom,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {
  provideClientHydration,
  withEventReplay,
} from '@angular/platform-browser';
import { provideState, provideStore } from '@ngrx/store';
import { authFeature } from './core/store/auths/auths.reducer';
import { provideEffects } from '@ngrx/effects';
import {
  loginByPswd$,
  getUserAuthenticated$,
  logout$,
  loginByOTP$,
} from './core/store/auths/auths.effect';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { NgOtpInputModule } from 'ng-otp-input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { authsInterceptor } from './core/interceptors/auths.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
    provideHttpClient(withInterceptors([authsInterceptor])),
    provideStore(),
    provideState(authFeature),
    provideEffects({
      loginByPswd$,
      getUserAuthenticated$,
      logout$,
      loginByOTP$,
    }),
    importProvidersFrom([NgOtpInputModule, MatSnackBarModule]),
  ],
};
