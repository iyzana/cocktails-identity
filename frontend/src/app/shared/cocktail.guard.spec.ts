import { TestBed, async, inject } from '@angular/core/testing';

import { OrderGuard } from './order-guard.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('OrderGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [OrderGuard],
      imports: [RouterTestingModule],
    });
  });

  it('should ...', inject([OrderGuard], (guard: OrderGuard) => {
    expect(guard).toBeTruthy();
  }));
});
