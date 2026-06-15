package com.checkin.api.service;

import com.checkin.api.exception.BusinessValidationException;
import com.checkin.api.exception.ResourceNotFoundException;
import com.checkin.api.model.Usuario;
import com.checkin.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pela lógica de negócio de usuários.
 * 
 * Camada de serviço que implementa validações e regras de negócio
 * relacionadas aos usuários.
 */
@Service
public class UsuarioService implements UsuarioServicePort {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Cria um novo usuário após validações.
     * 
     * @param usuario Dados do usuário
     * @return Usuário criado
     * @throws BusinessValidationException se o email já estiver em uso
     */
    @Transactional
    @Override
    public Usuario criarUsuario(Usuario usuario) {
        // Validação: Email já existe?
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new BusinessValidationException(
                    "Email " + usuario.getEmail() + " já está em uso"
            );
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Busca um usuário por ID.
     * 
     * @param id ID do usuário
     * @return Usuário encontrado
     * @throws ResourceNotFoundException se o usuário não existir
     */
    @Transactional(readOnly = true)
    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário com ID " + id + " não encontrado"
                ));
    }

    /**
     * Busca um usuário por email.
     * 
     * @param email Email do usuário
     * @return Usuário encontrado
     * @throws ResourceNotFoundException se o usuário não existir
     */
    @Transactional(readOnly = true)
    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário com email " + email + " não encontrado"
                ));
    }

    /**
     * Lista todos os usuários.
     * 
     * @return Lista de usuários
     */
    @Transactional(readOnly = true)
    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Atualiza dados de um usuário.
     * 
     * @param id ID do usuário
     * @param usuarioAtualizado Dados atualizados
     * @return Usuário atualizado
     * @throws ResourceNotFoundException se o usuário não existir
     * @throws BusinessValidationException se o email já estiver em uso por outro usuário
     */
    @Transactional
    @Override
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = buscarPorId(id);

        // Validação: Se o email mudou, verificar se já está em uso
        if (!usuario.getEmail().equals(usuarioAtualizado.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
                throw new BusinessValidationException(
                        "Email " + usuarioAtualizado.getEmail() + " já está em uso"
                );
            }
        }

        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());

        return usuarioRepository.save(usuario);
    }

    /**
     * Deleta um usuário.
     * 
     * @param id ID do usuário
     * @throws ResourceNotFoundException se o usuário não existir
     */
    @Transactional
    @Override
    public void deletarUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }
}
