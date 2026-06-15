import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, map, catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { ClienteRegistro } from '../shared/models';

export interface CustomerResponseDto {
  name: string;
  email: string;
  cpf: string;
  phone: string;
  salary: number;
  registrationStatus?: string;
  pendingManagerCpf?: string;
  address?: {
    streetName: string;
    streetNumber: string;
    complement?: string;
    zipCode: string;
    city: string;
    state: string;
  };
}

@Injectable({ providedIn: 'root' })
export class CustomerApiService {
  private readonly gatewayUrl = 'http://localhost:8000';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  solicitarAutocadastro(dados: ClienteRegistro): Observable<void> {
    const body = this.montarCorpoCadastro(dados);
    return this.http
      .post<CustomerResponseDto>(`${this.gatewayUrl}/customers/registration/request`, body)
      .pipe(map(() => undefined));
  }

  listarAutocadastrosPendentes(): Observable<ClienteRegistro[]> {
    const token = this.authService.obterToken();
    if (!token) {
      return throwError(() => new Error('Sessão expirada. Faça login novamente.'));
    }
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    return this.http
      .get<CustomerResponseDto[]>(`${this.gatewayUrl}/customers/registration/pending`, { headers })
      .pipe(
        map((lista) => lista.map((d) => this.paraClienteRegistro(d))),
        catchError((err: HttpErrorResponse) => {
          const msg =
            err.error?.message ??
            (typeof err.error === 'string' ? err.error : null) ??
            err.message ??
            'Não foi possível carregar as solicitações.';
          return throwError(() => new Error(msg));
        })
      );
  }

  private montarCorpoCadastro(d: ClienteRegistro): object {
    const cpf = d.cpf.replace(/\D/g, '');
    const cep = d.cep.replace(/\D/g, '');
    return {
      name: d.nome,
      email: d.email,
      cpf,
      phone: d.telefone,
      salary: d.salario,
      address: {
        streetName: d.logradouro,
        streetNumber: d.numero,
        complement: d.complemento || undefined,
        zipCode: cep,
        city: d.cidade,
        state: d.estado,
      },
    };
  }

  private paraClienteRegistro(d: CustomerResponseDto): ClienteRegistro {
    const a = d.address;
    return {
      nome: d.name,
      cpf: d.cpf,
      email: d.email,
      telefone: d.phone ?? '',
      salario: Number(d.salary),
      cep: a?.zipCode ?? '',
      logradouro: a?.streetName ?? '',
      numero: a?.streetNumber ?? '',
      complemento: a?.complement,
      cidade: a?.city ?? '',
      estado: a?.state ?? '',
    };
  }
}
