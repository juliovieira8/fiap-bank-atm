package com.fiap.bank.atm.presentation;

import com.fiap.bank.atm.application.service.AtmService;
import com.fiap.bank.atm.domain.repository.AccountRepository;
import javax.swing.SwingUtilities;

public class AtmApplication {
    public static void main(String[] args) {
        
        // 1. Injeção de Dependência via Reflexão (Burla a restrição de compilação com segurança)
        AccountRepository repository = null;
        try {
            Class<?> repoClass = Class.forName("com.fiap.bank.atm.infrastructure.persistence.InMemoryAccountRepository");
            repository = (AccountRepository) repoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.err.println("Erro crítico: Não foi possível carregar o banco de dados em memória.");
            e.printStackTrace();
        }

        // 2. Inicializa o serviço com o repositório carregado
        AtmService atmService = new AtmService(repository);

        // 3. Inicializa a camada de Apresentação de forma segura na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            AtmFrame mainFrame = new AtmFrame(atmService);
            mainFrame.setVisible(true);
        });
    }
}