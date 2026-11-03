package org.iesvdm.controlador;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.iesvdm.modelo.Cliente;
import org.iesvdm.servicios.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller 
public class ClienteController {
    
    @Autowired 
    private ClienteService clienteService;

    
    @GetMapping("/clientes")
    public String listar(Model model) {
        List<Cliente> clientes = clienteService.getAllClientes();
        model.addAttribute("clientes", clientes);
        return "clientes";
    }

    @GetMapping("/clientes/crear")
    public String crear(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "crear-cliente";
    }

    @PostMapping("/clientes/crear")
    public String submitCrear(@ModelAttribute("cliente") Cliente cliente, RedirectAttributes attributes) {
        clienteService.createCliente(cliente);
        attributes.addFlashAttribute("mensaje", "Cliente creado correctamente.");
        return "redirect:/clientes";
    }

    @GetMapping("/clientes/{id}")
    public String detalle(@PathVariable int id, Model model, RedirectAttributes attributes) {
        return clienteService.findCliente(id)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    return "detalle-cliente";
                })
                .orElseGet(() -> {
                    attributes.addFlashAttribute("error", "No se encontró el cliente solicitado.");
                    return "redirect:/clientes";
                });
    }

    @GetMapping("/clientes/editar/{id}")
    public String editar(@PathVariable int id, Model model, RedirectAttributes attributes) {
        return clienteService.findCliente(id)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    return "editar-cliente";
                })
                .orElseGet(() -> {
                    attributes.addFlashAttribute("error", "No se encontró el cliente solicitado.");
                    return "redirect:/clientes";
                });
    }

    @PostMapping("/clientes/editar/{id}")
    public String submitEditar(@PathVariable int id, @ModelAttribute("cliente") Cliente cliente,
            RedirectAttributes attributes) {
        cliente.setId(id);
        clienteService.updateCliente(cliente);
        attributes.addFlashAttribute("mensaje", "Cliente actualizado correctamente.");
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/borrar/{id}")
    public String borrar(@PathVariable int id, RedirectAttributes attributes) {
        try {
            clienteService.deleteCliente(id);
            attributes.addFlashAttribute("mensaje", "Cliente eliminado correctamente.");
        } catch (DataIntegrityViolationException exception) {
            attributes.addFlashAttribute("error", "No se puede eliminar el cliente porque tiene pedidos asociados.");
        }
        return "redirect:/clientes";
    }
}
