package com.algaworks.brewer.session;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.session.TabelaItensVenda;

public class TabelaItensVendaTest {

	private TabelaItensVenda tabelaItensVenda;
	
	@Before
	public void setUp() {
		this.tabelaItensVenda = new TabelaItensVenda	("1");
	}

	
	@Test
	public void deveCalcularValorTotalSemItens() throws Exception {
		assertEquals( BigDecimal.ZERO ,tabelaItensVenda.getValorTotal());
		
	}	
	
	@Test
	public void deveCalcularValorTotalComUmItem() throws Exception {
		Cerveja cerveja = new Cerveja();
		BigDecimal valor = new BigDecimal(8.9);
		cerveja.setValor(valor);
		this.tabelaItensVenda.adicionalItem(cerveja, 1);
		
		assertEquals( valor ,tabelaItensVenda.getValorTotal());
		
	}
	
	@Test
	public void deveCalcularValorTotalComVariosItens() throws Exception {
		Cerveja c1 = new Cerveja();
		BigDecimal v1 = new BigDecimal("8.9");
		c1.setCodigo(1L);
		c1.setValor(v1);
		
		Cerveja c2 = new Cerveja();
		BigDecimal v2 = new BigDecimal("4.99");
		c2.setValor(v2);
		c2.setCodigo(2L);
		
		this.tabelaItensVenda.adicionalItem(c1, 1);
		this.tabelaItensVenda.adicionalItem(c2, 2);
		
		assertEquals( new BigDecimal("18.88") ,tabelaItensVenda.getValorTotal());
		
	}
	
	
	@Test
	public void deveManterTamanhoDaListaParaMesmasCervejas() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setCodigo(1l);
		c1.setValor(new BigDecimal("10.5"));
		
		tabelaItensVenda.adicionalItem(c1, 1);
		tabelaItensVenda.adicionalItem(c1, 1);
		
		assertEquals(1, tabelaItensVenda.total());
		assertEquals(new BigDecimal("21.0"), tabelaItensVenda.getValorTotal());
	}
	
	
	@Test
	public void deveAlterarQuantidadeDoItem() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setCodigo(1l);
		c1.setValor(new BigDecimal("4.5"));
		
		tabelaItensVenda.adicionalItem(c1, 1);
		
		tabelaItensVenda.alterarQuantidadeItens(c1, 3);
		
		assertEquals(new BigDecimal("13.5"), tabelaItensVenda.getValorTotal());
		
	}
	
	@Test
	public void deveExcluirItem() throws Exception {
		Cerveja c1 = new Cerveja();
		BigDecimal v1 = new BigDecimal("8.9");
		c1.setCodigo(1L);
		c1.setValor(v1);
		
		Cerveja c2 = new Cerveja();
		BigDecimal v2 = new BigDecimal("4.99");
		c2.setValor(v2);
		c2.setCodigo(2L);
		
		Cerveja c3= new Cerveja();
		BigDecimal v3= new BigDecimal("2.00");
		c3.setValor(v3);
		c3.setCodigo(3l);
		
		this.tabelaItensVenda.adicionalItem(c1, 1);
		this.tabelaItensVenda.adicionalItem(c2, 2);
		this.tabelaItensVenda.adicionalItem(c3, 1);
		
		assertEquals( new BigDecimal("20.88") ,tabelaItensVenda.getValorTotal());
		assertEquals(3, tabelaItensVenda.total());
		
		tabelaItensVenda.excluirItem(c3);
		
		assertEquals( new BigDecimal("18.88") ,tabelaItensVenda.getValorTotal());
		assertEquals(2, tabelaItensVenda.total());
		
		
	}
}
