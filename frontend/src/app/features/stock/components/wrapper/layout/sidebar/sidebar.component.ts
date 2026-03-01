import { Component, Input } from '@angular/core';
import { DropdownDirective } from '../../../../../../shared/directives/dropdown.directive';
import { RouterLink } from '@angular/router';
import { injectAuthsStore } from '../../../../../../core/store/auths/auths.facade';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  imports: [DropdownDirective, RouterLink, NgIf],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
  @Input()
  isSidebarExpanded: boolean = true;
  authStore = injectAuthsStore();

  get menuTextClasses(): string {
    return `menu-text  whitespace-nowrap ${
      this.isSidebarExpanded ? 'opacity-100' : 'opacity-0'
    }`;
  }

  get isAdmin(): boolean {
    return this.authStore.userDetails()?.role.includes('ADMIN') || false;
  }

  get isStockManager(): boolean {
    return (
      this.authStore.userDetails()?.role.includes('STOCK_MANAGER') || false
    );
  }

  get isSalesManager(): boolean {
    return (
      this.authStore.userDetails()?.role.includes('SALES_MANAGER') || false
    );
  }

  get isViewer(): boolean {
    return this.authStore.userDetails()?.role.includes('VIEWER') || false;
  }
}
