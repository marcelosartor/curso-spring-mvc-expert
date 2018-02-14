CREATE TABLE estilo (
    codigo bigserial NOT NULL,
    nome VARCHAR(50) NOT NULL,
    primary key (codigo)
) ;

CREATE TABLE cerveja (
    codigo bigserial NOT NULL,
    sku VARCHAR(50) NOT NULL,
    nome VARCHAR(80) NOT NULL,
    descricao text NOT NULL,
    valor numeric(10, 2) NOT NULL,
    teor_alcoolico numeric(10, 2) NOT NULL,
    comissao numeric(10, 2) NOT NULL,
    sabor VARCHAR(50) NOT NULL,
    origem VARCHAR(50) NOT NULL,
    codigo_estilo bigint NOT NULL,
    primary key (codigo)
) ;

ALTER TABLE IF EXISTS cerveja                                   
   add constraint FK_cerveja_estilo foreign key (codigo_estilo) references estilo;  


INSERT INTO estilo(nome) VALUES ('Amber Lager');
INSERT INTO estilo(nome) VALUES ('Dark Lager');
INSERT INTO estilo(nome) VALUES ('Pale Lager');
INSERT INTO estilo(nome) VALUES ('Pilsner');