import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { salesManagerGuard } from './sales-manager.guard';

describe('salesManagerGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => salesManagerGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
