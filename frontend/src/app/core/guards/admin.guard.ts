import { CanActivateFn, Router } from '@angular/router';
import { injectAuthsStore } from '../store/auths/auths.facade';
import { inject } from '@angular/core';
import { SnackbarService } from '../../shared/services/snackbar-service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authStore = injectAuthsStore();
  const snackbar = inject(SnackbarService);
  const router = inject(Router);

  const isAdmin: boolean =
    authStore.userDetails()?.role.includes('ADMIN') || false;
  if (!isAdmin) {
    snackbar.openSnackBar(
      "Cette resource est reservé pour l'administrateur",
      'error'
    );
    router.navigateByUrl('/home');
    return false;
  }
  return true;
};
