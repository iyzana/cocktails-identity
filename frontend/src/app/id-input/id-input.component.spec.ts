import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IdInputComponent } from './id-input.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';

describe('IdInputComponent', () => {
  let component: IdInputComponent;
  let fixture: ComponentFixture<IdInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [IdInputComponent],
      imports: [FontAwesomeModule, FormsModule, RouterTestingModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IdInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
