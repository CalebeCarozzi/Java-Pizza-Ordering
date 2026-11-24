package service;

import dao.ClienteDAO;
import exception.CampoInvalidoException;
import exception.ClienteNaoEncontradoException;
import model.Cliente;

import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService(ClienteDAO clienteDAO) {

        this.clienteDAO = clienteDAO;
    }

    // ================= CADASTRAR =================

    public void cadastrarCliente(
            String nome, String sobrenome, String telefone) {

        nome = normalizar(nome);
        sobrenome = normalizar(sobrenome);
        telefone = normalizar(telefone);

        validarCampos(nome, sobrenome, telefone);

        validarTelefoneDuplicado(telefone, 0);

        Cliente cliente = new Cliente(
                nome, sobrenome, telefone
        );

        clienteDAO.salvar(cliente);
    }

    // ================= EDITAR =================

    public void editarCliente(
            int id, String nome,
            String sobrenome, String telefone) {

        nome = normalizar(nome);
        sobrenome = normalizar(sobrenome);
        telefone = normalizar(telefone);

        if (nome.isBlank() && sobrenome.isBlank()
                && telefone.isBlank()) {

            throw new CampoInvalidoException(
                    "Informe pelo menos um campo para editar."
            );
        }

        Cliente cliente = clienteDAO.buscarPorId(id);

        if (nome.isBlank()) {
            nome = cliente.getNome();
        }

        if (sobrenome.isBlank()) {
            sobrenome = cliente.getSobrenome();
        }

        if (telefone.isBlank()) {
            telefone = cliente.getTelefone();
        }

        validarCampos(nome, sobrenome, telefone);

        validarTelefoneDuplicado(telefone, id);

        cliente.setNome(nome);
        cliente.setSobrenome(sobrenome);
        cliente.setTelefone(telefone);

        clienteDAO.atualizar(cliente);
    }

    // ================= EXCLUIR =================

    public void excluirCliente(int id) {

        clienteDAO.excluir(id);
    }

    // ================= CONSULTAS =================

    public List<Cliente> mostrarClientes() {

        return clienteDAO.listarTodos();
    }

    public List<Cliente> filtrarClientes(
            String sobrenome, String telefone) {

        telefone = normalizar(telefone);

        if (!telefone.isBlank()
                && !telefone.matches("[0-9]{1,11}")) {

            throw new CampoInvalidoException(
                    "Filtro de telefone deve conter até 11 números."
            );
        }

        return clienteDAO.filtrar(
                normalizar(sobrenome), telefone
        );
    }

    public List<Cliente> buscarPorTelefone(String telefone) {

        telefone = normalizar(telefone);

        if (telefone.isBlank()
                || !telefone.matches("[0-9]{1,11}")) {

            throw new CampoInvalidoException(
                    "Informe um telefone ou parte dele, somente números."
            );
        }

        List<Cliente> clientes = clienteDAO.filtrar(
                "", telefone
        );

        if (clientes.isEmpty()) {

            throw new ClienteNaoEncontradoException(
                    "Nenhum cliente encontrado."
            );
        }

        return clientes;
    }

    // ================= VALIDAÇÕES =================

    private void validarCampos(
            String nome, String sobrenome, String telefone) {

        if (nome.isBlank() || sobrenome.isBlank()) {

            throw new CampoInvalidoException(
                    "Nome e sobrenome são obrigatórios."
            );
        }

        if (nome.length() > 100 || sobrenome.length() > 100) {

            throw new CampoInvalidoException(
                    "Nome e sobrenome devem ter até 100 caracteres."
            );
        }

        if (!telefone.matches("[0-9]{11}")) {

            throw new CampoInvalidoException(
                    "Telefone deve conter exatamente 11 números."
            );
        }
    }

    private void validarTelefoneDuplicado(
            String telefone, int idIgnorado) {

        for (Cliente cliente : clienteDAO.filtrar("", telefone)) {

            if (cliente.getId() != idIgnorado
                    && cliente.getTelefone().equals(telefone)) {

                throw new CampoInvalidoException(
                        "Já existe um cliente com esse telefone."
                );
            }
        }
    }

    private String normalizar(String texto) {

        return texto == null ? "" : texto.trim();
    }
}