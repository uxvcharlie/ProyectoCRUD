# ProyectoCRUD
```sql
--Creamos la base de datos
CREATE DATABASE bdventas;

--Hacer el esquema
CREATE SCHEMA catalogos;

--Crear tablas

--Crear tabla de categorias
CREATE TABLE catalogos.categorias(
id_categoria SERIAL PRIMARY KEY,
nombre VARCHAR(80) NOT NULL,
descripcion VARCHAR(200) NOT NULL,
estado SMALLINT NOT NULL DEFAULT 1,

CONSTRAINT chk_categoria_estado
CHECK(estado IN(0, 1))
);

INSERT INTO catalogos.categorias (nombre, descripcion)
VALUES ('Cereales 2', 'Categoria adicional');



--Crear tabla marcas
CREATE TABLE catalogos.marcas(
id_marcas SERIAL PRIMARY KEY,
nombre VARCHAR(80) NOT NULL UNIQUE,
pais_origen VARCHAR(80),
estado SMALLINT NOT NULL DEFAULT 1,

CONSTRAINT chk_marca_estado
    CHECK(estado IN(0,1))
);

INSERT INTO catalogos.marcas(nombre, pais_origen)
VALUES ('Nike', 'Canada');

INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES('Puma', 'Tailandia', 1);

INSERT INTO catalogos.marcas(nombre, pais_origen)
VALUES('Adidas', 'Taiwan');

--Crear tabla productos
CREATE TABLE catalogos.productos(
id_producto SERIAL NOT NULL PRIMARY KEY,
codigo VARCHAR(20) NOT NULL UNIQUE,
nombre VARCHAR(100) NOT NULL,
descripcion VARCHAR(200),
precio NUMERIC(10, 2) NOT NULL,
stock INTEGER NOT NULL DEFAULT 0,
fecha_creacion DATE NOT NULL DEFAULT CURRENT_DATE,
id_categoria INTEGER NOT NULL,
id_marcas INTEGER NOT NULL,
estado SMALLINT NOT NULL DEFAULT 1,

CONSTRAINT fk_producto_categoria
FOREIGN KEY (id_categoria)
REFERENCES catalogos.categorias(id_categoria),

CONSTRAINT fk_producto_marca
FOREIGN KEY (id_marcas)
REFERENCES catalogos.marcas(id_marcas),

CONSTRAINT chk_producto_precio
CHECK(precio > 0),

CONSTRAINT chk_producto_stock
CHECK(stock >= 0),

CONSTRAINT chk_producto_estado
CHECK(estado IN(0, 1))

);

-- Crear 5 registro de categorias

-- 1
INSERT INTO catalogos.categorias (nombre, descripcion)
VALUES ('Engranajes', 'Categoria de piezas');
-- 2
INSERT INTO catalogos.categorias (nombre, descripcion)
VALUES ('Motores', 'Categoria de electronica');
-- 3
INSERT INTO catalogos.categorias (nombre, descripcion)
VALUES ('Ejes', 'Categoria de piezas');

-- 4
INSERT INTO catalogos.categorias (nombre, descripcion)
VALUES ('EV3', 'Categoria de hubs');

-- 5
INSERT INTO catalogos.categorias (nombre, descripcion)
VALUES ('Technic spike prime', 'Categoria de hubs');



-- Crear 5 registro de marcas
--
INSERT INTO catalogos.marcas(nombre, pais_origen)
VALUES ('Tommy Hilfiger', 'España');

INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES('Gucci', 'Estados Unidos', 1);

INSERT INTO catalogos.marcas(nombre, pais_origen)
VALUES('New balance', 'Estados unidos');


INSERT INTO catalogos.marcas(nombre, pais_origen, estado)
VALUES('Converse', 'México', 1);

INSERT INTO catalogos.marcas(nombre, pais_origen)
VALUES('Under armor', 'Estados unidos');

-- Crear 10 registro de productos
-- id_producto SERIAL NOT NULL PRIMARY KEY,
-- codigo VARCHAR(20) NOT NULL UNIQUE,
-- nombre VARCHAR(100) NOT NULL,
-- descripcion VARCHAR(200),
--precio NUMERIC(10, 2) NOT NULL,
--stock INTEGER NOT NULL DEFAULT 0,
--fecha_creacion DATE NOT NULL DEFAULT CURRENT_DATE,
--id_categoria INTEGER NOT NULL,
--id_marcas INTEGER NOT NULL,
--estado SMALLINT NOT NULL DEFAULT 1,


-- 1
INSERT INTO catalogos.productos(codigo, nombre, descripcion, precio, stock, id_categor
VALUES('P001', 'Camiseta', 'Camiseta 80% algodon', 200.90, 10,
(SELECT id_categoria FROM catalogos.categorias WHERE nombre = 'Engranajes' LIMIT 1),
(SELECT id_marcas FROM catalogos.marcas WHERE nombre = 'Gucci'));

-- 2
INSERT INTO catalogos.productos(codigo, nombre, descripcion, precio, stock, id_categor
VALUES('P002', 'Pantalon', 'Pantalon de mezclilla', 450.00, 15,
(SELECT id_categoria FROM catalogos.categorias WHERE nombre = 'Motores' LIMIT 1),
(SELECT id_marcas FROM catalogos.marcas WHERE nombre = 'Nike'));

-- 3
INSERT INTO catalogos.productos(codigo, nombre, descripcion, precio, stock, id_categor
VALUES('P003', 'Tenis', 'Tenis deportivos para correr', 899.99, 8,
(SELECT id_categoria FROM catalogos.categorias WHERE nombre = 'Ejes' LIMIT 1),
(SELECT id_marcas FROM catalogos.marcas WHERE nombre = 'Adidas'));

-- 4
INSERT INTO catalogos.productos(codigo, nombre, descripcion, precio, stock, id_categor
VALUES('P004', 'Sudadera', 'Sudadera con capucha', 650.50, 12,
(SELECT id_categoria FROM catalogos.categorias WHERE nombre = 'EV3' LIMIT 1),
(SELECT id_marcas FROM catalogos.marcas WHERE nombre = 'Puma'));

-- 5
INSERT INTO catalogos.productos(codigo, nombre, descripcion, precio, stock, id_categor
VALUES('P005', 'Gorra', 'Gorra ajustable', 150.00, 20,
(SELECT id_categoria FROM catalogos.categorias WHERE nombre = 'Technic spike prime' LI
(SELECT id_marcas FROM catalogos.marcas WHERE nombre = 'New balance'));

-- 6
INSERT INTO catalogos.productos(codigo, nombre, descripcion, precio, stock, id_categor
VALUES('P006', 'Chamarra', 'Chamarra impermeable', 1200.00, 5,
(SELECT id_categoria FROM catalogos.categorias WHERE nombre = 'Engranajes' LIMIT 1),
(SELECT id_marcas FROM catalogos.marcas WHERE nombre = 'Tommy Hilfiger'));

-- 7
INSERT INTO catalogos.productos(codigo, nombre, descripcion, precio, stock, id_categor
VALUES('P007', 'Calcetines', 'Paquete de 3 pares', 99.90, 30,
(SELECT id_categoria FROM catalogos.categorias WHERE nombre = 'Motores' LIMIT 1),
(SELECT id_marcas FROM catalogos.marcas WHERE nombre = 'Under armor'));

-- 8
INSERT INTO catalogos.productos(codigo, nombre, descripcion, precio, stock, id_categor
VALUES('P008', 'Short', 'Short deportivo', 320.00, 18,
(SELECT id_categoria FROM catalogos.categorias WHERE nombre = 'Ejes' LIMIT 1),
(SELECT id_marcas FROM catalogos.marcas WHERE nombre = 'Converse'));

-- 9
INSERT INTO catalogos.productos(codigo, nombre, descripcion, precio, stock, id_categor
VALUES('P009', 'Playera polo', 'Playera polo de algodon', 550.00, 9,
(SELECT id_categoria FROM catalogos.categorias WHERE nombre = 'EV3' LIMIT 1),
(SELECT id_marcas FROM catalogos.marcas WHERE nombre = 'Tommy Hilfiger'));

-- 10
INSERT INTO catalogos.productos(codigo, nombre, descripcion, precio, stock, id_categor
VALUES('P010', 'Mochila', 'Mochila escolar', 780.00, 7,
(SELECT id_categoria FROM catalogos.categorias WHERE nombre = 'Technic spike prime' LI
(SELECT id_marcas FROM catalogos.marcas WHERE nombre = 'Nike'));
```
