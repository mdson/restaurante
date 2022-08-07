package com.avaliacao.restaurante.security;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.avaliacao.restaurante.models.Cliente;
import com.avaliacao.restaurante.models.Permissoes;
import com.avaliacao.restaurante.repositorios.ClienteRepository;


@Service
@Transactional
public class DetalheClienteService implements UserDetailsService {
	
	private ClienteRepository clienteRepository;
	
	public DetalheClienteService(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Cliente cliente = clienteRepository.findByEmail(username);
		if(cliente != null) {
			Set<GrantedAuthority> permsDoCliente = new HashSet<GrantedAuthority>();
			for(Permissoes perms : cliente.getPerms()) {
				GrantedAuthority clientPerm = new SimpleGrantedAuthority(perms.getPerm());
				permsDoCliente.add(clientPerm);
			}
			User user = new User(cliente.getEmail(), cliente.getSenha(), permsDoCliente);
			return user;
		} else {
			throw new UsernameNotFoundException("Usuário não encontrado!");
		}
	}
}
