package org.iesvdm.controlador;

import org.iesvdm.modelo.Pedido;
import org.iesvdm.servicios.ClienteService;
import org.iesvdm.servicios.ComercialService;
import org.iesvdm.servicios.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/pedidos")
@Controller
public class PedidoController {

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ComercialService comercialService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("pedidos", pedidoService.getAllPedidosDetallados());
		return "pedidos";
	}

	@GetMapping("/{id}")
	public String detalle(@PathVariable int id, Model model, RedirectAttributes attributes) {
		return pedidoService.findPedidoDetallado(id)
				.map(pedido -> {
					model.addAttribute("pedido", pedido);
					return "detalle-pedido";
				})
				.orElseGet(() -> {
					attributes.addFlashAttribute("error", "No se encontró el pedido solicitado.");
					return "redirect:/pedidos";
				});
	}

	@GetMapping("/crear")
	public String crear(Model model) {
		model.addAttribute("pedido", new Pedido());
		cargarRelaciones(model);
		return "crear-pedido";
	}

	@PostMapping("/crear")
	public String submitCrear(@ModelAttribute("pedido") Pedido pedido, BindingResult bindingResult,
			Model model, RedirectAttributes attributes) {
		validarRelaciones(pedido, bindingResult);
		if (bindingResult.hasErrors()) {
			cargarRelaciones(model);
			return "crear-pedido";
		}
		pedidoService.createPedido(pedido);
		attributes.addFlashAttribute("mensaje", "Pedido creado correctamente.");
		return "redirect:/pedidos";
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable int id, Model model, RedirectAttributes attributes) {
		return pedidoService.findPedido(id)
				.map(pedido -> {
					model.addAttribute("pedido", pedido);
					cargarRelaciones(model);
					return "editar-pedido";
				})
				.orElseGet(() -> {
					attributes.addFlashAttribute("error", "No se encontró el pedido solicitado.");
					return "redirect:/pedidos";
				});
	}

	@PostMapping("/editar/{id}")
	public String submitEditar(@PathVariable int id, @ModelAttribute("pedido") Pedido pedido,
			BindingResult bindingResult, Model model, RedirectAttributes attributes) {
		pedido.setId(id);
		validarRelaciones(pedido, bindingResult);
		if (bindingResult.hasErrors()) {
			cargarRelaciones(model);
			return "editar-pedido";
		}
		pedidoService.updatePedido(pedido);
		attributes.addFlashAttribute("mensaje", "Pedido actualizado correctamente.");
		return "redirect:/pedidos";
	}

	@PostMapping("/borrar/{id}")
	public String borrar(@PathVariable int id, RedirectAttributes attributes) {
		pedidoService.deletePedido(id);
		attributes.addFlashAttribute("mensaje", "Pedido eliminado correctamente.");
		return "redirect:/pedidos";
	}

	private void cargarRelaciones(Model model) {
		model.addAttribute("clientes", clienteService.getAllClientes());
		model.addAttribute("comerciales", comercialService.getAllComerciales());
	}

	private void validarRelaciones(Pedido pedido, BindingResult bindingResult) {
		if (pedido.getClienteId() == null || clienteService.findCliente(pedido.getClienteId()).isEmpty()) {
			bindingResult.addError(new FieldError("pedido", "clienteId", pedido.getClienteId(), false,
					null, null, "Selecciona un cliente existente."));
		}
		if (pedido.getComercialId() == null || comercialService.findComercial(pedido.getComercialId()).isEmpty()) {
			bindingResult.addError(new FieldError("pedido", "comercialId", pedido.getComercialId(), false,
					null, null, "Selecciona un comercial existente."));
		}
	}
}