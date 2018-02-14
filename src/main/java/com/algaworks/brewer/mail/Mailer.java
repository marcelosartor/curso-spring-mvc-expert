package com.algaworks.brewer.mail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.storage.FotoStorage;

import javassist.bytecode.ByteArray;

@Component
public class Mailer {
	
	private static Logger logger = LoggerFactory.getLogger(Mailer.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	@Autowired
	private FotoStorage fotoStorage;

	@Async
	public void enviar(Venda venda) {
		
		Context context = new Context(new Locale("pt","BR"));
		context.setVariable("venda", venda);
		context.setVariable("logo", "logo");
		
		Map<String, String> fotos = new HashMap<>();
		for(ItemVenda item : venda.getItens()) {
			String cid = "foto-"+item.getCerveja().getCodigo();
			context.setVariable(cid, cid);
			fotos.put(cid, item.getCerveja().getFotoOuMock() +"|" +item.getCerveja().getContentType());
		}
		
		try {
			String email = thymeleaf.process("mail/ResumoVenda", context);
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
			helper.setFrom("marcelo.msartor@gmail.com");
			helper.setTo(venda.getCliente().getEmail());
			helper.setSubject(String.format("Brewer - Venda Nº %d ",venda.getCodigo()));
			helper.setText(email, true);
			
			helper.addInline("logo", new ClassPathResource("static/images/logo-gray.png"));
			
			for(String cid :fotos.keySet()) {
				String[] fotoContentType = fotos.get(cid).split("\\|"); 
				String foto = fotoContentType[0];
				String contentType = fotoContentType[1];
				
				byte[] arrayFoto = fotoStorage.recuperarThumbnail(foto);	
				helper.addInline(cid, new ByteArrayResource(arrayFoto), contentType);
			}
			
			
		
						
			mailSender.send(mimeMessage);
			
		} catch (MessagingException e) {
			logger.error("Erro enviando e-mail", e);
		}
		
		
	
	}
	
	/*
	@Async
	public void enviar(Venda venda) {
		
		// Exemplo de email simples - sem HTML
		SimpleMailMessage mensagem = new SimpleMailMessage();
		mensagem.setFrom("marcelo.msartor@gmail.com");
		mensagem.setTo(venda.getCliente().getEmail());
		mensagem.setSubject("Venda evetuada");
		mensagem.setText("Obrigada pela preferencia");
		mailSender.send(mensagem);
		 
		// teste asyncrono
		//System.out.println(">>>>> Enviando email");
		//try {
		//	Thread.sleep(10000);
		//} catch (InterruptedException e) {
		//	e.printStackTrace();
		//}
		//System.out.println(">>>>> Email enviado");

		
	}
	*/
	
}
