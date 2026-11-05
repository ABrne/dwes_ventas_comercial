package org.iesvdm.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.PedidoDetalle;
import org.iesvdm.modelo.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PedidoDAOImpl implements PedidoDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public synchronized void create(Pedido pedido) {
		String sqlInsert = "INSERT INTO pedido (total, fecha, id_cliente, id_comercial) VALUES (?, ?, ?, ?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		int rows = jdbcTemplate.update(connection -> {
			PreparedStatement statement = connection.prepareStatement(sqlInsert, new String[] { "id" });
			statement.setDouble(1, pedido.getTotal());
			statement.setObject(2, pedido.getFecha());
			statement.setInt(3, pedido.getClienteId());
			statement.setInt(4, pedido.getComercialId());
			return statement;
		}, keyHolder);

		pedido.setId(keyHolder.getKey().intValue());
		log.info("Insertados {} registros de Pedido.", rows);
	}

	@Override
	public List<Pedido> getAll() {
		List<Pedido> pedidos = jdbcTemplate.query(
				"SELECT id, total, fecha, id_cliente, id_comercial FROM pedido",
				(rs, rowNum) -> new Pedido(rs.getInt("id"), rs.getDouble("total"),
						rs.getObject("fecha", java.time.LocalDate.class), rs.getInt("id_cliente"),
						rs.getInt("id_comercial")));

		log.info("Devueltos {} registros de Pedido.", pedidos.size());
		return pedidos;
	}

	@Override
	public Optional<Pedido> find(int id) {
		List<Pedido> pedidos = jdbcTemplate.query(
				"SELECT id, total, fecha, id_cliente, id_comercial FROM pedido WHERE id = ?",
				(rs, rowNum) -> new Pedido(rs.getInt("id"), rs.getDouble("total"),
						rs.getObject("fecha", java.time.LocalDate.class), rs.getInt("id_cliente"),
						rs.getInt("id_comercial")),
				id);

		if (pedidos.isEmpty()) {
			log.info("Pedido no encontrado.");
		}
		return pedidos.stream().findFirst();
	}

	@Override
	public List<PedidoDetalle> getAllDetalles() {
		List<PedidoDetalle> pedidos = jdbcTemplate.query("""
				SELECT p.id, p.total, p.fecha, p.id_cliente AS cliente_id,
				       p.id_comercial AS comercial_id,
				       CONCAT_WS(' ', c.nombre, c.apellido1, c.apellido2) AS nombre_cliente,
				       CONCAT_WS(' ', co.nombre, co.apellido1, co.apellido2) AS nombre_comercial
				FROM pedido p
				JOIN cliente c ON p.id_cliente = c.id
				JOIN comercial co ON p.id_comercial = co.id
				""",
				(rs, rowNum) -> new PedidoDetalle(rs.getInt("id"), rs.getDouble("total"),
						rs.getObject("fecha", java.time.LocalDate.class), rs.getInt("cliente_id"),
						rs.getInt("comercial_id"), rs.getString("nombre_cliente"),
						rs.getString("nombre_comercial")));

		log.info("Devueltos {} registros detallados de Pedido.", pedidos.size());
		return pedidos;
	}

	@Override
	public Optional<PedidoDetalle> findDetalle(int id) {
		List<PedidoDetalle> pedidos = jdbcTemplate.query("""
				SELECT p.id, p.total, p.fecha, p.id_cliente AS cliente_id,
				       p.id_comercial AS comercial_id,
				       CONCAT_WS(' ', c.nombre, c.apellido1, c.apellido2) AS nombre_cliente,
				       CONCAT_WS(' ', co.nombre, co.apellido1, co.apellido2) AS nombre_comercial
				FROM pedido p
				JOIN cliente c ON p.id_cliente = c.id
				JOIN comercial co ON p.id_comercial = co.id
				WHERE p.id = ?
				""",
				(rs, rowNum) -> new PedidoDetalle(rs.getInt("id"), rs.getDouble("total"),
						rs.getObject("fecha", java.time.LocalDate.class), rs.getInt("cliente_id"),
						rs.getInt("comercial_id"), rs.getString("nombre_cliente"),
						rs.getString("nombre_comercial")),
				id);

		return pedidos.stream().findFirst();
	}

	@Override
	public void update(Pedido pedido) {
		int rows = jdbcTemplate.update(connection -> {
			PreparedStatement statement = connection.prepareStatement(
					"UPDATE pedido SET total = ?, fecha = ?, id_cliente = ?, id_comercial = ? WHERE id = ?");
			statement.setDouble(1, pedido.getTotal());
			statement.setObject(2, pedido.getFecha());
			statement.setInt(3, pedido.getClienteId());
			statement.setInt(4, pedido.getComercialId());
			statement.setInt(5, pedido.getId());
			return statement;
		});

		log.info("Update de Pedido con {} registros actualizados.", rows);
	}

	@Override
	public void delete(int id) {
		int rows = jdbcTemplate.update("DELETE FROM pedido WHERE id = ?", id);
		log.info("Delete de Pedido con {} registros eliminados.", rows);
	}
}