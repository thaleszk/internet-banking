export type TipoMovimentacao =
  | 'deposito'
  | 'saque'
  | 'transferencia_enviada'
  | 'transferencia_recebida';

export interface Movimentacao {
  dataHora: string;
  tipo: TipoMovimentacao;
  valor: number;
  contaOrigem?: string;
  contaDestino?: string;
  nomeOrigem?: string;
  nomeDestino?: string;
}