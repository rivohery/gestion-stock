import { CanActivateFn, Router } from '@angular/router';
import { injectAuthsStore } from '../store/auths/auths.facade';
import { inject } from '@angular/core';
import { SnackbarService } from '../../shared/services/snackbar-service';

export const salesManagerGuard: CanActivateFn = (route, state) => {
  const authStore = injectAuthsStore();
  const router = inject(Router);
  const snackbar = inject(SnackbarService);

  const isStockManager: boolean =
    authStore.userDetails()?.role.includes('SALES_MANAGER') || false;
  if (!isStockManager) {
    snackbar.openSnackBar(
      "Cette resource est seulement disponible pour les utilisateurs qui ont le role 'SALES_MANAGER'",
      'error'
    );
    router.navigateByUrl('/home');
    return false;
  }
  return true;
};
