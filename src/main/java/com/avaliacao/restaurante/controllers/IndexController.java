package com.avaliacao.restaurante.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.avaliacao.restaurante.models.Cliente;
import com.avaliacao.restaurante.models.Permissoes;
import com.avaliacao.restaurante.repositorios.ClienteRepository;

@Controller
public class IndexController {
	
	@Autowired
	private ClienteRepository clienteRepository;

	// Index Functions

	@RequestMapping("/")
	public String index(Model m, @CurrentSecurityContext(expression = "authentication.name") String login) {
		Cliente cliente = clienteRepository.findByEmail(login);
		m.addAttribute("cliente", cliente);
		return "index";
	}

	private boolean temAutorizacao(Cliente cliente, String permGlobal) {
		for (Permissoes permAuxGlobal : cliente.getPerms()) {
			if (permAuxGlobal.getPerm().equals(permGlobal)) {
				return true;
			}
		}
		return false;
	}

	@RequestMapping("/index")
	public String formCliente(Model m, @CurrentSecurityContext(expression = "authentication.name") String login) {

		Cliente cliente = clienteRepository.findByEmail(login);

		String redirectURL = "";
		if (temAutorizacao(cliente, "MODERADOR")) {
			redirectURL = "moderadorIndex";
		} else if (temAutorizacao(cliente, "USUARIO")) {
			redirectURL = "clienteIndex";
		}
		
		m.addAttribute("cliente", cliente);
		return redirectURL;
	}
}
