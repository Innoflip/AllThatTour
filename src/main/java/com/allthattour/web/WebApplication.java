package com.allthattour.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.allthattour.web.user.model.ERole;
import com.allthattour.web.user.model.Role;
import com.allthattour.web.user.repository.RoleRepository;

@SpringBootApplication
public class WebApplication {
	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initialize() {
		System.out.println("hello world, I have just started up");
		if (!roleRepository.findByName(ERole.ROLE_VISITOR).isPresent()) {
			roleRepository.save(new Role(ERole.ROLE_VISITOR));
		}
		if (!roleRepository.findByName(ERole.ROLE_USER).isPresent()) {
			roleRepository.save(new Role(ERole.ROLE_USER));
		}
	}

}
