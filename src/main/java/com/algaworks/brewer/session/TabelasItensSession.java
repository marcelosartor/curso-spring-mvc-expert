package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

@Component
@SessionScope
public class TabelasItensSession {

	private Set<TabelaItensVenda> tabelas = new HashSet<>();

	
	public void adicionalItem(String uuid, Cerveja cerveja, int quantidade) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		
		tabela.adicionalItem(cerveja, quantidade);
		tabelas.add(tabela);
	}


	public void alterarQuantidadeItens(String uuid, Cerveja cerveja, Integer quantidade) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.alterarQuantidadeItens(cerveja, quantidade);
		
	}

	public void excluirItem(String uuid,Cerveja cerveja) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.excluirItem(cerveja);
		
	}

	public List<ItemVenda> getItens(String uuid) {
		return buscarTabelaPorUuid(uuid).getItens();
	}
	
	public BigDecimal getValorTotal(String uuid) {
		return buscarTabelaPorUuid(uuid).getValorTotal();
	}
	
	private TabelaItensVenda buscarTabelaPorUuid(String uuid) {
		TabelaItensVenda tabela = 
						tabelas.stream().filter(i->i.getUuid()
										.equals(uuid))
						.findAny()
						.orElse(new TabelaItensVenda(uuid));
		return tabela;
	}


	
}
