package service;

import dao.ClienteDAO;
import dao.DaoFactory;
import exception.ClienteNaoEncontradoException;
import exception.CampoInvalidoException;
import model.Cliente;
import repository.RepositoryMemoria;

import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO;

    // Temporário: os pedidos ainda utilizam memória.
    private final RepositoryMemoria repositoryMemoria;

    // Construtor com injeção de dependência.
    public ClienteService(
            ClienteDAO clienteDAO,
            RepositoryMemoria repositoryMemoria) {

        this.clienteDAO = clienteDAO;
        this.repositoryMemoria = repositoryMemoria;
    }

    // Construtor temporário para manter a TelaPrincipal funcionando.
    public ClienteService(RepositoryMemoria repositoryMemoria) {

        this(DaoFactory.getClienteDAO(), repositoryMemoria);
    }

    public void cadastrarCliente(
            String nome, String sobrenome, String telefone) {

        nome = normalizar(nome);
        sobrenome = normalizar(sobrenome);
        telefone = normalizar(telefone);

        validaCampos(nome, sobrenome, telefone);
        validarTelefoneDuplicado(telefone);

        Cliente cliente = new Cliente(
                nome, sobrenome, telefone
        );

        clienteDAO.salvar(cliente);
    }

    public void excluirCliente(int idCliente) {

        clienteDAO.excluir(idCliente);

        // Temporário: remove os pedidos ainda armazenados em memória.
        repositoryMemoria.excluiPedidosCliente(idCliente);
    }

    public void editarCliente(
            int idCliente, String nome,
            String sobrenome, String telefone) {

        nome = normalizar(nome);
        sobrenome = normalizar(sobrenome);
        telefone = normalizar(telefone);

        validaCamposEditar(nome, sobrenome, telefone);

        Cliente cliente = clienteDAO.buscarPorId(idCliente);

        if (!nome.isBlank()) {
            cliente.setNome(nome);
        }

        if (!sobrenome.isBlank()) {
            cliente.setSobrenome(sobrenome);
        }

        if (!telefone.isBlank()) {
            validarTelefoneDuplicadoEditar(idCliente, telefone);
            cliente.setTelefone(telefone);
        }

        clienteDAO.atualizar(cliente);
    }

    public void validaCampos(
            String nome, String sobrenome, String telefone) {

        if (nome == null || nome.isBlank()) {
            throw new CampoInvalidoException(
                    "Nome não pode ser vazio"
            );
        }

        if (sobrenome == null || sobrenome.isBlank()) {
            throw new CampoInvalidoException(
                    "Sobrenome não pode ser vazio"
            );
        }

        if (telefone == null || telefone.isBlank()) {
            throw new CampoInvalidoException(
                    "Telefone não pode ser vazio"
            );
        }

        validarTamanhoNome(nome, sobrenome);
        validarFormatoTelefone(telefone);
    }

    public void validaCamposEditar(
            String nome, String sobrenome, String telefone) {

        nome = normalizar(nome);
        sobrenome = normalizar(sobrenome);
        telefone = normalizar(telefone);

        if (nome.isBlank() && sobrenome.isBlank()
                && telefone.isBlank()) {

            throw new CampoInvalidoException(
                    "Algum campo deve ser atualizado"
            );
        }

        validarTamanhoNome(nome, sobrenome);

        if (!telefone.isBlank()) {
            validarFormatoTelefone(telefone);
        }
    }

    private void validarTamanhoNome(
            String nome, String sobrenome) {

        if (nome.length() > 100) {
            throw new CampoInvalidoException(
                    "Nome deve ter no máximo 100 caracteres"
            );
        }

        if (sobrenome.length() > 100) {
            throw new CampoInvalidoException(
                    "Sobrenome deve ter no máximo 100 caracteres"
            );
        }
    }

    private void validarFormatoTelefone(String telefone) {

        if (!telefone.matches("[0-9]{11}")) {
            throw new CampoInvalidoException(
                    "Telefone deve conter exatamente 11 números"
            );
        }
    }

    public void validarTelefoneDuplicado(String telefone) {

        for (Cliente cliente : clienteDAO.filtrar("", telefone)) {

            if (cliente.getTelefone().equals(telefone)) {

                throw new CampoInvalidoException(
                        "Já existe um cliente com esse telefone"
                );
            }
        }
    }

    public void validarTelefoneDuplicadoEditar(
            int id, String telefone) {

        for (Cliente cliente : clienteDAO.filtrar("", telefone)) {

            if (cliente.getTelefone().equals(telefone)
                    && cliente.getId() != id) {

                throw new CampoInvalidoException(
                        "Já existe um cliente com esse telefone"
                );
            }
        }
    }

    public List<Cliente> mostrarClientes() {
        return clienteDAO.listarTodos();
    }

    public List<Cliente> filtrarClientes(
            String sobrenome, String telefone) {

        sobrenome = normalizar(sobrenome);
        telefone = normalizar(telefone);

        if (sobrenome.isBlank() && telefone.isBlank()) {

            throw new CampoInvalidoException(
                    "Sobrenome e telefone não podem ser vazios"
            );
        }

        List<Cliente> resultado =
                clienteDAO.filtrar(sobrenome, telefone);

        if (resultado.isEmpty()) {

            throw new ClienteNaoEncontradoException(
                    "Nenhum cliente encontrado"
            );
        }

        return resultado;
    }

    public List<Cliente> buscarPorTelefone(String telefone) {

        telefone = normalizar(telefone);

        if (telefone.isBlank()) {

            throw new CampoInvalidoException(
                    "Telefone não pode ser vazio"
            );
        }

        List<Cliente> resultado =
                clienteDAO.filtrar("", telefone);

        if (resultado.isEmpty()) {

            throw new ClienteNaoEncontradoException(
                    "Nenhum cliente encontrado"
            );
        }

        return resultado;
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}