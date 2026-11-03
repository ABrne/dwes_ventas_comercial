package org.iesvdm.servicios;

import java.util.List;
import java.util.Optional;

import org.iesvdm.dao.ClienteDAO;
import org.iesvdm.modelo.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service 
public class ClienteService {
    
    @Autowired 
    private ClienteDAO clienteDAO;

    public List<Cliente> getAllClientes() {
        return clienteDAO.getAll();
    }

    public Optional<Cliente> findCliente(int id) {
        return clienteDAO.find(id);
    }

    public void createCliente(Cliente cliente) {
        clienteDAO.create(cliente);
    }

    public void updateCliente(Cliente cliente) {
        clienteDAO.update(cliente);
    }

    public void deleteCliente(int id) {
        clienteDAO.delete(id);
    }

}
