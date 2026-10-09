ALTER TABLE favoritos
    ADD COLUMN lista_id BIGINT,
    ADD CONSTRAINT fk_favoritos_lista FOREIGN KEY (lista_id) REFERENCES listas (id);