package com.algaworks.brewer.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Response;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.dto.PeriodoRelatorio;
import com.algaworks.brewer.service.RelatorioService;

import net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation.Target;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping("/relatorios")
public class RelatoriosController {
	
	@Autowired
	private RelatorioService relatorioService;
	
	@GetMapping("/vendasEmitidas")
	public ModelAndView relatorioVendasEmitidas() {
		ModelAndView mv = new ModelAndView("relatorio/RelatorioVendasEmitidas");
		mv.addObject(new PeriodoRelatorio());
		return mv;
	}
	
	@PostMapping("/vendasEmitidas")
	public ResponseEntity<byte[]> gerarRelatorioVendasEmitidas(PeriodoRelatorio periodoRelatorio) throws SQLException, JRException {
		
		byte[] relarotio = relatorioService.gerarRelatorioVendasEmitidas(periodoRelatorio);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE).body(relarotio);
		
	}

}




