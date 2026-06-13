import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { GerenteListagem, NovoGerente, AtualizacaoGerente } from '../shared/models';

@Injectable({ providedIn: 'root' })
export class ManagerApiService {
  private readonly gatewayUrl = 'http://localhost:8000';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private headers(): HttpHeaders {
    const token = this.authService.obterToken();
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

  // R19 — Listar todos os gerentes
  listarGerentes(): Observable<GerenteListagem[]> {
    return this.http
      .get<GerenteListagem[]>(`${this.gatewayUrl}/managers`, {
        headers: this.headers()
      })
      .pipe(catchError(err => throwError(() => new Error(err.error?.message ?? 'Erro ao listar gerentes.'))));
  }

  // R17 — Inserir gerente
  inserirGerente(dados: NovoGerente): Observable<GerenteListagem> {
    const body = {
      name:  dados.nome,
      cpf:   dados.cpf.replace(/\D/g, ''),
      email: dados.email,
      phone: dados.telefone,
      password: dados.senha,
    };
    return this.http
      .post<GerenteListagem>(`${this.gatewayUrl}/managers`, body, {
        headers: this.headers()
      })
      .pipe(catchError(err => throwError(() => new Error(err.error?.message ?? 'Erro ao cadastrar gerente.'))));
  }

  // R20 — Alterar gerente
  alterarGerente(cpf: string, dados: AtualizacaoGerente): Observable<GerenteListagem> {
    const body: any = { name: dados.nome, email: dados.email };
    if (dados.senha) body['password'] = dados.senha;
    return this.http
      .put<GerenteListagem>(`${this.gatewayUrl}/managers/${cpf}`, body, {
        headers: this.headers()
      })
      .pipe(catchError(err => throwError(() => new Error(err.error?.message ?? 'Erro ao atualizar gerente.'))));
  }

  // R18 — Remover gerente
  removerGerente(cpf: string): Observable<void> {
    return this.http
      .delete<void>(`${this.gatewayUrl}/managers/${cpf}`, {
        headers: this.headers()
      })
      .pipe(catchError(err => throwError(() => new Error(err.error?.message ?? 'Erro ao remover gerente.'))));
  }
}
