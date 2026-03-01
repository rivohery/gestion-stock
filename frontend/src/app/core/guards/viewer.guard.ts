import { CanActivateFn, Router } from '@angular/router';
import { injectAuthsStore } from '../store/auths/auths.facade';
import { inject } from '@angular/core';
import { SnackbarService } from '../../shared/services/snackbar-service';

export const viewerGuard: CanActivateFn = (route, state) => {
  const authStore = injectAuthsStore();
  const router = inject(Router);
  const snackbar = inject(SnackbarService);

  const authenticate: boolean =
    authStore.userDetails()?.role.includes('VIEWER') || false;
  if (!authenticate) {
    snackbar.openSnackBar("Vous devez s'authentifier", 'error');
    router.navigateByUrl('/home');
    return false;
  }
  return true;
};
