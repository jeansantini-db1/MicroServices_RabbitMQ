package micro.services.mensageria.mscartoes.application;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import micro.services.mensageria.mscartoes.application.representation.CartaoSaveRequest;
import micro.services.mensageria.mscartoes.application.representation.CartoesPorClienteResponse;
import micro.services.mensageria.mscartoes.domain.Cartao;
import micro.services.mensageria.mscartoes.domain.ClienteCartao;

@RestController
@RequestMapping("cartoes")
@RequiredArgsConstructor
public class CartaoController {

    private final CartaoService cartaoService;
    private final ClienteCartaoService clienteCartaoService;

    @GetMapping
    public String status() {
        return "ok";
    }

    @PostMapping
    public ResponseEntity save(@RequestBody CartaoSaveRequest request){
        Cartao cartao = request.toModel();
        cartaoService.save(cartao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam("renda") Long renda){
        return ResponseEntity.ok(cartaoService.getCartoesRendaMenorIgual(renda));
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<List<CartoesPorClienteResponse>> getCartoesByCliente(@RequestParam("cpf") String cpf){
        List<ClienteCartao> lista = clienteCartaoService.listCartoesByCpf(cpf);
        
        List<CartoesPorClienteResponse> resultList = lista.stream()
            .map(CartoesPorClienteResponse::fromModel).collect(Collectors.toList());
        
        return ResponseEntity.ok(resultList);
    }
}
