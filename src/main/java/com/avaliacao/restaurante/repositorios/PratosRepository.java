package com.avaliacao.restaurante.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.avaliacao.restaurante.models.Prato;

public interface PratosRepository extends JpaRepository<Prato, Long>{
	
}
