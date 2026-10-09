# TP2 · Persistencia, migraciones y arquitectura hexagonal

Evolución del proyecto del TP1 (Spring Boot, API REST y arquitectura en capas). Los favoritos dejan de guardarse en memoria y pasan a **PostgreSQL** con **Spring Data JPA / Hibernate**, el esquema se versiona con **Flyway**, se suma el recurso **Listas** (relación con favoritos) y se implementa una operación **transaccional**.

## 🛠️ Tecnologías

- Java 25
- Spring Boot 4.1.x
- Maven
- Spring Web MVC y Bean Validation
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Springdoc OpenAPI / Swagger
- RestClient + API externa DummyJSON

## 🏗️ Arquitectura

```
Controller  →  Service  →  Puerto (interfaz Repository)
                                   ↑ implementa
                           Adapter (JPA)  →  PostgreSQL
```

El Service depende de un **puerto** (`FavoritoRepository`, `ListaRepository`): un contrato que solo habla en términos del dominio. La implementación concreta con JPA es un **adapter** que traduce entre los records del dominio y las entidades JPA.

## 🚀 Requisitos

- Java 25
- PostgreSQL (instalación local)

## 🐘 Cómo levantar PostgreSQL

Los datos de conexión que usa la aplicación (`application.properties`) son:

| Dato | Valor |
|---|---|
| Base | `tp2_web2` |
| Usuario | `webii_tp2` |
| Contraseña | `webii_tp2` |
| Puerto | `5432` |

### Opción A: Docker

```bash
docker compose up -d
```

### Opción B: instalación local

Con PostgreSQL instalado, en pgAdmin (Query Tool) o `psql`, ejecutar una sentencia por vez:

```sql
CREATE ROLE webii_tp2 WITH LOGIN PASSWORD 'webii_tp2';
```

```sql
CREATE DATABASE tp2_web2 OWNER webii_tp2;
```

## ▶️ Ejecución

```bash
./mvnw spring-boot:run
```

En Windows:

```bash
mvnw.cmd spring-boot:run
```

Queda disponible en `http://localhost:8080`:

- API: `/api/productos`, `/api/favoritos`, `/api/listas`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## 🗂️ Migraciones con Flyway

Flyway aplica automáticamente, al arrancar, las migraciones de `src/main/resources/db/migration`. Hibernate está en `ddl-auto=validate`: solo verifica que las entidades coincidan con el esquema, nunca lo modifica.

| Versión | Archivo | Qué hace |
|---|---|---|
| V1 | `V1__create_favoritos.sql` | Crea la tabla `favoritos` |
| V2 | `V2__create_listas.sql` | Crea la tabla `listas` |
| V3 | `V3__add_lista_id_a_favoritos.sql` | Agrega `lista_id` (con clave foránea) a `favoritos` |
| V4 | `V4__lista_id_obligatorio.sql` | Crea la lista "Sin clasificar", asigna los favoritos sin lista y hace `lista_id` `NOT NULL` |

**Cómo confirmar que corrieron:** en el log de arranque aparece `Successfully applied N migration(s)`, y la tabla que Flyway crea sola lo registra:

```sql
SELECT installed_rank, version, description, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

## 📌 Endpoints

### Productos (solo lectura, espejo de DummyJSON)

```
GET /api/productos
GET /api/productos/{id}
```

### Favoritos

```
POST   /api/favoritos
GET    /api/favoritos
GET    /api/favoritos/{id}
PUT    /api/favoritos/{id}
DELETE /api/favoritos/{id}
```

Cada favorito pertenece a una lista (`listaId` obligatorio).

### Listas

| Operación | Método | Código de éxito |
|---|---|---|
| Crear lista | `POST /api/listas` | 201 Created |
| Listar listas | `GET /api/listas` | 200 OK |
| Obtener una lista | `GET /api/listas/{id}` | 200 OK |
| Favoritos de una lista | `GET /api/listas/{id}/favoritos` | 200 OK |
| Eliminar una lista vacía | `DELETE /api/listas/{id}` | 204 No Content |
| Mover favoritos a otra lista | `POST /api/listas/{origenId}/mover-favoritos` | 200 OK |

Errores: `400` por datos inválidos, `404` si un recurso no existe, `409` al borrar una lista con favoritos o al mover una lista sobre sí misma.

## 🔄 Qué cambió en la capa de persistencia (TP1 → TP2)

Al migrar los favoritos de memoria a JPA:

**Clases agregadas:** `FavoritoEntity`, `FavoritoJpaRepository` y `FavoritoRepositoryAdapter`.

**Clase eliminada:** `InMemoryFavoritoRepository`. Dos beans implementando `FavoritoRepository` hacen que Spring no sepa cuál inyectar.

**Clases que quedaron exactamente iguales:** `FavoritoRepository` (el puerto), el record de dominio `Favorito`, `FavoritoService`, `FavoritoController` y los DTOs de favoritos.

Esto fue posible porque `FavoritoRepository` es un **puerto**: el Service solo conoce el contrato (`buscarTodos`, `buscarPorId`, `save`, `deleteById`) y no sabe quién lo implementa. La versión en memoria del TP1 y `FavoritoRepositoryAdapter` son **adapters** intercambiables detrás de la misma interfaz, y cambiar de uno a otro no exige tocar nada de las capas superiores.

Después, al sumar Listas, `Favorito`, `FavoritoService`, los DTOs de favoritos y el puerto `FavoritoRepository` se modificaron para incorporar `listaId` y `buscarPorListaId`. Ese cambio se debe a una funcionalidad nueva, no al cambio de tecnología de persistencia.

## 🧬 Evolución del esquema: por qué V4 es una migración nueva

V1, V2 y V3 ya estaban aplicadas, y Flyway guarda un checksum de cada una en `flyway_schema_history`: si editara un archivo ya ejecutado, el checksum no coincidiría y la aplicación dejaría de arrancar, además de que otras bases que ya corrieron esa versión quedarían con un esquema distinto. Por eso la corrección se hizo con una migración nueva (V4), que se aplica una sola vez, en orden, y mantiene el historial idéntico en todas las bases.

## 🔒 Transacciones: por qué `mover-favoritos` es `@Transactional`

Mover los favoritos de una lista a otra son varias escrituras: reasignar cada favorito a la lista destino y, al final, eliminar la lista origen. Sin `@Transactional`, cada llamada al repository se confirma por separado (commit) apenas termina. Si una escritura fallara después de que otra ya se confirmó, el cambio anterior no se deshace, y la base queda a medio camino: por ejemplo, con parte de los favoritos ya en la lista destino y el resto todavía en la origen, o con todos los favoritos movidos pero la lista origen sin eliminar. Eso rompe la **atomicidad** de ACID (la operación debería aplicarse completa o no aplicarse) y deja datos inconsistentes, porque el resultado no corresponde ni al estado anterior ni al final esperado (**consistencia**). Con `@Transactional`, todas las escrituras forman una sola transacción: si cualquiera falla, se hace rollback de todas y la base queda exactamente como estaba, y los cambios recién se confirman, y se vuelven visibles para otros, cuando la operación terminó entera.

## 🧪 Evidencia

Casos de éxito y de error de cada recurso (incluida la operación de mover favoritos) en [`requests.http`](./requests.http), para usar con la extensión REST Client de VS Code. Ejecutarlo de arriba hacia abajo.


## 📖 Objetivos del TP

- Conectar una aplicación Spring Boot a PostgreSQL con Spring Data JPA.
- Reemplazar un repository en memoria por un adapter JPA sin tocar el dominio, el Service ni el Controller.
- Versionar el esquema con Flyway.
- Modelar una relación entre entidades y exponerla en la API.
- Delimitar una operación transaccional y justificarla en términos de atomicidad.
- Distinguir un puerto (contrato) de un adapter (implementación de infraestructura).