import { ClienteRelatorio } from '../services/auth.service';

export interface Manager {
  id: number;
  name: string;
  cpf: string;
  email: string;
  password: string;
  clientes: ClienteRelatorio[];
}