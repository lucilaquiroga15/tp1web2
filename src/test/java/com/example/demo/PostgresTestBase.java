package com.example.demo;

import com.example.demo.domain.Rol;
import com.example.demo.domain.Usuario;
import com.example.demo.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.TimeZone;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class PostgresTestBase {

    // Un solo PostgreSQL para todos los ITs; Flyway corre las migraciones sobre una base limpia
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17");
    static {
        // Java en Windows reporta "America/Buenos_Aires", que PostgreSQL 17 rechaza
        TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
        POSTGRES.start();
    }
    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void propiedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Value("${local.server.port}")
    private int puerto;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final HttpClient cliente = HttpClient.newHttpClient();

    // ---------- helpers HTTP ----------

    protected HttpResponse<String> get(String ruta, String token) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + puerto + ruta))
                .GET();
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        return cliente.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> post(String ruta, String json, String token) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + puerto + ruta))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        return cliente.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    // ---------- helpers de datos ----------

    protected String emailUnico() {
        return "user-" + UUID.randomUUID() + "@test.com";
    }

    protected HttpResponse<String> login(String email, String password) throws Exception {
        return post("/api/auth/login",
                "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}", null);
    }

    protected String extraerToken(HttpResponse<String> respuestaLogin) {
        Matcher m = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(respuestaLogin.body());
        if (!m.find()) {
            throw new IllegalStateException("No hay token en: " + respuestaLogin.body());
        }
        return m.group(1);
    }

    protected Long extraerId(HttpResponse<String> respuesta) {
        Matcher m = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(respuesta.body());
        if (!m.find()) {
            throw new IllegalStateException("No hay id en: " + respuesta.body());
        }
        return Long.valueOf(m.group(1));
    }

    // Registra un usuario común por la API y devuelve su token
    protected String registrarYLoguear(String email, String password) throws Exception {
        post("/api/auth/registro",
                "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}", null);
        return extraerToken(login(email, password));
    }

    // El registro siempre crea USER, así que el ADMIN se inserta directo en la base
    protected String crearAdminYLoguear(String email, String password) throws Exception {
        usuarioRepository.save(new Usuario(null, email, passwordEncoder.encode(password), Rol.ADMIN));
        return extraerToken(login(email, password));
    }
}