package com.internet.banking.microservice.auth.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthData {

    // Formato exigido pelo professor:
    // { "access_token": "...", "token_type": "bearer", "tipo": "CLIENTE", "usuario": {...} }

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType = "bearer";

    @JsonProperty("tipo")
    private String tipo;

    @JsonProperty("usuario")
    private UsuarioData usuario;

    public AuthData() {}

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public UsuarioData getUsuario() { return usuario; }
    public void setUsuario(UsuarioData usuario) { this.usuario = usuario; }

    // ── Classe interna para dados do usuário ──────────────────────────────────
    public static class UsuarioData {
        private String cpf;
        private String nome;
        private String email;
        private String perfil;

        public UsuarioData() {}

        public String getCpf() { return cpf; }
        public void setCpf(String cpf) { this.cpf = cpf; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPerfil() { return perfil; }
        public void setPerfil(String perfil) { this.perfil = perfil; }
    }

    // ── Campos legados (mantidos para compatibilidade) ─────────────────────────
    private String username;
    private String token;
    private String type;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}