package service;

import dao.SaborDAO;
import exception.CampoInvalidoException;
import exception.SaborInvalidoException;
import model.Sabor;
import model.TipoPizza;

import java.util.List;

public class SaborService {

    private final SaborDAO saborDAO;

    public SaborService(SaborDAO saborDAO) {
        this.saborDAO = saborDAO;
    }

    public void cadastrarSabor(String nome, TipoPizza tipo) {

        nome = normalizar(nome);

        validarCampos(nome, tipo);
        validarSaborDuplicado(nome);

        Sabor sabor = new Sabor(nome, tipo);

        saborDAO.salvar(sabor);
    }

    public void excluirSabor(int idSabor) {

        saborDAO.excluir(idSabor);
    }

    public void editarSabor(
            int idSabor, String nome, TipoPizza tipo) {

        nome = normalizar(nome);

        Sabor sabor = saborDAO.buscarPorId(idSabor);

        if (nome.isBlank()) {
            nome = sabor.getNome();
        }

        validarCampos(nome, tipo);
        validarSaboreDuplicadoEditar(idSabor, nome);

        sabor.setNome(nome);
        sabor.setTipoPizza(tipo);

        saborDAO.atualizar(sabor);
    }

    public List<Sabor> listarSabores() {

        return saborDAO.listarTodos();
    }

    public List<Sabor> listarPorSabor(String nomeSabor) {

        return saborDAO.filtrarPorNome(
                normalizar(nomeSabor)
        );
    }

    public void validarCampos(String nome, TipoPizza tipo) {

        if (nome == null || nome.isBlank()) {

            throw new CampoInvalidoException(
                    "Nome do sabor não pode ser vazio."
            );
        }

        if (nome.length() > 100) {

            throw new CampoInvalidoException(
                    "Nome deve ter no máximo 100 caracteres."
            );
        }

        if (tipo == null) {

            throw new CampoInvalidoException(
                    "O tipo da pizza é obrigatório."
            );
        }
    }

    private void validarSaborDuplicado(String nome) {

        for (Sabor sabor : saborDAO.filtrarPorNome(nome)) {

            if (sabor.getNome().equalsIgnoreCase(nome)) {

                throw new SaborInvalidoException(
                        "Já existe um sabor cadastrado com esse nome."
                );
            }
        }
    }

    public void validarSaboreDuplicadoEditar(
            int idSabor, String nome) {

        for (Sabor sabor : saborDAO.filtrarPorNome(nome)) {

            if (sabor.getNome().equalsIgnoreCase(nome)
                    && sabor.getIdSabor() != idSabor) {

                throw new SaborInvalidoException(
                        "Já existe outro sabor com esse nome."
                );
            }
        }
    }

    private String normalizar(String valor) {

        return valor == null ? "" : valor.trim();
    }
}