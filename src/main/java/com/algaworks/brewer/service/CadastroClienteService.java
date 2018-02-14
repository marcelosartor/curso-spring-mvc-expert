package com.algaworks.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.service.exception.CpfCnpjClienteJaCadastradoException;
import com.algaworks.brewer.service.exception.ImplossivelExcluirEntidadeException;

@Service
public class CadastroClienteService {

	@Autowired
	private Clientes clientes;
		
	@Transactional
	public void salvar(Cliente cliente){
		
		Optional<Cliente> clienteExistente = clientes.findByCpfOuCnpj(cliente.getCpfOuCnpjSemFormatacao());
		if(clienteExistente.isPresent() && !clienteExistente.get().equals(cliente)){
			throw new CpfCnpjClienteJaCadastradoException("CPF/CNPJ já cadastrado");
		}
		clientes.save(cliente);
	}

	@Transactional
	public void excluir(Cliente cliente) {
		try {
			clientes.delete(cliente);
			clientes.flush();
		}catch(PersistenceException e) {
			throw new ImplossivelExcluirEntidadeException("Impossível apagar o cliente! Já foi utilizado em algum cadastro.");
		//}catch(Exception e) {
		//	e.printStackTrace();
		//	throw new ImplossivelExcluirEntidadeException("Impossível apagar o cliente! Já foi utilizado em algum cadastro......");
		}
			
	}
	
	
	
}
