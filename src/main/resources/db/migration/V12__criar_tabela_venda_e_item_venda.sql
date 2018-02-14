CREATE TABLE venda (
    codigo bigserial not null,
    data_criacao timestamp not null,
    valor_frete numeric(10,2),
    valor_desconto numeric(10,2),
    valor_total numeric(10,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    observacao VARCHAR(200),
    data_hora_entrega timestamp not null,
    codigo_cliente BIGINT NOT NULL,
    codigo_usuario BIGINT NOT NULL,
    primary key (codigo)
);

ALTER TABLE IF EXISTS venda                                   
   add constraint FK_venda_cliente foreign key (codigo_cliente) references cliente;  

ALTER TABLE IF EXISTS venda                                   
   add constraint FK_venda_usuario foreign key (codigo_usuario) references usuario;  


CREATE TABLE item_venda (
    codigo bigserial not null,
    quantidade int NOT NULL,
    valor_unitario numeric(10,2) NOT NULL,
    codigo_cerveja BIGINT NOT NULL,
    codigo_venda BIGINT NOT NULL,
    primary key (codigo)
); 


ALTER TABLE IF EXISTS item_venda                                   
   add constraint FK_item_venda_cerveja foreign key (codigo_cerveja) references cerveja;  
   
ALTER TABLE IF EXISTS item_venda                                   
   add constraint FK_item_venda_venda foreign key (codigo_venda) references venda;   

