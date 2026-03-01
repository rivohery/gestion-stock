import {
  Injectable,
  WritableSignal,
  computed,
  inject,
  signal,
} from '@angular/core';
import { HandleErrorService } from '../../../shared/services/handle-error.service';
import { environment } from '../../../../environments/environment.dev';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, catchError, take } from 'rxjs';
import { UserDetailsResponse } from '../models/auths.model';
import { State } from '../../../shared/models/state.model';
import {
  GlobalResponse,
  PageResponse,
} from '../../../shared/models/shared.model';
import {
  CreateUserRequest,
  UpdateUserInfoRequest,
  UpdateUserStatusRequest,
} from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService extends HandleErrorService {
  userUrl: string = `${environment.server_url}/users`;
  http = inject(HttpClient);

  updateUserInfosSignal: WritableSignal<State<GlobalResponse, string>> = signal(
    State.builder<GlobalResponse, string>().forInit().build()
  );
  updateUserInfosState = computed(() => this.updateUserInfosSignal());

  fetchAllUserSignal: WritableSignal<
    State<PageResponse<UserDetailsResponse>, string>
  > = signal(
    State.builder<PageResponse<UserDetailsResponse>, string>().forInit().build()
  );
  fetchAllUserState = computed(() => this.fetchAllUserSignal());

  updateUserStatusSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  updateUserStatusState = computed(() => this.updateUserStatusSignal());

  deleteUserByIdSignal: WritableSignal<State<GlobalResponse, string>> = signal(
    State.builder<GlobalResponse, string>().forInit().build()
  );
  deleteUserByIdState = computed(() => this.deleteUserByIdSignal());

  createUserSignal: WritableSignal<State<GlobalResponse, string>> = signal(
    State.builder<GlobalResponse, string>().forInit().build()
  );
  createUserState = computed(() => this.createUserSignal());

  getUserAuthenticated(): Observable<UserDetailsResponse> {
    return this.http
      .get<UserDetailsResponse>(`${this.userUrl}/get-user-authenticated`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  initCreateUserState(): void {
    this.createUserSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  createUser(request: CreateUserRequest): void {
    this.initCreateUserState();
    this.http
      .post<GlobalResponse>(`${this.userUrl}/create`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.createUserSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.createUserSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  initUpdateUserInfosState(): void {
    this.updateUserInfosSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  updateUserInfos(request: UpdateUserInfoRequest): void {
    this.initUpdateUserInfosState();
    this.http
      .post<GlobalResponse>(`${this.userUrl}/update-user-infos`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.updateUserInfosSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.updateUserInfosSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }
  uploadImage(formData: FormData): Observable<GlobalResponse> {
    return this.http
      .post<GlobalResponse>(`${this.userUrl}/save-profile-image`, formData, {
        headers: new HttpHeaders().set('Content-Type', 'multipart/form-data'),
        responseType: 'json',
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  downloadProfile(profileUrl: string): Observable<string> {
    return this.http
      .get(environment.server_url + profileUrl, {
        responseType: 'text',
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  findAllUser(search: string = '', page: number = 0, size: number = 3): void {
    this.fetchAllUserSignal.set(
      State.builder<PageResponse<UserDetailsResponse>, string>()
        .forInit()
        .build()
    );
    this.http
      .get<PageResponse<UserDetailsResponse>>(`${this.userUrl}/find-all`, {
        params: new HttpParams()
          .append('search', search)
          .append('page', page)
          .append('size', size),
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.fetchAllUserSignal.set(
            State.builder<PageResponse<UserDetailsResponse>, string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.fetchAllUserSignal.set(
            State.builder<PageResponse<UserDetailsResponse>, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  initUpdateUserStatus(): void {
    this.updateUserStatusSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  updateUserStatus(request: UpdateUserStatusRequest): void {
    this.initUpdateUserStatus();
    this.http
      .post<GlobalResponse>(`${this.userUrl}/update-user-status`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.updateUserStatusSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.updateUserStatusSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  initDeleteUserState(): void {
    this.deleteUserByIdSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  deleteUserById(userId: number): void {
    this.initDeleteUserState();
    this.http
      .delete<GlobalResponse>(`${this.userUrl}/delete/${userId}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.deleteUserByIdSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.deleteUserByIdSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }
}
