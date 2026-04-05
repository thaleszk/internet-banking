import { Injectable } from '@angular/core';
import { Manager } from '../models/manager.model';
import { AuthService, ClienteRelatorio } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ManagerService {

  private managers: Manager[] = [];

  constructor(private authService: AuthService) {}

  getManagers(): Manager[] {
    return this.managers;
  }

  insertManager(newManagerData: Omit<Manager, 'id' | 'clientes'>): Manager {
    const newManager: Manager = {
      id: this.generateManagerId(),
      ...newManagerData,
      clientes: []
    };

    this.applyClientDistributionRule(newManager);
    this.managers.push(newManager);

    return newManager;
  }

  private applyClientDistributionRule(newManager: Manager): void {
    if (this.managers.length === 0) {
      return;
    }

    if (this.managers.length === 1) {
      const unicoGerenteClientes = this.getClientesDoGerente(this.managers[0].name);

      if (unicoGerenteClientes.length <= 1) {
        return;
      }
    }

    const managersWithClientes = this.managers.map(manager => {
      const clientes = this.getClientesDoGerente(manager.name);
      return {
        manager,
        clientes
      };
    });

    const maxClientes = Math.max(...managersWithClientes.map(item => item.clientes.length));

    const candidates = managersWithClientes.filter(item =>
      item.clientes.length === maxClientes && item.clientes.length > 0
    );

    if (candidates.length === 0) {
      return;
    }

    const selectedManagerWrapper = candidates.reduce((previous, current) => {
      const previousLowestSaldo = this.getLowestPositiveSaldo(previous.clientes);
      const currentLowestSaldo = this.getLowestPositiveSaldo(current.clientes);

      return currentLowestSaldo < previousLowestSaldo ? current : previous;
    });

    const clienteSelecionado = this.extractClienteWithLowestPositiveSaldo(
      selectedManagerWrapper.manager.name,
      selectedManagerWrapper.clientes
    );

    if (clienteSelecionado) {
      clienteSelecionado.gerenteNome = newManager.name;
      clienteSelecionado.gerenteCpf = newManager.cpf;
      newManager.clientes.push(clienteSelecionado);
    }
  }

  private getClientesDoGerente(nomeGerente: string): ClienteRelatorio[] {
    return this.authService.obterClientesDoGerente(nomeGerente) ?? [];
  }

  private getLowestPositiveSaldo(clientes: ClienteRelatorio[]): number {
    const clientesComSaldoPositivo = clientes.filter(cliente => (cliente.saldo ?? 0) > 0);

    if (clientesComSaldoPositivo.length === 0) {
      return Number.MAX_SAFE_INTEGER;
    }

    return Math.min(...clientesComSaldoPositivo.map(cliente => cliente.saldo ?? Number.MAX_SAFE_INTEGER));
  }

  private extractClienteWithLowestPositiveSaldo(
    managerName: string,
    clientes: ClienteRelatorio[]
  ): ClienteRelatorio | null {
    const clientesComSaldoPositivo = clientes.filter(cliente => (cliente.saldo ?? 0) > 0);

    if (clientesComSaldoPositivo.length === 0) {
      return null;
    }

    const menorSaldoPositivo = Math.min(
      ...clientesComSaldoPositivo.map(cliente => cliente.saldo ?? Number.MAX_SAFE_INTEGER)
    );

    const clienteSelecionado = clientes.find(
      cliente => (cliente.saldo ?? 0) === menorSaldoPositivo && (cliente.saldo ?? 0) > 0
    );

    if (!clienteSelecionado) {
      return null;
    }

    this.removeClienteDoGerenteAtual(managerName, clienteSelecionado);

    return { ...clienteSelecionado };
  }

  private removeClienteDoGerenteAtual(managerName: string, cliente: ClienteRelatorio): void {
    const manager = this.managers.find(item => item.name === managerName);

    if (!manager) {
      return;
    }

    manager.clientes = manager.clientes.filter(item => item.cpf !== cliente.cpf);
  }

  private generateManagerId(): number {
    if (this.managers.length === 0) {
      return 1;
    }

    return Math.max(...this.managers.map(manager => manager.id)) + 1;
  }
}