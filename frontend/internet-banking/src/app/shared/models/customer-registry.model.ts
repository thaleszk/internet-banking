export interface ClienteRegistro {
  nome: string;
  cpf: string;
  email: string;
  telefone: string;
  salario: number;
  cep: string;
  logradouro: string;
  numero: string;
  complemento?: string;
  cidade: string;
  estado: string;
}