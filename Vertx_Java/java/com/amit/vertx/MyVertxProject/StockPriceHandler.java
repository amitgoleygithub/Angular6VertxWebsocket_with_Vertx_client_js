package com.amit.vertx.MyVertxProject;

import java.util.Date;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class StockPriceHandler implements Handler<RoutingContext>{

	@Override
	public void handle(RoutingContext routingContext) {
	

		HttpServerResponse response = routingContext.response();
		
		JsonObject obj_1 = new JsonObject();
		obj_1.put("company", "Amazon");
		obj_1.put("datetime", new Date().toString());
		obj_1.put("price", 100);
		
		JsonObject obj_2 = new JsonObject();
		obj_2.put("company", "Google");
		obj_2.put("datetime", new Date().toString());
		obj_2.put("price", 1005);
		
		 JsonArray jsonarray = new JsonArray();
		 jsonarray.add(obj_1);
		 jsonarray.add(obj_2);

		
		routingContext.response()
								.putHeader("content-type", "application/json")
								.setStatusCode(200)
								.end(Json.encodePrettily(jsonarray));
		
		
	}
	
	

}
