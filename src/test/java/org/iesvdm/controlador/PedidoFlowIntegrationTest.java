package org.iesvdm.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.iesvdm.dao.ClienteDAO;
import org.iesvdm.dao.ComercialDAO;
import org.iesvdm.dao.PedidoDAO;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.modelo.Comercial;
import org.iesvdm.modelo.Pedido;
import org.iesvdm.ventas.VentasApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = VentasApplication.class, properties = {
		"spring.datasource.url=jdbc:h2:mem:ventas-flow;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.sql.init.mode=always",
		"spring.sql.init.schema-locations=classpath:schema-test.sql"
})
@Transactional
class PedidoFlowIntegrationTest {

	@Autowired
	private WebApplicationContext applicationContext;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ClienteDAO clienteDAO;

	@Autowired
	private ComercialDAO comercialDAO;

	@Autowired
	private PedidoDAO pedidoDAO;

	private MockMvc mockMvc;

	@BeforeEach
	void prepararMockMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
	}

	@Test
	void creaEditaYConsultaPedidoConNombresCompletos() throws Exception {
		Cliente clienteOriginal = crearCliente("Inés", "Vega", null);
		Comercial comercialOriginal = crearComercial("Luis", "Mora", null);
		Cliente clienteNuevo = crearCliente("Aarón", "Rivero", "López");
		Comercial comercialNuevo = crearComercial("Marta", "Herrera", "Gil");

		mockMvc.perform(post("/pedidos/crear")
				.param("total", "245.75")
				.param("fecha", "2026-11-05")
				.param("clienteId", Integer.toString(clienteOriginal.getId()))
				.param("comercialId", Integer.toString(comercialOriginal.getId())))
			.andExpect(status().is3xxRedirection());

		Integer pedidoId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM pedido", Integer.class);
		assertNotNull(pedidoId);

		mockMvc.perform(get("/pedidos"))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Inés Vega")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Luis Mora")))
			.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("null"))));

		mockMvc.perform(get("/pedidos/{id}", pedidoId))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Inés Vega")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Luis Mora")));

		mockMvc.perform(get("/pedidos/editar/{id}", pedidoId))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.containsString(
					"value=\"" + clienteOriginal.getId() + "\" selected=\"selected\"")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString(
					"value=\"" + comercialOriginal.getId() + "\" selected=\"selected\"")));

		mockMvc.perform(post("/pedidos/editar/{id}", pedidoId)
				.param("total", "310.50")
				.param("fecha", "2026-11-06")
				.param("clienteId", Integer.toString(clienteNuevo.getId()))
				.param("comercialId", Integer.toString(comercialNuevo.getId())))
			.andExpect(status().is3xxRedirection());

		assertEquals(clienteNuevo.getId(), jdbcTemplate.queryForObject(
				"SELECT id_cliente FROM pedido WHERE id = ?", Integer.class, pedidoId));
		assertEquals(comercialNuevo.getId(), jdbcTemplate.queryForObject(
				"SELECT id_comercial FROM pedido WHERE id = ?", Integer.class, pedidoId));
		assertEquals(LocalDate.of(2026, 11, 6), jdbcTemplate.queryForObject(
				"SELECT fecha FROM pedido WHERE id = ?", LocalDate.class, pedidoId));

		mockMvc.perform(get("/pedidos/{id}", pedidoId))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Aarón Rivero López")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Marta Herrera Gil")));
	}

	@Test
	void rechazaIdsInvalidosAlCrearYEditar() throws Exception {
		Cliente cliente = crearCliente("Eva", "Soto", null);
		Comercial comercial = crearComercial("Pablo", "Rey", null);

		mockMvc.perform(get("/pedidos/crear"))
			.andExpect(status().isOk())
			.andExpect(view().name("crear-pedido"));

		mockMvc.perform(post("/pedidos/crear")
				.param("total", "20")
				.param("fecha", "2026-11-05"))
			.andExpect(status().isOk())
			.andExpect(view().name("crear-pedido"))
			.andExpect(model().attributeHasFieldErrors("pedido", "clienteId", "comercialId"));

		mockMvc.perform(post("/pedidos/crear")
				.param("total", "20")
				.param("fecha", "2026-11-05")
				.param("clienteId", "99999")
				.param("comercialId", "99999"))
			.andExpect(status().isOk())
			.andExpect(view().name("crear-pedido"))
			.andExpect(model().attributeHasFieldErrors("pedido", "clienteId", "comercialId"));
		assertEquals(0, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pedido", Integer.class));

		Pedido pedido = new Pedido(0, 20.0, LocalDate.of(2026, 11, 5), cliente.getId(), comercial.getId());
		pedidoDAO.create(pedido);

		mockMvc.perform(post("/pedidos/editar/{id}", pedido.getId())
				.param("total", "25")
				.param("fecha", "2026-11-06")
				.param("clienteId", "99999")
				.param("comercialId", "99999"))
			.andExpect(status().isOk())
			.andExpect(view().name("editar-pedido"))
			.andExpect(model().attributeHasFieldErrors("pedido", "clienteId", "comercialId"));

		assertEquals(cliente.getId(), jdbcTemplate.queryForObject(
				"SELECT id_cliente FROM pedido WHERE id = ?", Integer.class, pedido.getId()));
		assertEquals(comercial.getId(), jdbcTemplate.queryForObject(
				"SELECT id_comercial FROM pedido WHERE id = ?", Integer.class, pedido.getId()));
	}

	@Test
	void lasClavesForaneasImpidenBorrarPersonasConPedidos() throws Exception {
		Cliente cliente = crearCliente("Clara", "Gil", null);
		Comercial comercial = crearComercial("Mario", "León", null);
		Pedido pedido = new Pedido(0, 50.0, LocalDate.of(2026, 11, 5), cliente.getId(), comercial.getId());
		pedidoDAO.create(pedido);

		MvcResult resultadoCliente = mockMvc.perform(post("/clientes/borrar/{id}", cliente.getId()))
				.andExpect(status().is3xxRedirection())
				.andReturn();
		assertEquals("No se puede eliminar el cliente porque tiene pedidos asociados.",
				resultadoCliente.getFlashMap().get("error"));

		MvcResult resultadoComercial = mockMvc.perform(post("/comerciales/borrar/{id}", comercial.getId()))
				.andExpect(status().is3xxRedirection())
				.andReturn();
		assertEquals("No se puede eliminar el comercial porque tiene pedidos asociados.",
				resultadoComercial.getFlashMap().get("error"));

		assertEquals(1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pedido WHERE id = ?", Integer.class,
				pedido.getId()));
		assertEquals(1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cliente WHERE id = ?", Integer.class,
				cliente.getId()));
		assertEquals(1, jdbcTemplate.queryForObject("SELECT COUNT(*) FROM comercial WHERE id = ?", Integer.class,
				comercial.getId()));
		assertFalse(jdbcTemplate.queryForObject(
				"SELECT COUNT(*) = 0 FROM pedido WHERE id_cliente = ? AND id_comercial = ?",
				Boolean.class, cliente.getId(), comercial.getId()));
	}

	private Cliente crearCliente(String nombre, String apellido1, String apellido2) {
		Cliente cliente = new Cliente(0, nombre, apellido1, apellido2, "Sevilla", 100);
		clienteDAO.create(cliente);
		return cliente;
	}

	private Comercial crearComercial(String nombre, String apellido1, String apellido2) {
		Comercial comercial = new Comercial(0, nombre, apellido1, apellido2, 0.1f);
		comercialDAO.create(comercial);
		return comercial;
	}
}