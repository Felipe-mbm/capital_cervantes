package domain.conta;

import domain.cliente.Cliente;
import domain.cliente.DadosCadastroCliente;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ContaDAO {

    private Connection conn;

    ContaDAO(Connection connection)  {
        this.conn = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta) {

        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente, BigDecimal.ZERO, true);

        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email, esta_ativa)" +
                "VALUE (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());
            preparedStatement.setBoolean(6, true);

            preparedStatement.execute();
            conn.close();
        } catch (SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listar() {

        Set<Conta> contas = new HashSet<>();
        String sql = "SELECT * FROM conta WHERE esta_ativa = true;";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);
                Boolean estaAtiva = resultSet.getBoolean(6);
                Cliente cliente = new Cliente(new DadosCadastroCliente(nome, cpf, email));

                contas.add(new Conta(numero, cliente, saldo, estaAtiva));
            }

            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro na listagem de contas: " + e);
        }

        return contas;
    }

    public Conta listarNumero(int numeroConta) {

        Conta conta = null;
            String sql = "SELECT * FROM conta WHERE numero= ? AND esta_ativa = true";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroConta);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);
                Boolean estaAtiva = resultSet.getBoolean(6);
                Cliente cliente = new Cliente(new DadosCadastroCliente(nome, cpf, email));

                conta =  new Conta(numero, cliente, saldo, estaAtiva);
            }
            conn.close();
        } catch (SQLException e ) {
            throw new RuntimeException(e);
        }
        return conta;
    }

    public void alterar(Integer numero, BigDecimal valor) {
        String sql = "UPDATE conta SET saldo = ? WHERE numero = ? and esta_ativa = true";

        try (PreparedStatement ps = conn.prepareStatement(sql)){

            conn.setAutoCommit(false);

            ps.setBigDecimal(1, valor);
            ps.setInt(2, numero);

            ps.execute();
            conn.close();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    public void deletar(Integer numeroDaConta) {
        String sql = "DELETE FROM conta  WHERE numero = ? and esta_ativa = true";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroDaConta);

            ps.execute();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void alterarLogico(Integer numeroDaConta) {
        String sql = "UPDATE conta SET esta_ativa = false WHERE numero = ? and esta_ativa = true";

        try (PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, numeroDaConta);

            ps.execute();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
