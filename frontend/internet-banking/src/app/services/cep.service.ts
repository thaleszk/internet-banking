import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CepResponse {
  cep: string;
  logradouro: string;
  complemento: string;
  bairro: string;
  localidade: string;
  uf: string;
  erro?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CepService {

  constructor(private readonly http: HttpClient) {}

  getAddressByCep(cep: string): Observable<CepResponse> {
    const cleanCep = cep.replace(/\D/g, '');
    return this.http.get<CepResponse>(`https://viacep.com.br/ws/${cleanCep}/json/`);
  }
}
