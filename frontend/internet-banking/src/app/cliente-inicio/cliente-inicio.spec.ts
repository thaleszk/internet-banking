import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClienteInicio } from './cliente-inicio';

describe('ClienteInicio', () => {
  let component: ClienteInicio;
  let fixture: ComponentFixture<ClienteInicio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClienteInicio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClienteInicio);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
