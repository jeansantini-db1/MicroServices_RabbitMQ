package micro.services.mensageria.msavaliadorcredito.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import micro.services.mensageria.msavaliadorcredito.application.exception.DadosClienteNotFoundException;
import micro.services.mensageria.msavaliadorcredito.application.exception.ErroComunicacaoMicroservicesException;
import micro.services.mensageria.msavaliadorcredito.application.exception.ErroSolicitacaoCartaoException;
import micro.services.mensageria.msavaliadorcredito.domain.Cartao;
import micro.services.mensageria.msavaliadorcredito.domain.CartaoCliente;
import micro.services.mensageria.msavaliadorcredito.domain.DadosCliente;
import micro.services.mensageria.msavaliadorcredito.domain.DadosSolicitacaoEmissaoCartao;
import micro.services.mensageria.msavaliadorcredito.domain.ProtocoloSolicitacaoCartao;
import micro.services.mensageria.msavaliadorcredito.domain.RetornoAvaliacaoCliente;
import micro.services.mensageria.msavaliadorcredito.domain.SituacaoCliente;
import micro.services.mensageria.msavaliadorcredito.infra.clients.CartaoResourceClient;
import micro.services.mensageria.msavaliadorcredito.infra.clients.ClienteResourceClient;
import micro.services.mensageria.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clientesClient;
    private final CartaoResourceClient cartoesClient;
    private final SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;

    public SituacaoCliente obterSituacaoCliente(String cpf) 
            throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {
        try{
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> dadosCartaoResponse = cartoesClient.getCartoesByCliente(cpf);
    
            return SituacaoCliente
                .builder()
                .cliente(dadosClienteResponse.getBody())
                .cartoes(dadosCartaoResponse.getBody())
                .build();
        }
        catch (FeignException.FeignClientException e) {
            int status = e.status();
            if(HttpStatus.SC_NOT_FOUND == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }
        catch (FeignException e){
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), e.status());
        }
    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda)
            throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {
        try{
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> dadosCartaoResponse = cartoesClient.getCartoesRendaAte(renda);

            List<Cartao> cartoes = dadosCartaoResponse.getBody();
            DadosCliente dadosCliente = dadosClienteResponse.getBody();

            List<CartaoCliente> cartoesAprovados = cartoes.stream().map(cartao -> {
                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
                BigDecimal fator = idadeBD.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoCliente aprovado = new CartaoCliente();
                aprovado.setNome(cartao.getNome());
                aprovado.setBandeira(cartao.getBandeira());
                aprovado.setLimiteLiberado(limiteAprovado);
                return aprovado;
            }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(cartoesAprovados);

        }
        catch (FeignException.FeignClientException e) {
            int status = e.status();
            if(HttpStatus.SC_NOT_FOUND == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }
        catch (FeignException e){
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), e.status());
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados){
        try{
            emissaoCartaoPublisher.solicitarCartao(dados);
            var protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        }
        catch(Exception e){
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }
}
