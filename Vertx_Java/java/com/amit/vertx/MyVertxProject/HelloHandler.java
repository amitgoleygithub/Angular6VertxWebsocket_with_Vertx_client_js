package com.amit.vertx.MyVertxProject;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class HelloHandler implements Handler<RoutingContext>{

	@Override
	public void handle(RoutingContext routingContext) {
	

		HttpServerResponse response = routingContext.response();
		String dateParam = routingContext.request().getParam("date");
		JsonObject obj = new JsonObject();
		obj.put("helloKeyHandler", "helloValueHandler");
		obj.put("dateParam", dateParam);
		
		routingContext.response()
								.putHeader("content-type", "application/json")
								.setStatusCode(200)
								.end(Json.encodePrettily(obj));
		
		
	}
	
	

}
