package org.iesvdm.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor
@SuppressWarnings ("unused")
public class Comercial {
    private int id;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private Float comision;
}
