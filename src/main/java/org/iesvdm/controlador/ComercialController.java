package org.iesvdm.controlador;

import java.util.List;

import org.iesvdm.modelo.Comercial;
import org.iesvdm.servicios.ComercialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/comerciales")
@Controller
public class ComercialController {

	@Autowired
	private ComercialService comercialService;

	@GetMapping
	public String listar(Model model) {
		List<Comercial> comerciales = comercialService.getAllComerciales();
		model.addAttribute("comerciales", comerciales);
		return "comerciales";
	}

	@GetMapping("/crear")
	public String crear(Model model) {
		model.addAttribute("comercial", new Comercial());
		return "crear-comercial";
	}

	@PostMapping("/crear")
	public String submitCrear(@ModelAttribute("comercial") Comercial comercial, RedirectAttributes attributes) {
		comercialService.createComercial(comercial);
		attributes.addFlashAttribute("mensaje", "Comercial creado correctamente.");
		return "redirect:/comerciales";
	}

	@GetMapping("/{id}")
	public String detalle(@PathVariable int id, Model model, RedirectAttributes attributes) {
		return comercialService.findComercial(id)
				.map(comercial -> {
					model.addAttribute("comercial", comercial);
					return "detalle-comercial";
				})
				.orElseGet(() -> {
					attributes.addFlashAttribute("error", "No se encontró el comercial solicitado.");
					return "redirect:/comerciales";
				});
	}

	@GetMapping("/editar/{id}")
	public String editar(@PathVariable int id, Model model, RedirectAttributes attributes) {
		return comercialService.findComercial(id)
				.map(comercial -> {
					model.addAttribute("comercial", comercial);
					return "editar-comercial";
				})
				.orElseGet(() -> {
					attributes.addFlashAttribute("error", "No se encontró el comercial solicitado.");
					return "redirect:/comerciales";
				});
	}

	@PostMapping("/editar/{id}")
	public String submitEditar(@PathVariable int id, @ModelAttribute("comercial") Comercial comercial,
			RedirectAttributes attributes) {
		comercial.setId(id);
		comercialService.updateComercial(comercial);
		attributes.addFlashAttribute("mensaje", "Comercial actualizado correctamente.");
		return "redirect:/comerciales";
	}

	@PostMapping("/borrar/{id}")
	public String borrar(@PathVariable int id, RedirectAttributes attributes) {
		try {
			comercialService.deleteComercial(id);
			attributes.addFlashAttribute("mensaje", "Comercial eliminado correctamente.");
		} catch (DataIntegrityViolationException exception) {
			attributes.addFlashAttribute("error", "No se puede eliminar el comercial porque tiene pedidos asociados.");
		}
		return "redirect:/comerciales";
	}
}
