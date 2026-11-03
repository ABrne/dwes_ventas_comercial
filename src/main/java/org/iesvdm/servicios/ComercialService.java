package org.iesvdm.servicios;

import java.util.List;
import java.util.Optional;

import org.iesvdm.dao.ComercialDAO;
import org.iesvdm.modelo.Comercial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComercialService {

	@Autowired
	private ComercialDAO comercialDAO;

	public List<Comercial> getAllComerciales() {
		return comercialDAO.getAll();
	}

	public Optional<Comercial> findComercial(int id) {
		return comercialDAO.find(id);
	}

	public void createComercial(Comercial comercial) {
		comercialDAO.create(comercial);
	}

	public void updateComercial(Comercial comercial) {
		comercialDAO.update(comercial);
	}

	public void deleteComercial(int id) {
		comercialDAO.delete(id);
	}
}