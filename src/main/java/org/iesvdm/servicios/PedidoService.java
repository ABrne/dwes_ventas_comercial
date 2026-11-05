package org.iesvdm.servicios;

import java.util.List;
import java.util.Optional;

import org.iesvdm.dao.PedidoDAO;
import org.iesvdm.modelo.Pedido;
import org.iesvdm.modelo.PedidoDetalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

	@Autowired
	private PedidoDAO pedidoDAO;

	public List<Pedido> getAllPedidos() {
		return pedidoDAO.getAll();
	}

	public Optional<Pedido> findPedido(int id) {
		return pedidoDAO.find(id);
	}

	public List<PedidoDetalle> getAllPedidosDetallados() {
		return pedidoDAO.getAllDetalles();
	}

	public Optional<PedidoDetalle> findPedidoDetallado(int id) {
		return pedidoDAO.findDetalle(id);
	}

	public void createPedido(Pedido pedido) {
		pedidoDAO.create(pedido);
	}

	public void updatePedido(Pedido pedido) {
		pedidoDAO.update(pedido);
	}

	public void deletePedido(int id) {
		pedidoDAO.delete(id);
	}
}