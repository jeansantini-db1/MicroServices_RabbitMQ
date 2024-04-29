package micro.services.mensageria.msavaliadorcredito.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import micro.services.mensageria.msavaliadorcredito.application.exception.DadosClienteNotFoundException;
import micro.services.mensageria.msavaliadorcredito.application.exception.ErroComunicacaoMicroservicesException;
import micro.services.mensageria.msavaliadorcredito.application.exception.ErroSolicitacaoCartaoException;
import micro.services.mensageria.msavaliadorcredito.domain.DadosAvaliacao;
import micro.services.mensageria.msavaliadorcredito.domain.DadosSolicitacaoEmissaoCartao;
import micro.services.mensageria.msavaliadorcredito.domain.ProtocoloSolicitacaoCartao;
import micro.services.mensageria.msavaliadorcredito.domain.RetornoAvaliacaoCliente;
import micro.services.mensageria.msavaliadorcredito.domain.SituacaoCliente;

@RestController
@RequestMapping("avaliacoes-credito")
@RequiredArgsConstructor
public class AvaliadorCreditoController {

    private final AvaliadorCreditoService service;

    @GetMapping
    public String status() {
        return "ok";
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity consultaSituacaoCliente(@RequestParam("cpf") String cpf) {
        try{
            SituacaoCliente situacaoCliente = service.obterSituacaoCliente(cpf);
            return ResponseEntity.ok(situacaoCliente);
        } catch (DadosClienteNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoMicroservicesException e){
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus()))
            .body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity realizarAvaliacao(@RequestBody DadosAvaliacao dados) {
        try{
            RetornoAvaliacaoCliente retorno = service.realizarAvaliacao(dados.getCpf(), dados.getRenda());
            return ResponseEntity.ok(retorno);
        } catch (DadosClienteNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoMicroservicesException e){
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus()))
            .body(e.getMessage());
        }
    }

    @PostMapping("solicitacoes-cartao")
    public ResponseEntity solicitarCartao(@RequestBody DadosSolicitacaoEmissaoCartao dados){
        try{
            ProtocoloSolicitacaoCartao protocolo = service.solicitarEmissaoCartao(dados);
            return ResponseEntity.ok(protocolo);
        } catch (ErroSolicitacaoCartaoException e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
