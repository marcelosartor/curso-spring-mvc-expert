package com.algaworks.brewer.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.repository.listener.CervejaEntityListener;
import com.algaworks.brewer.validation.SKU;

@EntityListeners(CervejaEntityListener.class)
@Entity
@Table(name="cerveja")
public class Cerveja implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long codigo;
	
	//@Pattern(regexp="([a-zA-Z]{2}\\d{4})?",message="O SKU deve seguir o padrão XX9999")
	@SKU
	@NotBlank
	private String sku;
	
	@NotBlank(message="Nome é Obrigatorio")
	private String nome;
		
	@NotBlank(message="A Descrição é Obrigatoria")
	@Size(min = 1, max = 50,message="Descricao deve ter de 1 a 50 caracteres")
	private String descricao;
	
	@NotNull(message="O valor não pode ser nulo")
	@DecimalMin(value="1.01",message="O valor da cerveja deve ser maior que R$ 1,01")	
	@DecimalMax(value="9999999.99",message="O valor da cerveja deve ser menor que R$ 9.999.999,99")
	private BigDecimal valor;
	
	
	@Column(name="teor_alcoolico")
	@NotNull(message=" teor alcoolico da cerveja não pode ser nulo")
	@DecimalMin(value="0.01",message="O teor alcoolico da cerveja deve ser maior que R$ 0,01")
	@DecimalMax(value="100.00",message="O teor alcoolico deve ser menor que 100,00")
	private BigDecimal teorAlcoolico;
	
	
	@DecimalMax(value="100.00",message="A comissão deve ser menor que 100,00")
	private BigDecimal comissao;
	
	@Column(name="quantidade_estoque")
	@Max(value=9999,message="A quandidade de estoque deve ser menor que 9.999")
	private Integer quantidadeEstoque;
	
	@NotNull(message="A origem não pode ser nula")
	@Enumerated(EnumType.STRING)
	private Origem origem;
	
	@NotNull(message="O sabor não pode ser nulo")
	@Enumerated(EnumType.STRING)
	private Sabor sabor;
	
	@NotNull(message="O estilo não pode ser nulo")
	@ManyToOne
	@JoinColumn(name="codigo_estilo")
	private Estilo estilo;
	
	private String foto;
	
	@Column(name="content_type")
	private String contentType;
	
	@Transient
	private boolean novaFoto;
	
	@Transient
	private String urlFoto;
	
	@Transient
	private String urlThumbnailFoto;
	
	@PrePersist @PreUpdate
	private void prePersistUpdate(){
		this.sku = this.sku.toUpperCase();
	}
	
	
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
			
	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public BigDecimal getTeorAlcoolico() {
		return teorAlcoolico;
	}
	public void setTeorAlcoolico(BigDecimal teorAlcoolico) {
		this.teorAlcoolico = teorAlcoolico;
	}
	public BigDecimal getComissao() {
		return comissao;
	}
	public void setComissao(BigDecimal comissao) {
		this.comissao = comissao;
	}
	public Integer getQuantidadeEstoque() {
		return quantidadeEstoque;
	}
	public void setQuantidadeEstoque(Integer quantidadeEstoque) {
		this.quantidadeEstoque = quantidadeEstoque;
	}
	public Origem getOrigem() {
		return origem;
	}
	public void setOrigem(Origem origem) {
		this.origem = origem;
	}
	public Sabor getSabor() {
		return sabor;
	}
	public void setSabor(Sabor sabor) {
		this.sabor = sabor;
	}
	public Estilo getEstilo() {
		return estilo;
	}
	public void setEstilo(Estilo estilo) {
		this.estilo = estilo;
	}
			
	public String getFoto() {
		return foto;
	}
	
	public String getFotoOuMock(){
		return !StringUtils.isEmpty(foto) ? foto : "cerveja-mock.png";
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getContentType() {
		return !StringUtils.isEmpty(foto) ? contentType : "image/png";
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isNova() {
		return this.codigo ==null;
	}

	public boolean isNovaFoto() {
		return novaFoto;
	}


	public void setNovaFoto(boolean novaFoto) {
		this.novaFoto = novaFoto;
	}

	public String getUrlFoto() {
		return urlFoto;
	}
	
	public void setUrlFoto(String urlFoto) {
		this.urlFoto = urlFoto;
	}
	
	public String getUrlThumbnailFoto() {
		return urlThumbnailFoto;
	}

	public void setUrlThumbnailFoto(String urlThumbnailFoto) {
		this.urlThumbnailFoto = urlThumbnailFoto;
	}


	@Override
	public String toString() {
		return "Cerveja [\ncodigo=" + codigo + ", \nsku=" + sku + ", \nnome=" + nome + ", \ndescricao=" + descricao + ", \nvalor=" + valor + ", \nteorAlcoolico="
						+ teorAlcoolico + ", \ncomissao=" + comissao + ", \nquantidadeEstoque=" + quantidadeEstoque + ", \norigem=" + origem + ", \nsabor="
						+ sabor + ", \nestilo=" + estilo + "\n]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cerveja other = (Cerveja) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}
	
	

}
