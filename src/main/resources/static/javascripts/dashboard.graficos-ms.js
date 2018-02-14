var Brewer = Brewer || {};

Brewer.GraficoVendasPorMes = (function(){
	
	function GraficoVendasPorMes(){
		this.ctx = $('#graficoVendasPorMes')[0].getContext('2d');
	}
	
	GraficoVendasPorMes.prototype.iniciar = function(){
		$.ajax({
			url : 'vendas/totalPorMes',
			method : 'GET',
			success : onDadosRecebidos.bind(this)
		});
		
	}
	
	function onDadosRecebidos(vendaMes){
		
		var labelsEixoX = []; //['Jan','Fev','Mar','Abr','Mai','Jun','Jul','Ago','Set','Out','Nov','Dez'];
		
		var valores = []; //[100,20,30,3,15,27,4,16,32,5,10,15];
		
		vendaMes.forEach(function(obj){
			labelsEixoX.unshift(obj.mes);
			valores.unshift(obj.total); 
		});
		
		var data = {
				labels   :  labelsEixoX ,
				datasets : [{
							  label            : 'Vendas por mês',
							  backgroundColor  : "rgba(26,179,148,0.5)",
						      pointBorderColor : "rgba(26,179,148,1)",
						      pointBackgroundColor : "#fff",
						      data	: valores
							}] 
			}; 
						
		var graficoDeVendasPorMes = new Chart(this.ctx, {
		    type: 'line',
		    data: data,
		});
	}
	
	
	return GraficoVendasPorMes;
}());


Brewer.GraficoVendasPorOrigemMes = (function(){
	
	function GraficoVendasPorOrigemMes(){
		this.ctx = $('#graficoVendasPorOrigemMes')[0].getContext('2d');
	}
	
	GraficoVendasPorOrigemMes.prototype.iniciar = function(){
		$.ajax({
			url : 'vendas/totalPorOrigemMes',
			method : 'GET',
			success : onDadosRecebidos.bind(this)
		});
		
	}
	
	function onDadosRecebidos(vendaMes){
		
		var labelsEixoX = []; //['Jan','Fev','Mar','Abr','Mai','Jun','Jul','Ago','Set','Out','Nov','Dez'];
		
		var valoresNacional = []; //[100,20,30,3,15,27,4,16,32,5,10,15];
		var valoresInternacional = []; //[100,20,30,3,15,27,4,16,32,5,10,15];
		
		vendaMes.forEach(function(obj){
			labelsEixoX.unshift(obj.mes);
			valoresNacional.unshift(obj.totalNacional);
			valoresInternacional.unshift(obj.totalInternacional);
		});
		
		var data = {
				labels   :  labelsEixoX ,
				datasets : [
							{
							  label            : 'Nacional',
							  backgroundColor  : "rgba(26,179,148,0.5)",
						      pointBorderColor : "rgba(26,179,148,1)",
						      pointBackgroundColor : "#fff",
						      data	: valoresNacional
							},
							{
								  label            : 'Internacional',
								  backgroundColor  : "rgba(20,100,148,0.5)",
							      pointBorderColor : "rgba(20,100,148,1)",
							      pointBackgroundColor : "#fff",
							      data	: valoresInternacional
							}
							] 
			}; 
						
		var graficoDeVendasPorOrigemMes = new Chart(this.ctx, {
		    type: 'bar',
		    data: data,
		});
	}
	
	
	return GraficoVendasPorOrigemMes;
}());


$(function(){
	
	var graficoVendasPorMes = new Brewer.GraficoVendasPorMes();
	graficoVendasPorMes.iniciar();
	
	var graficoVendasPorOrigemMes = new Brewer.GraficoVendasPorOrigemMes();
	graficoVendasPorOrigemMes.iniciar();
	
})