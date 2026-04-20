import {ClienteRelatorio} from '../models';

export interface Manager {
  id: number;
  name: string;
  cpf: string;
  email: string;
  password: string;
  clientes: ClienteRelatorio[];
}

export interface GerenteResumo {
  nome: string;
  email: string;
  clienteCount: number;
  somaSaldosPositivos: number;
  somaSaldosNegativos: number;
}

export interface GerenteListagem {
  cpf: string;
  nome: string;
  email: string;
  telefone?: string;
  clienteCount: number;
  somaSaldosPositivos: number;
  somaSaldosNegativos: number;
}

export interface AtualizacaoGerente {
  nome: string;
  email: string;
  senha?: string;
}

export interface NovoGerente {
  nome: string;
  cpf: string;
  email: string;
  telefone: string;
  senha: string;
}