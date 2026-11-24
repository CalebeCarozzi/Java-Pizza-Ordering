-- Cria o banco sem apagar dados existentes.
CREATE DATABASE IF NOT EXISTS PizzaOrdering
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE PizzaOrdering;

-- TABELA DE CLIENTES

CREATE TABLE IF NOT EXISTS cliente (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(100) NOT NULL,
                         sobrenome VARCHAR(100) NOT NULL,
                         telefone VARCHAR(11) NOT NULL UNIQUE
);

-- TABELA DE TIPOS E PREÇOS

CREATE TABLE IF NOT EXISTS tipo_pizza (
                            tipo VARCHAR(10) PRIMARY KEY,
                            preco_cm2 DECIMAL(10,4) NOT NULL,

                            CHECK (tipo IN (
                                            'SIMPLES', 'ESPECIAL', 'PREMIUM'
                                )),

                            CHECK (preco_cm2 > 0)
);

-- TABELA DE SABORES

CREATE TABLE IF NOT EXISTS sabor (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       nome VARCHAR(100) NOT NULL UNIQUE,
                       tipo VARCHAR(10) NOT NULL,

                       FOREIGN KEY (tipo)
                           REFERENCES tipo_pizza(tipo)
);

-- TABELA DE PEDIDOS

CREATE TABLE IF NOT EXISTS pedido (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        cliente_id INT NOT NULL,
                        estado VARCHAR(15) NOT NULL DEFAULT 'ABERTO',
                        preco_total DECIMAL(12,2) NOT NULL DEFAULT 0,

                        FOREIGN KEY (cliente_id)
                            REFERENCES cliente(id)
                            ON DELETE CASCADE,

                        CHECK (estado IN (
                                          'ABERTO', 'A_CAMINHO', 'ENTREGUE'
                            )),

                        CHECK (preco_total >= 0)
);

-- TABELA DE ITENS DO PEDIDO

CREATE TABLE IF NOT EXISTS item_pedido (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             pedido_id INT NOT NULL,

                             forma VARCHAR(15) NOT NULL,
                             medida DECIMAL(16,10) NOT NULL,

                             sabor1_id INT NOT NULL,
                             sabor2_id INT,

                             quantidade INT NOT NULL DEFAULT 1,
                             preco_unitario DECIMAL(12,2) NOT NULL,

                             FOREIGN KEY (pedido_id)
                                 REFERENCES pedido(id)
                                 ON DELETE CASCADE,

                             FOREIGN KEY (sabor1_id)
                                 REFERENCES sabor(id)
                                 ON DELETE RESTRICT,

                             FOREIGN KEY (sabor2_id)
                                 REFERENCES sabor(id)
                                 ON DELETE RESTRICT,

                             CHECK (forma IN (
                                              'QUADRADO', 'CIRCULO', 'TRIANGULO'
                                 )),

                             CHECK (medida > 0),
                             CHECK (quantidade > 0),
                             CHECK (preco_unitario > 0),
                             CHECK (sabor2_id IS NULL OR sabor1_id <> sabor2_id)
);

-- PREÇOS INICIAIS: insere apenas tipos ausentes, preservando preços existentes.

INSERT INTO tipo_pizza (tipo, preco_cm2)
SELECT 'SIMPLES', 0.05
WHERE NOT EXISTS (SELECT 1 FROM tipo_pizza WHERE tipo = 'SIMPLES');

INSERT INTO tipo_pizza (tipo, preco_cm2)
SELECT 'ESPECIAL', 0.07
WHERE NOT EXISTS (SELECT 1 FROM tipo_pizza WHERE tipo = 'ESPECIAL');

INSERT INTO tipo_pizza (tipo, preco_cm2)
SELECT 'PREMIUM', 0.10
WHERE NOT EXISTS (SELECT 1 FROM tipo_pizza WHERE tipo = 'PREMIUM');
