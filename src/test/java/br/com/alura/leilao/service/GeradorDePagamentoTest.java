package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

class GeradorDePagamentoTest {

    private GeradorDePagamento gerador;
    @Mock
    private PagamentoDao dao;

    @Mock
    private Clock clock;

    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @BeforeEach
    public void beforeEach(){
        MockitoAnnotations.initMocks(this);
        this.gerador = new GeradorDePagamento(dao, clock);
    }

    @Test
    void deveriaCriarPagamentoParaVencedorDoLeilao(){
        Leilao leilao = leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();


        LocalDate data = LocalDate.of(2022,9,23);
        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        gerador.gerarPagamento(lanceVencedor);
        Mockito.verify(dao).salvar(captor.capture()); // captor.capture() captura o objeto passado como parametro

        Pagamento pagamento = captor.getValue(); // captor.getValue() devolve o objeto capturado
        Assertions.assertEquals(LocalDate.now().plusDays(1),pagamento.getVencimento());
        Assertions.assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        Assertions.assertFalse(pagamento.getPago());
        Assertions.assertEquals(pagamento.getUsuario(),lanceVencedor.getUsuario());
        Assertions.assertEquals(leilao,pagamento.getLeilao());
    }

    private Leilao leilao() {
        Leilao leilao = new Leilao("Celular",new BigDecimal("500"), new Usuario("Fulano"));

        Lance lance = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

        leilao.propoe(lance);
        leilao.setLanceVencedor(lance);

        return leilao;
    }

}