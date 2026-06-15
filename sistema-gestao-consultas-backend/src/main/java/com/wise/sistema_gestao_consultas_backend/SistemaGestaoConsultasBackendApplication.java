package com.wise.sistema_gestao_consultas_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemaGestaoConsultasBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaGestaoConsultasBackendApplication.class, args);
	}

}
