package com.bumxu.commercials;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Idealista1 {

	public static List<Commercial> getCommercials(String location) {
		List<Commercial> list = new ArrayList<Commercial>();
		
		getResults(list, location);
		
		getDetails(list);
		
		return list;
	}

	
	
	private static void getResults(List<Commercial> list, String location) {
		String minArea = "60";
		String maxArea = "100";

		String url = "http://www.idealista.com/ajax/listingcontroller/listingmapajax.ajax"
				+ "?adfilter_pricemin=default"
				+ "&adfilter_price=default"
				+ "&adfilter_area=" + minArea
				+ "&adfilter_areamax=" + maxArea
				+ "&adfilter_hasheating="
				+ "&adfilter_iscornerlocated="
				+ "&adfilter_hasairconditioning="
				+ "&adfilter_hassmokeextractor="
				+ "&adfilter_agencyisabank="
				+ "&adfilter_totalpictures="
				+ "&adfilter_totalprofessionalvideos="
				+ "&adfilter_totalvirtualtours="
				+ "&adfilter_buildpurpose=default"
				+ "&adfilter_ubication=default"
				+ "&adfilter_published=default"
				+ "&onlySavedAds=false"
				+ "&locationUri=" + location
				+ "&typology=6"
				+ "&operation=1"
				+ "&freeText=";

		
		try {
			JSONObject json;
			String     response = Request.Get(url).execute().returnContent().asString();

			try {
				if (response.length() == 0)
					throw new Exception("La respuesta del proveedor está vacía.");

				json = new JSONObject(response);

				if (!json.getString("error").isEmpty())
					throw new Exception("La respuesta del proveedor contiene un error.");

			} catch (Exception e) {
				System.out.println(e.getMessage());
				return;
			}
			
			JSONArray results;

			try {
				results = json.getJSONObject("jsonResponse")
						.getJSONObject("map").getJSONArray("items");
			} catch (JSONException e) {
				return;
			}
			
			JSONObject place;
			String id;
			float lat, lon;
			boolean app;

			for (int i = 0; i < results.length(); i++) {
				
				place = results.getJSONObject(i);
				
				id  = Integer.toString(place.getInt("adId"));
				lat = (float) place.getDouble("latitude");
				lon = (float) place.getDouble("longitude");
				app = (place.getInt("iconType") == 1) ? true : false;

				if (place.getInt("iconType") != 6) {  // Coordenadas contraidas :(
					list.add(new Commercial(id, lat, lon, app));
				}
				
			}
			
			System.out.println("Obtenidos " + list.size() + " lugares");

		} catch (UnknownHostException e) {
			System.out.println("No se puede conectar con el proveedor.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void getDetails(List<Commercial> list) {
		
		Iterator<Commercial> it = list.iterator();
		
		System.out.print("Recuperando detalles <");

		while (it.hasNext()) {
			getDetailsFor(it.next());
			System.out.print("=");
		}
		
		System.out.print("> Terminado\n\n");

	}
	
	private static void getDetailsFor(Commercial place)
	{
		String url = "http://www.idealista.com/ajax/mapitemdetail.ajax?adId=" + place.getId() + "&operationId=1&typologyId=6";
		
		try {
			JSONObject json;
			String     response = Request.Get(url).execute().returnContent().asString();

			try {
				if (response.length() == 0)
					throw new Exception("La respuesta del proveedor está vacía.");

				json = new JSONObject(response);

				if (!json.getString("error").isEmpty())
					throw new Exception("La respuesta del proveedor contiene un error.");

			} catch (Exception e) {
				System.out.println(e.getMessage());
				return;
			}

			String html = json.getString("plainText");

			Document dom = Jsoup.parse(html);
			
			place.setPrice(Float.parseFloat(dom.select(".item-price").text().replaceAll("[.€]", "")));
			place.setArea(Float.parseFloat(dom.select(".item-detail").eq(0).text().replace(" m²", "")));
			place.setAddress(dom.select(".item-link").text().replaceFirst("(local|nave|local o nave) en ", ""));
			
		} catch (UnknownHostException e) {
			System.out.println("No se puede conectar con el proveedor.");
		} catch (HttpResponseException e){
			System.out.println("Error accediendio a " + url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
