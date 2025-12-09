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
        var conta = new Conta(dadosDaConta.numero(), cliente, BigDecimal.ZERO);

        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email)" +
                "VALUE (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());

            preparedStatement.execute();

            preparedStatement.execute();
        } catch (SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listar() {

        Set<Conta> contas = new HashSet<>();
        String sql = "SELECT * FROM conta;";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);
                Cliente cliente = new Cliente(new DadosCadastroCliente(nome, cpf, email));

                contas.add(new Conta(numero, cliente, saldo));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro na listagem de contas: " + e);
        }

        return contas;
    }

    public Conta listarNumero(int numeroConta) {

        Conta conta = null;
            String sql = "SELECT * FROM conta WHERE numero= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroConta);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Integer numero = resultSet.getInt(1);
                BigDecimal saldo = resultSet.getBigDecimal(2);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);
                Cliente cliente = new Cliente(new DadosCadastroCliente(nome, cpf, email));

                conta =  new Conta(numero, cliente, saldo);
            }

        } catch (SQLException e ) {
            throw new RuntimeException(e);
        }
        return conta;
    }

}
