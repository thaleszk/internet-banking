export interface ClienteRelatorio {
  cpf: string;
  nome: string;
  email: string;
  salario?: number;
  numeroConta?: string;
  saldo?: number;
  limite?: number;
  gerenteCpf?: string;
  gerenteNome?: string;
}

