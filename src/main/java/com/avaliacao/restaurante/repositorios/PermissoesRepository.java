package com.avaliacao.restaurante.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avaliacao.restaurante.models.Permissoes;

public interface PermissoesRepository extends JpaRepository<Permissoes, Long> {
	Permissoes findByPerm(String permissao);
}
