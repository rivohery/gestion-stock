import {
  Injectable,
  WritableSignal,
  computed,
  inject,
  signal,
} from '@angular/core';
import { HandleErrorService } from '../../../shared/services/handle-error.service';
import { environment } from '../../../../environments/environment.dev';
import {
  HttpClient,
  HttpContext,
  HttpContextToken,
  HttpHeaders,
} from '@angular/common/http';
import {
  ForgotPasswordRequest,
  LoginRequest,
  LoginResponse,
  OTPTokenRequest,
  UpdatePasswordRequest,
} from '../models/auths.model';
import { Observable, catchError, take } from 'rxjs';
import { GlobalResponse } from '../../../shared/models/shared.model';
import { State } from '../../../shared/models/state.model';

// Définir un HttpContextToken pour ignorer l'intercepteur d'autorisation
export const SKIP_TOKEN_ADDITION = new HttpContextToken<boolean>(() => false);

@Injectable({
  providedIn: 'root',
})
export class AuthsService extends HandleErrorService {
  authUrl = `${environment.server_url}/auth`;
  http = inject(HttpClient);

  private forgotPswdSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  forgotPswdState$ = computed(() => this.forgotPswdSignal());
  private resetPswdSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  resetPswdState$ = computed(() => this.resetPswdSignal());

  updatePswdProcess(request: UpdatePasswordRequest): void {
    this.resetPswdSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
    this.http
      .post<GlobalResponse>(`${this.authUrl}/update-password`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.resetPswdSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.resetPswdSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  forgotPswdProcess(request: ForgotPasswordRequest): void {
    this.forgotPswdSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
    this.http
      .post<GlobalResponse>(
        `${this.authUrl}/forgot-password-process`,
        request,
        {
          headers: new HttpHeaders().set('Content-Type', 'application/json'),
        }
      )
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.forgotPswdSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.forgotPswdSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  loginByPswd(request: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.authUrl}/login-by-password`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
        withCredentials: true,
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  logout(): Observable<GlobalResponse> {
    return this.http
      .post<GlobalResponse>(
        `${this.authUrl}/logout`,
        {},
        {
          headers: new HttpHeaders(),
          withCredentials: true,
          context: new HttpContext().set(SKIP_TOKEN_ADDITION, true),
        }
      )
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  loginByOTP(request: OTPTokenRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.authUrl}/login-by-otp`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
        withCredentials: true,
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  checkRefreshToken(): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(
        `${this.authUrl}/login-by-refresh-token`,
        {},
        {
          headers: new HttpHeaders(),
          withCredentials: true,
          context: new HttpContext().set(SKIP_TOKEN_ADDITION, true),
        }
      )
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }
}
