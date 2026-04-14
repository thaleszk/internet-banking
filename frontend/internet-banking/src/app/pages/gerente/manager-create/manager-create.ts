import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ManagerService } from '../../../services/manager.service';
import { Manager } from '../../../shared/models/manager.model';

@Component({
  selector: 'app-manager-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './manager-create.html',
  styleUrls: ['./manager-create.css']
})
export class ManagerCreateComponent implements OnInit {

  managerForm!: FormGroup;
  managers: Manager[] = [];
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private managerService: ManagerService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadManagers();
  }

  initializeForm(): void {
    this.managerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      cpf: ['', [Validators.required, Validators.minLength(11), Validators.maxLength(11)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  loadManagers(): void {
    this.managers = this.managerService.getManagers();
  }

  onSubmit(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (this.managerForm.invalid) {
      this.managerForm.markAllAsTouched();
      this.errorMessage = 'Preencha os campos corretamente.';
      return;
    }

    const createdManager = this.managerService.insertManager(this.managerForm.value);
    this.loadManagers();

    this.successMessage =
      `Gerente ${createdManager.name} cadastrado com sucesso. ` +
      `Contas vinculadas: ${createdManager.clientes.length}.`;

    this.managerForm.reset();
  }

  get name() {
    return this.managerForm.get('name');
  }

  get cpf() {
    return this.managerForm.get('cpf');
  }

  get email() {
    return this.managerForm.get('email');
  }

  get password() {
    return this.managerForm.get('password');
  }
}