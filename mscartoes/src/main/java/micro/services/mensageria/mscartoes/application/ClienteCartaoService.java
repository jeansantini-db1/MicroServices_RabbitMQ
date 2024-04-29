package micro.services.mensageria.mscartoes.application;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import micro.services.mensageria.mscartoes.domain.ClienteCartao;
import micro.services.mensageria.mscartoes.infra.repository.ClienteCartaoRepository;

@Service
@RequiredArgsConstructor
public class ClienteCartaoService {
    private final ClienteCartaoRepository repository;
    
    public List<ClienteCartao> listCartoesByCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

}
