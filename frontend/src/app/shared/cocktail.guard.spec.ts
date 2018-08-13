import { TestBed, async, inject } from '@angular/core/testing';

import { OrderGuard } from './order-guard.service';

describe('OrderGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [OrderGuard],
    });
  });

  it('should ...', inject([OrderGuard], (guard: OrderGuard) => {
    expect(guard).toBeTruthy();
  }));
});
