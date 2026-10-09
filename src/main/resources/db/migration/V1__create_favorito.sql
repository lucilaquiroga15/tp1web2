CREATE TABLE favoritos (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    producto_id BIGINT       NOT NULL,
    nota        VARCHAR(500) NOT NULL,
    fecha_alta  TIMESTAMP    NOT NULL
);