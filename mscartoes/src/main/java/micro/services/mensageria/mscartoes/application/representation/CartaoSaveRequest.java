package micro.services.mensageria.mscartoes.application.representation;

import java.math.BigDecimal;

import lombok.Data;
import micro.services.mensageria.mscartoes.domain.BandeiraCartao;
import micro.services.mensageria.mscartoes.domain.Cartao;

@Data
public class CartaoSaveRequest {
    private String nome;
    private BandeiraCartao bandeira;
    private BigDecimal renda;
    private BigDecimal limite;

    public Cartao toModel() {
        return new Cartao(nome, bandeira, renda, limite);
    }
}
