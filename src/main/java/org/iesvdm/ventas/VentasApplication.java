package org.iesvdm.ventas;

import org.iesvdm.dao.ClienteDAO;
import org.iesvdm.dao.ComercialDAO;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.Comercial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@SpringBootApplication(scanBasePackages = "org.iesvdm")
@Slf4j
public class VentasApplication implements CommandLineRunner {

	@Autowired
	private ClienteDAO clienteDAO;

	@Autowired
	private ComercialDAO comercialDAO;

	public static void main(String[] args) {
		SpringApplication.run(VentasApplication.class, args);
	}

	@Override
	public void run(String... args) {
		probarClienteDAO();
		probarComercialDAO();
	}

	private void probarClienteDAO() {
		log.info("Pruebas de ClienteDAO");
		Cliente cliente = new Cliente(0, "Prueba", "Cliente", "Temporal", "Pruebas", 100);
		clienteDAO.create(cliente);
		try {
			clienteDAO.getAll().forEach(registro -> log.info("Cliente: {}", registro));
			Optional<Cliente> encontrado = clienteDAO.find(cliente.getId());
			if (encontrado.isEmpty()) {
				throw new IllegalStateException("No se encontró el Cliente creado para la prueba.");
			}

			cliente.setNombre("Cliente actualizado");
			clienteDAO.update(cliente);
			if (!"Cliente actualizado".equals(clienteDAO.find(cliente.getId()).orElseThrow().getNombre())) {
				throw new IllegalStateException("No se actualizó el Cliente de prueba.");
			}
		} finally {
			clienteDAO.delete(cliente.getId());
		}
		if (clienteDAO.find(cliente.getId()).isPresent()) {
			throw new IllegalStateException("No se eliminó el Cliente de prueba.");
		}
	}

	private void probarComercialDAO() {
		log.info("Pruebas de ComercialDAO");
		Comercial comercial = new Comercial(0, "Prueba", "Comercial", "Temporal", 0.1f);
		comercialDAO.create(comercial);
		try {
			comercialDAO.getAll().forEach(registro -> log.info("Comercial: {}", registro));
			if (comercialDAO.find(comercial.getId()).isEmpty()) {
				throw new IllegalStateException("No se encontró el Comercial creado para la prueba.");
			}

			comercial.setNombre("Comercial actualizado");
			comercial.setComision(0.2f);
			comercialDAO.update(comercial);
			Comercial actualizado = comercialDAO.find(comercial.getId()).orElseThrow();
			if (!"Comercial actualizado".equals(actualizado.getNombre()) || actualizado.getComision() != 0.2f) {
				throw new IllegalStateException("No se actualizó el Comercial de prueba.");
			}
		} finally {
			comercialDAO.delete(comercial.getId());
		}
		if (comercialDAO.find(comercial.getId()).isPresent()) {
			throw new IllegalStateException("No se eliminó el Comercial de prueba.");
		}
	}
}
