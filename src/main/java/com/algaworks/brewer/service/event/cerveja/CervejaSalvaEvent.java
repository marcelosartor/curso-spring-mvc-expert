package com.algaworks.brewer.service.event.cerveja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.storage.FotoStorage;

public class CervejaSalvaEvent {

	private Cerveja cerveja;
		
	public CervejaSalvaEvent(Cerveja cerveja) {
		super();
		this.cerveja = cerveja;
	}

	public Cerveja getCerveja() {
		return cerveja;
	}
	
	public boolean temFoto(){
		return !StringUtils.isEmpty(cerveja.getFoto());
	}
	
	public boolean isNovaFoto() {
		return cerveja.isNovaFoto();
	}

}
