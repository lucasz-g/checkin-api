package com.checkin.api.model.enums;

/**
 * Enumeração representando os papéis de usuário disponíveis na aplicação.
 *
 * A existência de diferentes papéis permite restringir ou conceder acesso
 * a determinados endpoints. Por padrão, todos os usuários são criados com
 * o papel USER. O papel ADMIN pode ser atribuído manualmente via banco
 * de dados ou em futuras implementações administrativas.
 */
public enum Role {
    USER,
    ADMIN
}