package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.Origem;
import com.algaworks.brewer.model.Sabor;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.Estilos;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.repository.filter.ClienteFilter;
import com.algaworks.brewer.service.CadastroCervejaService;
import com.algaworks.brewer.service.CadastroClienteService;
import com.algaworks.brewer.service.exception.CpfCnpjClienteJaCadastradoException;
import com.algaworks.brewer.service.exception.ImplossivelExcluirEntidadeException;

@Controller
@RequestMapping("/clientes")
public class ClientesController {

	
	@Autowired
	private Clientes clientes;

	@Autowired
	private Estados estados;
	
	@Autowired
	private CadastroClienteService cadastroClienteService;
	
			
	@RequestMapping("/novo")
	public ModelAndView novo(Cliente cliente) {
		ModelAndView mv = new ModelAndView("cliente/CadastroCliente");
						
		mv.addObject("tiposPessoa",TipoPessoa.values());
		mv.addObject("estados",estados.findAll());
		return mv;
	}
	
	//@RequestMapping(value="/novo", method=RequestMethod.POST)
	@PostMapping({"/novo","{\\+d}"})
	public ModelAndView cadastrar(@Valid Cliente cliente, BindingResult result, Model model,RedirectAttributes attributes) {

		if(result.hasErrors()){
			return novo(cliente);
		}
		try{
			cadastroClienteService.salvar(cliente);
			attributes.addFlashAttribute("mensagem", "Cliente salvo com sucesso");
			return new ModelAndView("redirect:/clientes/novo");
		}catch(CpfCnpjClienteJaCadastradoException e){
			result.rejectValue("cpfOuCnpj", e.getMessage(), e.getMessage());
			return novo(cliente);
		}
		
		
	}
	
	
	@GetMapping
	public ModelAndView pesquisar(ClienteFilter clienteFilter, BindingResult result, @PageableDefault(size=10) Pageable pageable, HttpServletRequest httpServletRequest){
		
		ModelAndView mv = new ModelAndView("cliente/PesquisaClientes");
		PageWrapper<Cliente> paginaWrapper = new PageWrapper<>(clientes.filtrar(clienteFilter,pageable),httpServletRequest);
		
		
		
		mv.addObject("pagina",paginaWrapper);
		//mv.addObject("cervejas",cervejas.findAll(pageable));
		return mv;
	}
	
	@RequestMapping(consumes= {MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody List<Cliente> pesquisa(String nome){
		
		validarTamanhoNome(nome);
		
		return clientes.findByNomeStartingWithIgnoreCase(nome);
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Long codigo){
		Cliente cliente = clientes.buscarComCidade(codigo);
		cliente.getEndereco().setEstado(cliente.getEndereco().getCidade().getEstado());
		System.out.println(">>> codigo : "+cliente.getCodigo());
		//System.out.println(">>> Cidade : "+cliente.getEndereco().getCidade() == null ? "null" : cliente.getEndereco().getCidade().getNome());
		//System.out.println(">>> Estado : "+cliente.getEndereco().getCidade() == null ? "null" : cliente.getEndereco().getCidade().getEstado().getSigla());
		
		ModelAndView mv = novo(cliente);
		mv.addObject(cliente);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cliente cliente){
		try {
			cadastroClienteService.excluir(cliente);
		}catch (ImplossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}


	private void validarTamanhoNome(String nome) {
		if(StringUtils.isEmpty(nome) || nome.length()<3) {
			throw new IllegalArgumentException();
		}
		
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> tratarIllegalArgumentException(IllegalArgumentException e){
		return ResponseEntity.badRequest().build();
	}
 

}
