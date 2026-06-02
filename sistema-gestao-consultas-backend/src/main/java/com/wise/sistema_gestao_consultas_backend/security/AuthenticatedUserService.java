package com.wise.sistema_gestao_consultas_backend.security;

import com.wise.sistema_gestao_consultas_backend.enums.PerfilUsuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    public CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            throw new IllegalStateException("Usuario nao autenticado");
        }
        return user;
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public PerfilUsuario getCurrentPerfil() {
        return getCurrentUser().getPerfil();
    }

    public boolean isAdmin() {
        return PerfilUsuario.ADMIN.equals(getCurrentPerfil());
    }
}
