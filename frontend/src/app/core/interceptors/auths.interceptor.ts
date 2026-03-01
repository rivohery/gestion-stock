import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandlerFn,
  HttpHeaders,
  HttpInterceptorFn,
  HttpRequest,
} from '@angular/common/http';
import { injectAuthsStore } from '../store/auths/auths.facade';
import { inject } from '@angular/core';
import { TokenService } from '../../features/auths/services/token.service';
import {
  AuthsService,
  SKIP_TOKEN_ADDITION,
} from '../../features/auths/services/auths.service';
import {
  BehaviorSubject,
  Observable,
  catchError,
  defer,
  filter,
  switchMap,
  take,
  throwError,
} from 'rxjs';

const isRefreshingToken = new BehaviorSubject<boolean>(false);

export const authsInterceptor: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const authStore = injectAuthsStore();
  const tokenService = inject(TokenService);
  const authService = inject(AuthsService);

  // Vérifier si le flag SKIP_TOKEN_ADDITION est présent et vrai
  if (request.context.get(SKIP_TOKEN_ADDITION)) {
    return next(request); // Passer la requête sans ajouter l'en-tête d'autorisation
  }

  let authReq = request;
  const accessToken = tokenService.getTokenFromLocalStorage();
  if (accessToken) {
    authReq = addToken(authReq, accessToken);
  }
  return next(authReq).pipe(
    catchError((error) => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        return handle401Error(authReq, next, {
          authService,
          tokenService,
          authStore,
        });
      }
      return throwError(() => error);
    })
  );
};

function addToken(
  request: HttpRequest<unknown>,
  token: string
): HttpRequest<unknown> {
  return request.clone({
    headers: new HttpHeaders().set('Authorization', `Bearer ${token}`),
  });
}

function handle401Error(
  request: HttpRequest<unknown>,
  next: HttpHandlerFn,
  dependencies: {
    authService: AuthsService;
    tokenService: TokenService;
    authStore: ReturnType<typeof injectAuthsStore>;
  }
): Observable<HttpEvent<unknown>> {
  const { authService, tokenService, authStore } = dependencies;

  // Utiliser defer pour s'assurer que l'état de isRefreshingToken est lu au moment ôu une requêtte http echoue avec une erreur 401
  return defer(() => {
    if (!isRefreshingToken.getValue()) {
      isRefreshingToken.next(true); // Indiquer qu'un rafraîchissement est en cours

      return authService.checkRefreshToken().pipe(
        switchMap((response) => {
          tokenService.saveTokenInLocaleStorage(response.token);
          isRefreshingToken.next(false); // Réinitialiser l'état après un rafraîchissement réussi
          return next(addToken(request, response.token!));
        }),
        catchError((err: any) => {
          isRefreshingToken.next(false); // Réinitialiser l'état en cas d'erreur
          authStore.logout();
          return throwError(() => err);
        })
      );
    } else {
      return isRefreshingToken.pipe(
        filter((isRefreshing) => !isRefreshing), //ne laisse passer que si la valeur à nouveau de isRefreshing est false
        take(1),
        switchMap(() => {
          const newAccessToken = tokenService.getTokenFromLocalStorage();
          return next(addToken(request, newAccessToken!)); //on rélance à nouveau la requêtte originale avec le nouveau accessToken
        })
      );
    }
  });
}
