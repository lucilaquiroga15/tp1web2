package com.example.demo.controller;

import com.example.demo.PostgresTestBase;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class ListaControllerIT extends PostgresTestBase {

    private Long crearLista(String token, String nombre) throws Exception {
        HttpResponse<String> respuesta =
                post("/api/listas", "{\"nombre\":\"" + nombre + "\"}", token);
        assertThat(respuesta.statusCode()).isEqualTo(201);
        return extraerId(respuesta);
    }

    @Test
    void obtener_listaPropia_devuelve200() throws Exception {
        String token = registrarYLoguear(emailUnico(), "clave12345");
        Long listaId = crearLista(token, "Mi lista");

        HttpResponse<String> respuesta = get("/api/listas/" + listaId, token);

        assertThat(respuesta.statusCode()).isEqualTo(200);
    }

    @Test
    void obtener_listaAjena_devuelve403() throws Exception {
        String tokenDueno = registrarYLoguear(emailUnico(), "clave12345");
        String tokenOtro = registrarYLoguear(emailUnico(), "clave12345");
        Long listaId = crearLista(tokenDueno, "Lista del dueño");

        HttpResponse<String> respuesta = get("/api/listas/" + listaId, tokenOtro);

        assertThat(respuesta.statusCode()).isEqualTo(403);
    }

    @Test
    void listar_sinToken_devuelve401() throws Exception {
        HttpResponse<String> respuesta = get("/api/listas", null);

        assertThat(respuesta.statusCode()).isEqualTo(401);
    }

    @Test
    void obtener_adminConListaAjena_devuelve200() throws Exception {
        String tokenDueno = registrarYLoguear(emailUnico(), "clave12345");
        String tokenAdmin = crearAdminYLoguear(emailUnico(), "clave12345");
        Long listaId = crearLista(tokenDueno, "Lista de un usuario");

        HttpResponse<String> respuesta = get("/api/listas/" + listaId, tokenAdmin);

        assertThat(respuesta.statusCode()).isEqualTo(200);
    }
}