import { isPlatformBrowser } from '@angular/common';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { UserDetailsResponse } from '../models/auths.model';

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  private isBrowser: boolean;

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  saveTokenInLocaleStorage(token: string): void {
    if (this.isBrowser) {
      localStorage.setItem('access-token', token);
    } else {
      console.warn(
        'Attempted to save token in localStorage on non-browser platform.'
      );
    }
  }

  getTokenFromLocalStorage(): string | null {
    if (this.isBrowser) {
      return (localStorage.getItem('access-token') as string) || null;
    } else {
      console.warn('localStorage undefined in Server environment.');
      return null;
    }
  }

  clearLocalStorage(): void {
    if (this.isBrowser) {
      localStorage.clear();
    } else {
      console.warn('localStorage undefined in Server environment.');
    }
  }

  saveUserAuthenticatedInLS(userDetails: UserDetailsResponse): void {
    if (this.isBrowser) {
      localStorage.setItem('user-details-infos', JSON.stringify(userDetails));
    } else {
      console.warn('localStorage undefined in Server environment.');
    }
  }

  getUserAuthenticatedFromLS(): UserDetailsResponse | null {
    if (this.isBrowser) {
      return (
        (JSON.parse(
          localStorage.getItem('user-details-infos') as string
        ) as UserDetailsResponse) || null
      );
    } else {
      console.warn('localStorage undefined in Server environment.');
      return null;
    }
  }
}
