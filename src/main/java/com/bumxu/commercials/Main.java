package com.bumxu.commercials;

import java.util.List;

public class Main {

	public static void main(String[] args) {

		// Mirar coordenadas e id en el mapa y luego los detalles uno por uno
		// Problema: Se agrupan las coordendas cercanas
		List<Commercial> list = Idealista1.getCommercials("almeria-almeria");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		
		// Mirar id en la lista, página por página, y obtener detalles y coordendas uno por uno
		// Problema: Parecen tener algun tipo de sistema antibots
		//Idealista2 source = new Idealista2();
		//List<Commercial> list = source.getCommercials("adra-almeria");
		
	}

}
