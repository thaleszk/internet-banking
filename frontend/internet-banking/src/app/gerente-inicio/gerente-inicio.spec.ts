import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GerenteInicio } from './gerente-inicio';

describe('GerenteInicio', () => {
  let component: GerenteInicio;
  let fixture: ComponentFixture<GerenteInicio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GerenteInicio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GerenteInicio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
