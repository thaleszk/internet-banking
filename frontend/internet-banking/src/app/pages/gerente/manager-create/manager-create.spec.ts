import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerCreate } from './manager-create';

describe('ManagerCreate', () => {
  let component: ManagerCreate;
  let fixture: ComponentFixture<ManagerCreate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerCreate]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerCreate);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
