package controller;

import exception.CampoInvalidoException;
import exception.ClienteNaoEncontradoException;
import exception.NenhumaLinhaSelecionadaException;
import exception.PersistenciaException;
import service.ClienteService;
import view.TelaCliente;

import java.util.List;

public class ClienteController {

    private final ClienteService clienteService;
    private final TelaCliente tela;

    public ClienteController(
            ClienteService clienteService,
            TelaCliente tela) {

        this.clienteService = clienteService;
        this.tela = tela;
    }

    // Listar clientes
    public void carregarClientes() {

        try {
            tela.exibirClientes(
                    clienteService.mostrarClientes()
            );

        } catch (PersistenciaException e) {
            tratarErroPersistencia(e);
        }
    }

    // Cadastrar cliente
    public void cadastrarCliente() {

        try {
            clienteService.cadastrarCliente(
                    tela.getNome(),
                    tela.getSobrenome(),
                    tela.getTelefone()
            );

        } catch (CampoInvalidoException e) {
            tela.exibirErro(e.getMessage());
            return;

        } catch (PersistenciaException e) {
            tratarErroPersistencia(e);
            return;
        }

        atualizarTelaAposOperacao();

        tela.exibirMensagem(
                "Cliente cadastrado com sucesso."
        );
    }

    // Atualizar cliente
    public void atualizarCliente() {

        try {
            int id = tela.obterIdSelecionado();

            clienteService.editarCliente(
                    id,
                    tela.getNome(),
                    tela.getSobrenome(),
                    tela.getTelefone()
            );

        } catch (CampoInvalidoException |
                 NenhumaLinhaSelecionadaException |
                 ClienteNaoEncontradoException e) {

            tela.exibirErro(e.getMessage());
            return;

        } catch (PersistenciaException e) {
            tratarErroPersistencia(e);
            return;
        }

        atualizarTelaAposOperacao();

        tela.exibirMensagem(
                "Cliente atualizado com sucesso."
        );
    }

    // Excluir cliente
    public void excluirCliente() {

        try {
            int id = tela.obterIdSelecionado();

            if (!tela.confirmarExclusao()) {
                return;
            }

            clienteService.excluirCliente(id);

        } catch (CampoInvalidoException |
                 NenhumaLinhaSelecionadaException |
                 ClienteNaoEncontradoException e) {

            tela.exibirErro(e.getMessage());
            return;

        } catch (PersistenciaException e) {
            tratarErroPersistencia(e);
            return;
        }

        atualizarTelaAposOperacao();

        tela.exibirMensagem(
                "Cliente excluído com sucesso."
        );
    }

    // Filtrar clientes
    public void filtrarClientes() {

        try {
            tela.exibirClientes(
                    clienteService.filtrarClientes(
                            tela.getFiltroSobrenome(),
                            tela.getFiltroTelefone()
                    )
            );

        } catch (ClienteNaoEncontradoException e) {

            tela.exibirClientes(List.of());
            tela.exibirErro(e.getMessage());

        } catch (CampoInvalidoException e) {

            tela.exibirErro(e.getMessage());

        } catch (PersistenciaException e) {

            tratarErroPersistencia(e);
        }
    }

    // Limpar filtros
    public void limparFiltros() {

        tela.limparCamposFiltro();
        carregarClientes();
    }

    // Atualizar os componentes após cadastrar,
    // editar ou excluir um cliente
    private void atualizarTelaAposOperacao() {

        tela.limparCampos();
        tela.limparCamposFiltro();

        carregarClientes();
    }

    // Tratamento dos erros de banco de dados
    private void tratarErroPersistencia(
            PersistenciaException e) {

        e.printStackTrace();

        tela.exibirErro(e.getMessage());
    }
}