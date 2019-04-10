package com.amit.vertx.MyVertxProject;

import java.util.Date;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;

public class ServerVerticle extends AbstractVerticle{


	
	@Override
	public void start() throws Exception {

		try {
			
			HttpServer httpServer = vertx.createHttpServer();
			//Main router
			Router router = Router.router(vertx);		
			   //A cross origin request
				CorsHandler corsHandler = CorsHandler.create("http://localhost:4200"); 
				corsHandler.allowedMethod(HttpMethod.OPTIONS);
	            corsHandler.allowedMethod(HttpMethod.GET);
	            corsHandler.allowedMethod(HttpMethod.POST);
	            corsHandler.allowedMethod(HttpMethod.DELETE); 	
	            corsHandler.allowedHeader("x-requested-with");
	            corsHandler.allowedHeader("Access-Control-Allow-Origin");
	            corsHandler.allowedHeader("Origin");
	            corsHandler.allowedHeader("Content-Type");
	            corsHandler.allowedHeader("Accept");
	            corsHandler.allowedHeader("Access-Control-Allow-Credentials");
	            corsHandler.allowedHeader("Access-Control-Allow-Headers");
	            corsHandler.allowCredentials(true);
	            
            router.route().handler(corsHandler);	            
			router.route().handler(BodyHandler.create());			
			//for session handling					
			SessionHandler sessionHandler = SessionHandler.create(LocalSessionStore.create(vertx));
			router.route().handler(sessionHandler);
			
			router.get("/").handler(this::sayHelloRoot);             // handler in same class
			router.get("/hello/:date").handler(new HelloHandler()); // handler as a seperate class			
			router.get("/stockprice").handler(new StockPriceHandler()); // handler as a seperate class
			

	        // Allow events for the designated addresses in/out of the event bus bridge
	        BridgeOptions opts = new BridgeOptions()
	               // .addInboundPermitted(new PermittedOptions().setAddress("chat.to.server"))
	                .addOutboundPermitted(new PermittedOptions().setAddress("stock.price.event.bus"));

	        // Create the event bus bridge and add it to the router.
	            SockJSHandlerOptions handlerOptions = new SockJSHandlerOptions().setHeartbeatInterval(5000);
		        SockJSHandler ebHandler = SockJSHandler.create(vertx,handlerOptions).bridge(opts, event -> {
																            if (event.type() == BridgeEventType.SOCKET_CREATED)														
																                System.out.println("A socket was created.");
																            if (event.type() == BridgeEventType.PUBLISH)
																            	 System.out.println("A message has been published to the event bus bridge.");
																            event.complete(true);
																        });
		        router.route("/eventbus/*").handler(ebHandler);
		        router.route("/socket.io/*").handler(ebHandler);
	
		        EventBus eventBus = vertx.eventBus();
		        
		        vertx.setPeriodic(5000, x->{  
		        	
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
							    				 
		        	                    eventBus.publish("stock.price.event.bus", Json.encodePrettily(jsonarray));
		        	                   System.out.println("data sent");;
		                          });
		        
		   
/*		    httpServer.websocketHandler(new Handler<ServerWebSocket>(){
		    	
		    	public void handle(final ServerWebSocket ws) {
		    		
		    	try {	 
		    		 vertx.setPeriodic(5000, x->{  
		    			 
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

		    			 	 
		    			 	 
		    			 ws.writeTextMessage(Json.encodePrettily(jsonarray));
 	                   
                   });
		    		 
		    	}catch(Exception e) {
		    		
		    		ws.close();
		    		
		    		System.out.println("websocket connection closed"+e.getMessage());
		    	}
 
		    	}
		    	
		    	
		    });*/
		        
			
			httpServer.requestHandler(router::accept).listen(8080);
			
			System.out.println("Server Started");			   
			
			
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void sayHelloRoot (RoutingContext routingContext) {
		
		HttpServerResponse response = routingContext.response();
		JsonObject obj = new JsonObject();
		obj.put("helloKeyRoot", "helloValueRoot");
		
		routingContext.response()
								.putHeader("content-type", "application/json")
								.setStatusCode(200)
								.end(Json.encodePrettily(obj));
		
		
	}
	
}
