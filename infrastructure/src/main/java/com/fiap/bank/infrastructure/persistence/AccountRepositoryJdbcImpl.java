package com.fiap.bank.infrastructure.persistence;

// Imports do módulo domain
import com.fiap.bank.atm.domain.entity.Account;
import com.fiap.bank.atm.domain.repository.AccountRepository;

// Imports infraestrutura e do Java SQL
import com.fiap.bank.atm.infrastructure.database.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AccountRepositoryJdbcImpl implements AccountRepository {

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT id, account_number, balance FROM tb_account WHERE account_number = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Exclusivamente PreparedStatement!
             
            stmt.setString(1, accountNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mapeamento do ResultSet para a Entidade de Domínio
                    Account account = new Account(
                        rs.getLong("id"),
                        rs.getString("account_number"),
                        rs.getDouble("balance")
                    );
                    return Optional.of(account);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar conta por número no SQLite", e);
        }
        
        return Optional.empty(); // Zero null, como manda a regra!
    }

    @Override
    public void save(Account account) {
        String sql = "INSERT INTO tb_account (account_number, balance) VALUES (?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, account.getAccountNumber());
            stmt.setDouble(2, account.getBalance());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar conta no SQLite", e);
        }
    }

    @Override
    public void update(Account account) {
        String sql = "UPDATE tb_account SET balance = ? WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setDouble(1, account.getBalance());
            stmt.setLong(2, account.getId());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar conta no SQLite", e);
        }
    }
}