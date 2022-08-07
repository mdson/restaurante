package com.avaliacao.restaurante.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.avaliacao.restaurante.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{
	Cliente findByEmail(String email);
}
