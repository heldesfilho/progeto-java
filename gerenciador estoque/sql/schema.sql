CREATE DATABASE IF NOT EXISTS estoque_loja
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE estoque_loja;

CREATE TABLE IF NOT EXISTS categorias (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_categorias_nome (nome)
);

CREATE TABLE IF NOT EXISTS produtos (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(100) NOT NULL,
    nome VARCHAR(150) NOT NULL,
    categoria_id INT NULL,
    preco_custo DECIMAL(12, 2) NOT NULL,
    preco_venda DECIMAL(12, 2) NOT NULL,
    quantidade_atual INT NOT NULL DEFAULT 0,
    estoque_minimo INT NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_produtos_codigo (codigo),
    KEY idx_produtos_categoria (categoria_id),
    CONSTRAINT fk_produtos_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias (id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS movimentacoes (
    id INT NOT NULL AUTO_INCREMENT,
    produto_id INT NOT NULL,
    tipo ENUM('ENTRADA', 'SAIDA') NOT NULL,
    quantidade INT NOT NULL,
    data_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacao TEXT NULL,
    PRIMARY KEY (id),
    KEY idx_movimentacoes_produto_data (produto_id, data_hora),
    CONSTRAINT fk_movimentacoes_produto
        FOREIGN KEY (produto_id) REFERENCES produtos (id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS usuarios (
    id INT NOT NULL AUTO_INCREMENT,
    usuario VARCHAR(100) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    senha_salt VARCHAR(255) NOT NULL,
    cargo ENUM('GERENTE', 'SUBGERENTE', 'FUNCIONARIO') NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_usuario (usuario)
);