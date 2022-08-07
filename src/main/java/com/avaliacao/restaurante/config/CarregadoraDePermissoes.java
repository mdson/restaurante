package com.avaliacao.restaurante.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.avaliacao.restaurante.models.Permissoes;
import com.avaliacao.restaurante.repositorios.PermissoesRepository;

@Component
public class CarregadoraDePermissoes implements CommandLineRunner{
	
	@Autowired
	private PermissoesRepository permRepository;

	@Override
	public void run(String... args) throws Exception {
		
		String[] permissoes = {"MODERADOR", "USUARIO"};
		
		for(String permStringValor : permissoes) {
			Permissoes perm = permRepository.findByPerm(permStringValor);
			if(perm==null) {
				perm = new Permissoes(permStringValor);
				permRepository.save(perm);
			}
		}
	}
	
}
