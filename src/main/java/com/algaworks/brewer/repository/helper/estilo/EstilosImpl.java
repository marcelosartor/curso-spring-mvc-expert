package com.algaworks.brewer.repository.helper.estilo;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.repository.filter.EstiloFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class EstilosImpl implements EstilosQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true)
	public Page<Estilo> filtrar(EstiloFilter filtro,Pageable pageable) {
		
		@SuppressWarnings("deprecation")
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Estilo.class);
		
		paginacaoUtil.preparar(criteria, pageable);
			
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
		
	}

	private Long total(EstiloFilter filtro) {
	
			@SuppressWarnings("deprecation")
			Criteria criteria = manager.unwrap(Session.class).createCriteria(Estilo.class);
			adicionarFiltro(filtro, criteria);
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
	}
	
	private void adicionarFiltro(EstiloFilter filtro, Criteria criteria) {
		if(filtro != null){
			if(!StringUtils.isEmpty(filtro.getCodigo())){
				System.out.printf("\n Codigo : %s \n",filtro.getCodigo());
				criteria.add(Restrictions.eq("codigo",filtro.getCodigo() ));
			}	
			if(!StringUtils.isEmpty(filtro.getNome())){
				System.out.printf("\n Nome   : %s \n",filtro.getNome());
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
		}
		
	}
}