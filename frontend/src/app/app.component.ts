import { Component, OnInit, effect, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/components/layout/navbar/navbar.component';
import { injectAuthsStore } from './core/store/auths/auths.facade';
import { TokenService } from './features/auths/services/token.service';
import { UserDetailsResponse } from './features/auths/models/auths.model';

@Component({
  selector: 'app-root',
  imports: [NavbarComponent, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'gestion-stock';
  authStore = injectAuthsStore();
  tokenService = inject(TokenService);

  constructor() {
    effect(() => {
      //rafraissichement du navigateur
      if (this.authStore.userDetails() == null) {
        let userDetailsInLS: UserDetailsResponse | null =
          this.tokenService.getUserAuthenticatedFromLS();
        if (userDetailsInLS != null) {
          //console.log(userDetailsInLS);
          this.authStore.refreshUserDetails(userDetailsInLS);
        }
      }
    });
  }

  ngOnInit(): void {}
}
