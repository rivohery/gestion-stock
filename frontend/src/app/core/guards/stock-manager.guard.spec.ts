import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { stockManagerGuard } from './stock-manager.guard';

describe('stockManagerGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => stockManagerGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
