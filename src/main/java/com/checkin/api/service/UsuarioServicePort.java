package com.checkin.api.service;

import com.checkin.api.model.Usuario;

import java.util.List;

public interface UsuarioServicePort {

    Usuario criarUsuario(Usuario usuario);

    Usuario buscarPorId(Long id);

    Usuario buscarPorEmail(String email);

    List<Usuario> listarTodos();

    Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado);

    void deletarUsuario(Long id);
}
