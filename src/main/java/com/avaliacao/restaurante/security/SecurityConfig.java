package com.avaliacao.restaurante.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.avaliacao.restaurante.repositorios.ClienteRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private LoginConcluido loginSucesso;
	
	@Bean
	public BCryptPasswordEncoder criptografarSenha() {
		BCryptPasswordEncoder criptografia = new BCryptPasswordEncoder();
		return criptografia;
	}
	
	@Override
	public UserDetailsService userDetailsServiceBean() throws Exception {
		DetalheClienteService detalheDoCliente = new DetalheClienteService(clienteRepository);
		return detalheDoCliente;
	}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
		.antMatchers("/clientsList").hasAnyAuthority("MODERADOR")
		.antMatchers("/pagamentosList").hasAnyAuthority("MODERADOR")
		.antMatchers("/pedidosList").hasAnyAuthority("MODERADOR")
		.antMatchers("/clientForm").permitAll()
		.antMatchers("/cadastrarPrato").hasAnyAuthority("MODERADOR")
		.antMatchers("/cadastroFormaDePagamento").hasAnyAuthority("MODERADOR")
		.antMatchers("/pagamentosList").hasAnyAuthority("MODERADOR")
		.antMatchers("/attachPerms/*").hasAnyAuthority("MODERADOR")
		.antMatchers("/pedido/checkout/*").hasAnyAuthority("MODERADOR","USUARIO")
		.antMatchers("/listaDePratos").hasAnyAuthority("MODERADOR", "USUARIO")
		.antMatchers("/index").hasAnyAuthority("MODERADOR", "USUARIO")
		.antMatchers("/atribuirPermissoes").hasAnyAuthority("MODERADOR")
		.anyRequest()
		.authenticated()
		.and()
		.exceptionHandling().accessDeniedPage("/acessoNegadoPage")
		.and()
		.formLogin().successHandler(loginSucesso)
		.loginPage("/paginaLogin").permitAll()
		.and()
		.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.logoutSuccessUrl("/paginaLogin").permitAll();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		UserDetailsService detalheDoCliente = userDetailsServiceBean();
		BCryptPasswordEncoder criptografia = criptografarSenha();
		auth.userDetailsService(detalheDoCliente).passwordEncoder(criptografia);
	}
}
