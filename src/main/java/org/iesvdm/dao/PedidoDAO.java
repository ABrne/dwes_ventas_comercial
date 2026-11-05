package org.iesvdm.dao;

import java.util.List;
import java.util.Optional;

import org.iesvdm.modelo.PedidoDetalle;
import org.iesvdm.modelo.Pedido;

public interface PedidoDAO {
	public void create(Pedido pedido);
	public List<Pedido> getAll();
	public Optional<Pedido> find(int id);
	public List<PedidoDetalle> getAllDetalles();
	public Optional<PedidoDetalle> findDetalle(int id);
	public void update(Pedido pedido);
	public void delete(int id);
}