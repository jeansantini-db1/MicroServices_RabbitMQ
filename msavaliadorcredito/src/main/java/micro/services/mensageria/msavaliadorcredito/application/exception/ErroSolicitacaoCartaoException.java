package micro.services.mensageria.msavaliadorcredito.application.exception;

public class ErroSolicitacaoCartaoException extends RuntimeException {
    public ErroSolicitacaoCartaoException(String message) {
        super(message);
    }
}
