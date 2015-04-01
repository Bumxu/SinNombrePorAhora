package com.bumxu.commercials;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;


public class Idealista2 {

	public static List<Commercial> getCommercials(String location) {
		List<Commercial> list = new ArrayList<Commercial>();
		
		try {
			getFirstPage();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//getPage(list, location);
		
		//if (list.size() > 0)
		//	getDetails(list);
		
		return list;
	}

	
	private static void getFirstPage() throws ParseException, ClientProtocolException, IOException
	{
		String url   = "http://www.idealista.com/venta-locales/almeria-almeria/";
		
		HttpResponse response = Request.Get(url).execute().returnResponse();
		
		int status = response.getStatusLine().getStatusCode();
		
		//if (status != 200) {
		///	return;
		//}
		
		String  html = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
		Document dom = Jsoup.parse(html);
		
		Elements pageCount = dom.select(".pagination:not(.next)"); 
		
		System.out.println(pageCount);
	}
	
	private void getResults(List<Commercial> list, String location) {
		String minArea = "";
		String maxArea = "";

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

				list.add(new Commercial(id, lat, lon, app));
				
			}
			
			System.out.println("Obtenidos " + list.size() + " lugares");

		} catch (UnknownHostException e) {
			System.out.println("No se puede conectar con el proveedor.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getDetails(List<Commercial> list) {
		
		Iterator<Commercial> it = list.iterator();
		
		System.out.print("Recuperando detalles <");

		while (it.hasNext()) {
			getDetailsFor(it.next());
			System.out.print("=");
		}
		
		System.out.print("> Terminado\n\n");

	}
	
	private void getDetailsFor(Commercial place)
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
