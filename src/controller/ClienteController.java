package controller;

import model.Cliente;
import service.ClienteService;

import java.util.List;

public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public void cadastrarCliente(
            String nome,
            String sobrenome,
            String telefone) {

        clienteService.cadastrarCliente(
                nome, sobrenome, telefone
        );
    }

    public void atualizarCliente(
            int id,
            String nome,
            String sobrenome,
            String telefone) {

        clienteService.editarCliente(
                id, nome, sobrenome, telefone
        );
    }

    public void excluirCliente(int id) {
        clienteService.excluirCliente(id);
    }

    public List<Cliente> listarClientes() {
        return clienteService.mostrarClientes();
    }

    public List<Cliente> filtrarClientes(
            String sobrenome, String telefone) {

        return clienteService.filtrarClientes(
                sobrenome, telefone
        );
    }

    public List<Cliente> buscarPorTelefone(String telefone) {
        return clienteService.buscarPorTelefone(telefone);
    }
}