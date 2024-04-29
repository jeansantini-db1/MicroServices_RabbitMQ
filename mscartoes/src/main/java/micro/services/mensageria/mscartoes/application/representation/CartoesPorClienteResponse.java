package micro.services.mensageria.mscartoes.application.representation;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import micro.services.mensageria.mscartoes.domain.ClienteCartao;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartoesPorClienteResponse {
    private String nome;
    private String bandeira;
    private BigDecimal limiteLiberado;

    public static CartoesPorClienteResponse fromModel(ClienteCartao model){
        return new CartoesPorClienteResponse(model.getCartao().getNome(), 
            model.getCartao().getBandeira().toString(), model.getLimite());
    }
}
