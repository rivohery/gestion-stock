// dropdown.directive.ts
import { Directive, ElementRef, HostListener } from '@angular/core';

@Directive({
  selector: '[appDropdown]',
  standalone: true,
})
export class DropdownDirective {
  constructor(private el: ElementRef) {}

  @HostListener('click')
  onClick() {
    const dropdown = this.el.nativeElement.querySelector(
      '.dropdown-transition'
    );
    const arrow = this.el.nativeElement.querySelector('.dropdown-arrow');

    if (dropdown && arrow) {
      if (dropdown.classList.contains('max-h-0')) {
        dropdown.classList.remove('max-h-0');
        dropdown.classList.add('max-h-96', 'py-2');
        arrow.classList.add('rotate-90');
      } else {
        dropdown.classList.add('max-h-0');
        dropdown.classList.remove('max-h-96', 'py-2');
        arrow.classList.remove('rotate-90');
      }
    }
  }
}
