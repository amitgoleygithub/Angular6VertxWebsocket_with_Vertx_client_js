package com.amit.vertx.MyVertxProject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class MainClass  extends AbstractVerticle
{
    public static void main( String[] args )
    {
    	Vertx vertx = Vertx.factory.vertx();
    	vertx.deployVerticle(new ServerVerticle());
    	//vertx.deployVerticle(new StockPriceWebSocketVerticle());
    	
    }
}
