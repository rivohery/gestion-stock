import { Component } from '@angular/core';
import { SidebarComponent } from '../layout/sidebar/sidebar.component';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-stock-main',
  imports: [SidebarComponent, RouterOutlet],
  templateUrl: './stock-main.component.html',
  styleUrl: './stock-main.component.css',
})
export class StockMainComponent {
  isSidebarExpanded: boolean = true;

  get sidebarClasses(): string {
    return `sidebar-transition bg-white h-screen shadow-md fixed overflow-y-auto z-40 ${
      this.isSidebarExpanded ? 'w-68' : 'w-0'
    }`;
  }

  get contentClasses(): string {
    return `content-transition flex-1 h-screen p-6 ${
      this.isSidebarExpanded ? 'ml-68' : 'ml-0'
    }`;
  }

  toggleSidebar() {
    this.isSidebarExpanded = !this.isSidebarExpanded;
  }
}
