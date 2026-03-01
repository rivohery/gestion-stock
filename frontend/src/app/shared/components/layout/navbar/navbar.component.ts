import {
  Component,
  EventEmitter,
  Output,
  effect,
  inject,
  signal,
} from '@angular/core';
import { injectAuthsStore } from '../../../../core/store/auths/auths.facade';
import { Router, RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { SnackbarService } from '../../../services/snackbar-service';
import { MatMenuModule } from '@angular/material/menu';

@Component({
  selector: 'app-navbar',
  imports: [MatIconModule, MatButtonModule, MatMenuModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  @Output()
  onToggleSidebar: EventEmitter<any> = new EventEmitter();
  router = inject(Router);

  authStore = injectAuthsStore();
  stockLinksVisible = signal<boolean>(false);
  snackbar = inject(SnackbarService);

  constructor() {
    effect(() => {
      /** effect of login action **/
      if (this.authStore.userDetails() !== null) {
        this.stockLinksVisible.set(true);
      } else {
        this.stockLinksVisible.set(false);
      }
    });
    effect(() => {
      /** effect of logout action **/
      if (this.authStore.successMsg()) {
        this.snackbar.openSnackBar(
          this.authStore.successMsg() || '',
          'success'
        );
        this.router.navigateByUrl('/home');
      }
      if (this.authStore.errorMsg()) {
        this.snackbar.openSnackBar(this.authStore.errorMsg() || '', 'error');
      }
    });
  }

  resetPassword(): void {
    this.router.navigate(['reset-password']);
  }

  login(): void {
    this.router.navigateByUrl('/login');
  }

  logout(): void {
    this.authStore.logout();
  }

  showProfile(): void {
    this.router.navigateByUrl('/stock/user/profile');
  }
}
