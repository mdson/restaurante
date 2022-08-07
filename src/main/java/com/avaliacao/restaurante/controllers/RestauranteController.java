package com.avaliacao.restaurante.controllers;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.avaliacao.restaurante.models.Cliente;
import com.avaliacao.restaurante.models.FormaPagamento;
import com.avaliacao.restaurante.models.Pedido;
import com.avaliacao.restaurante.models.Permissoes;
import com.avaliacao.restaurante.models.Prato;
import com.avaliacao.restaurante.repositorios.ClienteRepository;
import com.avaliacao.restaurante.repositorios.FormaPagamentoRepository;
import com.avaliacao.restaurante.repositorios.PedidosRepository;
import com.avaliacao.restaurante.repositorios.PermissoesRepository;
import com.avaliacao.restaurante.repositorios.PratosRepository;
import  java.time.LocalDateTime;

@Controller
public class RestauranteController {
	
	//Bancos Wireds
	
	@Autowired
	private ClienteRepository cr;
	
	@Autowired
	private FormaPagamentoRepository fpr;
	
	@Autowired
	private PratosRepository pr;
	
	@Autowired
	private PedidosRepository pedidosRepository;
	
	@Autowired
	private PermissoesRepository permsRepository;
	
	@Autowired
	private BCryptPasswordEncoder criptografia;
	
	//Pagina login personalizada
	
	@RequestMapping("/paginaLogin")
	public String modeloDeLoginPersonalizado(Model m) {
		return "paginaLogin";
	}
	
	//Cliente Functions
	
	@RequestMapping(value="/clientForm", method=RequestMethod.GET)
	public String formCliente(Model m) {
		m.addAttribute("cliente", new Cliente());
		return "cadastroCliente";
	}
	
	@RequestMapping(value="/clientForm", method=RequestMethod.POST)
	public String formCliente(Model m, Cliente cliente) {
		Permissoes perm = permsRepository.findByPerm("USUARIO");
		List<Permissoes> perms = new ArrayList<Permissoes>();
		perms.add(perm);
		cliente.setPerms(perms);
		String senhaCriptografada = criptografia.encode(cliente.getSenha());
		cliente.setSenha(senhaCriptografada);
		cr.save(cliente);
		return "redirect:/paginaLogin";
	}
	
	@RequestMapping("/clientsList")
	public ModelAndView clientsList(Model m, @CurrentSecurityContext(expression = "authentication.name") String login) {
		Cliente cliente = cr.findByEmail(login);
		m.addAttribute("clienteLogado", cliente);
		ModelAndView mv = new ModelAndView("listaDeClientes");
		Iterable<Cliente> clientes = cr.findAll();
		mv.addObject("clientes", clientes);
		return mv;
	}
	
	//Prato Functions
	
	@RequestMapping(value="/cadastrarPrato", method=RequestMethod.GET)
	public String formPrato(Model m) {
		m.addAttribute("prato", new Prato());
		return "cadastroPrato";
	}
	
	@RequestMapping(value="/cadastrarPrato", method=RequestMethod.POST)
	public String formPrato(Model m, Prato prato) {
		pr.save(prato);
		return "redirect:/pratosList";
	}
	
	@RequestMapping("/pratosList")
	public ModelAndView pratosList(Model m, @CurrentSecurityContext(expression = "authentication.name") String login) {
		ModelAndView mv = new ModelAndView("listaDePratos");
		Cliente cliente = cr.findByEmail(login);
		m.addAttribute("cliente", cliente);
		Iterable<Prato> pratos = pr.findAll();
		mv.addObject("pratos", pratos);
		return mv;
	}
	
	@RequestMapping(value="pedido/checkout/{id}", method=RequestMethod.GET)
	public ModelAndView confirmarPedido(@PathVariable("id") Long id, Model m) {
		Prato prato = pr.getReferenceById(id);
		ModelAndView mv = new ModelAndView("confirmacaoDePedido");
		Iterable<FormaPagamento> formasDePagamento = fpr.findAll();
		m.addAttribute("formasDePagamento", formasDePagamento);
		m.addAttribute("pedido", new Pedido());
		mv.addObject("prato", prato);
		return mv;
	}
	
	
	//Pedido Functions
	
	@RequestMapping(value="pedido/checkout/{id}", method=RequestMethod.POST)
	public String confirmarPedidoPost(@PathVariable("id") Long idPrato, @RequestParam(value="pagamentoEscolhido") Long pagamentoSelecionadoId, 
			@RequestParam(value="descricao") String desc, @CurrentSecurityContext(expression = "authentication.name") String login, Model m) {
		Pedido pedido = new Pedido();
		Prato prato = pr.getReferenceById(idPrato);
		Cliente cliente = cr.findByEmail(login);
		FormaPagamento pagamentoSelecionado = fpr.getReferenceById(pagamentoSelecionadoId);
		pedido.setCliente(cliente);
		pedido.setPrato(prato);	
		pedido.setPreco(prato.getPreco());
		pedido.setDescricao(desc);
		pedido.setHoraDoPedido(LocalDateTime.now());
		pedido.setFormaDePagamento(pagamentoSelecionado);
		pedidosRepository.save(pedido);
		m.addAttribute("cliente", cliente);
		return "redirect:/index";
	}
	
	@RequestMapping("/pedidosList")
	public ModelAndView pedidosList(Model m, @CurrentSecurityContext(expression = "authentication.name") String login) {
		Cliente cliente = cr.findByEmail(login);
		m.addAttribute("clienteLogado", cliente);
		ModelAndView mv = new ModelAndView("listaDePedidos");
		Iterable<Pedido> pedidos = pedidosRepository.findAll();
		mv.addObject("pedidos", pedidos);
		return mv;
	}
	
	//Forma de pagamento Functions
	
	@RequestMapping(value="/cadastrarFormaDePagamento", method=RequestMethod.GET)
	public String formFormaDePagamento(Model m) {
		m.addAttribute("formaDePagamento", new FormaPagamento());
		return "cadastroFormaDePagamento";
	}
	
	@RequestMapping(value="/cadastrarFormaDePagamento", method=RequestMethod.POST)
	public String formFormaDePagamento(Model m, FormaPagamento formaDePagamento) {
		fpr.save(formaDePagamento);
		return "redirect:/pagamentosList";
	}
	
	@RequestMapping("/pagamentosList")
	public ModelAndView pagamentosList(Model m, @CurrentSecurityContext(expression = "authentication.name") String login) {
		ModelAndView mv = new ModelAndView("listaDeFormasDePagamento");
		Cliente cliente = cr.findByEmail(login);
		m.addAttribute("clienteLogado", cliente);
		Iterable<FormaPagamento> formasDePagamento = fpr.findAll();
		mv.addObject("formasDePagamento", formasDePagamento);
		return mv;
	}
	
	//Permissoes Functions
	
	@RequestMapping(value="attachPerms/{id}", method=RequestMethod.GET)
	public String atribuirPermissoes(@PathVariable("id") Long idCliente, Model m) {
		Cliente cliente = cr.getReferenceById(idCliente);
		m.addAttribute("cliente", cliente);
		m.addAttribute("permissoesList", permsRepository.findAll());
		return "atribuirPermissoes";
	}
	
	@RequestMapping(value="attachPerms/{id}", method=RequestMethod.POST)
	public String atribuirPermissoesPost(@PathVariable("id") Long idCliente, Model m, @RequestParam(value="permsSelecionadas", required=false) int[] permsIds, Cliente cliente, RedirectAttributes attributes) {
		if(permsIds==null) {
			cliente.setId(idCliente);
			attributes.addFlashAttribute("msg", "Selecione ao menos uma permissão!");
			return "redirect:/attachPerms/"+idCliente;
		} else {
			List<Permissoes> perms = new ArrayList<Permissoes>();
			for(int i = 0; i < permsIds.length; i++) {
				long idPerm = permsIds[i];
				Permissoes permAux = permsRepository.getReferenceById(idPerm);
				perms.add(permAux);
			}
			Optional<Cliente> clienteOptional = cr.findById(idCliente);
			if(clienteOptional.isPresent()) {
				Cliente client = clienteOptional.get();
				client.setPerms(perms);
				cr.save(client);
			}
		}
		
		return "redirect:/clientsList";
	}
}
