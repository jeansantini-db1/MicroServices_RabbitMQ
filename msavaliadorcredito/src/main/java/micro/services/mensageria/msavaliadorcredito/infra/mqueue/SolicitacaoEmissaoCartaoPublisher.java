package micro.services.mensageria.msavaliadorcredito.infra.mqueue;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import micro.services.mensageria.msavaliadorcredito.domain.DadosSolicitacaoEmissaoCartao;

@Component
@RequiredArgsConstructor
public class SolicitacaoEmissaoCartaoPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final Queue queueEmissaoCartoes;

    public void solicitarCartao(DadosSolicitacaoEmissaoCartao dados) throws JsonProcessingException{
        String json = convertIntoJson(dados);
        rabbitTemplate.convertAndSend(queueEmissaoCartoes.getName(), json);
    }

    private String convertIntoJson(DadosSolicitacaoEmissaoCartao dados) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dados);
        return json;
    }

}
