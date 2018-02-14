CREATE TABLE usuario (
    codigo bigserial NOT NULL,
    nome VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    senha VARCHAR(120) NOT NULL,
    ativo BOOLEAN DEFAULT true,
    data_nascimento DATE,
    primary key (codigo)
) ;

CREATE TABLE grupo (
    codigo bigserial NOT NULL,
    nome VARCHAR(50) NOT NULL,
    primary key (codigo)
) ;

CREATE TABLE permissao (
    codigo bigserial NOT NULL,
    nome VARCHAR(50) NOT NULL,
    primary key (codigo)
) ;

CREATE TABLE usuario_grupo (
    codigo_usuario bigint NOT NULL,
    codigo_grupo bigint NOT NULL,
    PRIMARY KEY (codigo_usuario, codigo_grupo),
    FOREIGN KEY (codigo_usuario) REFERENCES usuario(codigo),
    FOREIGN KEY (codigo_grupo) REFERENCES grupo(codigo)
);

CREATE TABLE grupo_permissao (
    codigo_grupo bigint NOT NULL,
    codigo_permissao bigint NOT NULL,
    PRIMARY KEY (codigo_grupo, codigo_permissao),
    FOREIGN KEY (codigo_grupo) REFERENCES grupo(codigo),
    FOREIGN KEY (codigo_permissao) REFERENCES permissao(codigo)
);