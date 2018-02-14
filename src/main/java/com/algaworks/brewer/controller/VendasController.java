package com.algaworks.brewer.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.CadastroVendaService;
import com.algaworks.brewer.session.TabelasItensSession;

@Controller
@RequestMapping("/vendas")
public class VendasController {
	
	@Autowired
	private Cervejas cervejas;
	
	
	@Autowired
	private TabelasItensSession tabelaItens;
	
	@Autowired
	private CadastroVendaService cadastroVendaService;
	
	@Autowired
	private VendaValidator vendaValidator;
	
	@Autowired
	private Vendas vendas;
	
	@Autowired
	private Mailer mailer;
	
	@InitBinder("venda")
	public void inicializarValidador(WebDataBinder binder) {
		binder.setValidator(vendaValidator);
		
	}
	
	@GetMapping("/nova")
	public ModelAndView nova(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
		
		setUuid(venda);
				
		mv.addObject("itens",venda.getItens());	
		mv.addObject("valorFrete",venda.getValorFrete());
		mv.addObject("valorDesconto",venda.getValorDesconto());
		mv.addObject("valorTotalItens",tabelaItens.getValorTotal(venda.getUuid()));
		
		
		
		return mv;
	}
	
	@PostMapping("/item")
	public ModelAndView adcionarItem(Long codigoCerveja, String uuid) {
		
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		
		tabelaItens.adicionalItem(uuid,cerveja, 1);
		
		return mvTabelaItensVenda(uuid);
	}
	
	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuandidadeItem(@PathVariable Long codigoCerveja,Integer quantidade, String uuid) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItens.alterarQuantidadeItens(uuid,cerveja, quantidade);
		
		return mvTabelaItensVenda(uuid);
	}
	
	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja, @PathVariable String uuid) {
		//Cerveja cerveja = cervejas.findOne(codigoCerveja);
		/*
		 * Para automatizar o findOne deve-se criar o bean  DomainClassConverter no WebConfig 
		 * e substituir o excluirItem(@PathVariable Long codigoCerveja)
		 * por excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja)
		 * e a conversao será automatica.
		 * */
		tabelaItens.excluirItem(uuid,cerveja);
		
		return mvTabelaItensVenda(uuid);
		
	}

	
	
	
	@PostMapping(value="/nova",params="salvar")
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		validarVenda(venda, result);
		if(result.hasErrors()) {
			return nova(venda);
		}
		
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem","Venda salva com sucesso");
		return new ModelAndView("redirect:/vendas/nova");
	}

	@PostMapping(value="/nova",params="emitir")
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		validarVenda(venda, result);
		
		if(result.hasErrors()) {
			return nova(venda);
		}
		
		
		venda.setUsuario(usuarioSistema.getUsuario());
		
		cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem","Venda salva com sucesso");
		return new ModelAndView("redirect:/vendas/nova");
	}

	
	@PostMapping(value="/nova",params="enviarEmail")
	public ModelAndView enviarEmail(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		
		validarVenda(venda, result);
		if(result.hasErrors()) {
			return nova(venda);
		}
		venda.setUsuario(usuarioSistema.getUsuario());
		
		venda = cadastroVendaService.salvar(venda);
		
		mailer.enviar(venda);
		//System.out.println("##### Apos chamar o enviar");
		
		attributes.addFlashAttribute("mensagem",String.format("Venda nº %d salva e enviado por email com sucesso",venda.getCodigo()));
		return new ModelAndView("redirect:/vendas/nova");
	}
	
	@GetMapping
	public ModelAndView pesquisar(VendaFilter vendaFilter,
			@PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("venda/PesquisaVendas");
		mv.addObject("todosStatus", StatusVenda.values());
		mv.addObject("tiposPessoa", TipoPessoa.values());
		
		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendas.filtrar(vendaFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Venda venda = vendas.buscarComItens(codigo);
		venda.setDataEntrega(venda.getDataHoraEntrega().toLocalDate());
		venda.setHorarioEntrega(venda.getDataHoraEntrega().toLocalTime());
		
		
		setUuid(venda);
		for(ItemVenda item : venda.getItens()) {
			tabelaItens.adicionalItem(venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}
		
		ModelAndView mv = nova(venda);
		mv.addObject(venda);
		return mv;
		
	}
	
	@PostMapping(value="/nova",params="cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		try {
			cadastroVendaService.cancelar(venda);
		}catch(AccessDeniedException e) {
			return new ModelAndView("403");
		}
		attributes.addFlashAttribute("mensagem","Venda cancelada com sucesso");
		return new ModelAndView("redirect:/vendas/"+venda.getCodigo());
	}
	
	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMes> listarTotalVendaPorMes(){
		return vendas.totalPorMes();
	}
	
	@GetMapping("/totalPorOrigemMes")
	public @ResponseBody List<VendaOrigem> listarTotalVendaPorOrigemMes(){
		return vendas.totalPorOrigem();
	}
	

	private void validarVenda(Venda venda, BindingResult result) {
		venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));
		venda.calcularValorTotal();
		vendaValidator.validate(venda, result);
	}
	
	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		return mv;
	}
	
	private void setUuid(Venda venda) {
		if(StringUtils.isEmpty(venda.getUuid())) {
			venda.setUuid(UUID.randomUUID().toString());
		}
	}
	

}