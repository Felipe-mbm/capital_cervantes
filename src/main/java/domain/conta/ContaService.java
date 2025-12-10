package domain.conta;

import domain.ConnectionFactory;
import domain.RegraDeNegocioException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Set;

public class ContaService {

    private ConnectionFactory connection;

    public ContaService() {
        this.connection = new ConnectionFactory();
    }

    public Set<Conta> listar() {
        Connection conn = connection.recuperarConexao();
        return new ContaDAO(conn).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        return conta.getSaldo();
    }

    public void abrir(DadosAberturaConta dadosDaConta) {
        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).salvar(dadosDaConta);
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);

        if (valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");

        if (valor.compareTo(conta.getSaldo()) > 0)
            throw new RegraDeNegocioException("Saldo insuficiente!");

        if (!conta.getEstaAtiva())
            throw new RegraDeNegocioException("Essa conta está desativada.");

        BigDecimal novoSaldo = conta.getSaldo().subtract(valor);

        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).alterar(conta.getNumero(), novoSaldo);
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        var conta = buscarContaPorNumero(numeroDaConta);

        if (valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new RegraDeNegocioException("Valor do deposito deve ser superior a zero!");

        BigDecimal novoSaldo = conta.getSaldo().add(valor);

        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).alterar(conta.getNumero(), novoSaldo);
    }

    public void realizarTransferencia(Integer numeroDaContaOrigem, Integer numeroDaContaDestino, BigDecimal valor) {
        buscarContaPorNumero(numeroDaContaDestino);
        buscarContaPorNumero(numeroDaContaOrigem);

        this.realizarSaque(numeroDaContaOrigem, valor);
        this.realizarDeposito(numeroDaContaDestino, valor);
    }

    public void encerrar(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo())
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");

        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).deletar(numeroDaConta);
    }

    public void encerrarLogico(Integer numeroDaConta) {
        var conta = buscarContaPorNumero(numeroDaConta);
        if (conta.possuiSaldo())
            throw new RegraDeNegocioException("Conta não pode ser encerrada pois ainda possui saldo!");

        Connection conn = connection.recuperarConexao();
        new ContaDAO(conn).alterarLogico(numeroDaConta);
    }

    public Conta buscarContaPorNumero(Integer numero) {
        Connection conn = connection.recuperarConexao();
        Conta conta = new ContaDAO(conn).listarNumero(numero);
        if (conta == null)
            throw new RegraDeNegocioException("Não existe conta cadastrada com este número");
        return conta;
    }
}
