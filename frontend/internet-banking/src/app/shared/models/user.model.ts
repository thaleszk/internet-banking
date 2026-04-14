export interface User {
  id?: string;
  email: string;
  nome: string;
  cpf: string;
  perfil: 'cliente' | 'gerente' | 'admin';
  salario?: number;
  saldo?: number;
  limite?: number;
  numeroConta?: string;
  gerente?: string;
  telefone?: string;
  logradouro?: string;
  numero?: string;
  complemento?: string;
  cep?: string;
  cidade?: string;
  estado?: string;
}