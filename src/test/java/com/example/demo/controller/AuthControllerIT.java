package com.example.demo.controller;

import com.example.demo.PostgresTestBase;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerIT extends PostgresTestBase {

    @Test
    void login_credencialesValidas_devuelve200ConToken() throws Exception {
        String email = emailUnico();
        post("/api/auth/registro",
                "{\"email\":\"" + email + "\",\"password\":\"clave12345\"}", null);

        HttpResponse<String> respuesta = login(email, "clave12345");

        assertThat(respuesta.statusCode()).isEqualTo(200);
        assertThat(respuesta.body()).contains("\"token\"");
    }

    @Test
    void login_credencialesInvalidas_devuelve401() throws Exception {
        String email = emailUnico();
        post("/api/auth/registro",
                "{\"email\":\"" + email + "\",\"password\":\"clave12345\"}", null);

        HttpResponse<String> respuesta = login(email, "contraseña-incorrecta");

        assertThat(respuesta.statusCode()).isEqualTo(401);
    }
}