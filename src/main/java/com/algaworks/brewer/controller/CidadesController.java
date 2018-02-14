package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.repository.filter.EstiloFilter;
import com.algaworks.brewer.service.CadastroCidadeService;
import com.algaworks.brewer.service.exception.CidadeJaCadastradaException;
import com.algaworks.brewer.service.exception.ImplossivelExcluirEntidadeException;

@Controller
@RequestMapping("/cidades")
public class CidadesController {


	@Autowired
	private Estados estados;
	
	@Autowired
	private Cidades cidades;
	
	@Autowired
	private CadastroCidadeService cadastroCidadeService; 
	
	
	
	@PostMapping("/novo")
	@CacheEvict(value="cidades",key="#cidade.estado.codigo", condition="#cidade.temEstado()")
	public ModelAndView cadastrar(@Valid Cidade cidade, BindingResult result, Model model,RedirectAttributes attributes) {
		
		if(result.hasErrors()){
			return novo(cidade);
		}
		try{
			cadastroCidadeService.salvar(cidade);
			attributes.addFlashAttribute("mensagem", "Cidade salva com sucesso");
			return new ModelAndView("redirect:/cidades/novo");
		}catch(CidadeJaCadastradaException e) {
			System.out.println(">>>>> 4");
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(cidade);
		}
	}
 	
	
	@Cacheable(value="cidades",key="#codigoEstado")
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Cidade> pesquisarPorCodigoEstado( @RequestParam(name="estado",defaultValue="-1") Long codigoEstado) {
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cidades.findByEstadoCodigo(codigoEstado);
	}
	

	@GetMapping
	public ModelAndView pesquisar(CidadeFilter cidadeFilter, BindingResult result,@PageableDefault(size=10) Pageable pageable,HttpServletRequest httpServletRequest){

		ModelAndView mv = new ModelAndView("cidade/PesquisaCidades");
		mv.addObject("estados",estados.findAll());
				
		PageWrapper<Cidade> paginaWrapper = new PageWrapper<>(cidades.filtrar(cidadeFilter, pageable),httpServletRequest);
		mv.addObject("pagina",paginaWrapper);
		
		return mv;
	}
	
	@RequestMapping("/novo")
	public ModelAndView novo(Cidade cidade) {
		ModelAndView mv = new ModelAndView("cidade/CadastroCidade");
		mv.addObject("estados",estados.findAll());
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Long codigo){
		Cidade cidade = cidades.buscarComEstados(codigo);
		
		System.out.println(">>> codigo : "+cidade.getCodigo());
		System.out.println(">>> estado : "+cidade.getEstado());
		System.out.println(">>> nome   : "+cidade.getNome());
		
		ModelAndView mv = novo(cidade);
		mv.addObject(cidade);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cidade cidade){
		try {
			cadastroCidadeService.excluir(cidade);
		}catch (ImplossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

}
