import { Injectable } from '@angular/core';
import { Observable, Observer, Subject, of } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http'; 
import { StockPriceBean } from './StockPriceBean';
import * as Rx from "rxjs/Rx";
import * as EventBus from 'vertx3-eventbus-client';



@Injectable({
    providedIn: 'root'
  })
  export class StockPriceService {

    stockPriceBeanArray : Observable<StockPriceBean[]>;
    private subject: Rx.Subject<MessageEvent>;
    private readonly eventBus: EventBus;

    public readonly stockPriceBeanArrayObservable : Observable<StockPriceBean[]>;
    private stockPriceBeanArrayObserver : Observer<StockPriceBean[]>;

    url = "http://localhost:8080/eventbus";

    constructor(private http: HttpClient) { 

          const options = {
            vertxbus_reconnect_attempts_max: Infinity, // Max reconnect attempts
            vertxbus_reconnect_delay_min: 10000, // Initial delay (in ms) before first reconnect attempt
            vertxbus_reconnect_delay_max: 5000, // Max delay (in ms) between reconnect attempts
            vertxbus_reconnect_exponent: 2, // Exponential backoff factor
            vertxbus_randomization_factor: 0.5 // Randomization factor between 0 and 1
        };

        this.eventBus = new EventBus(this.url, options);
        // we need the "self" constant because we cannot use "this" inside the function below
        const self = this;
        this.stockPriceBeanArrayObservable = Observable.create(function(observer: Observer<StockPriceBean[]>) {
                    self.stockPriceBeanArrayObserver = observer;
                    });
    }//constructor closed   


 
    //function
    getStockPrice() : Observable<StockPriceBean[]>{

        let url = "http://localhost:8080/stockprice";
         
        return this.http.get<Array<StockPriceBean>>(url, {responseType : 'json'});
    
     }


      getMessages(): Rx.Subject<MessageEvent> {

      let url = "ws://localhost:8080/eventbus";
      let ws = new WebSocket(url);
  
      let observable = Rx.Observable.create((obs: Rx.Observer<MessageEvent>) => {
        ws.onmessage = obs.next.bind(obs);
        ws.onerror = obs.error.bind(obs);
        ws.onclose = obs.complete.bind(obs);
        return ws.close.bind(ws);
      });
      let observer = {
        next: (data: Object) => {

          console.log("data="+data);
          if (ws.readyState === WebSocket.OPEN) {
            ws.send(JSON.stringify(data));
          }
        }
      };
      return Rx.Subject.create(observer, observable);
    }


    public setUpEventBusClient(): void {

      console.log('Starting setUpEventBusClient');
      // we need the "self" constant because we cannot use "this" inside the function below
      const self = this;

      this.eventBus.onopen = function() {

              console.log('onopen function started');
              self.eventBus.registerHandler('stock.price.event.bus', function(error, message) {

                      console.log('Received a message: ' + message);
                      console.log('Type of "error" variable: ' + typeof(error)); // object

                      if (error == null) {
                          console.log('"error" variable is null');
                      } else {
                          for (const property of Object.keys(error)) {
                              console.log('The "' + property + '" property value of "error" object is: ' + error[property]);
                          }
                      }

                      console.log('Type of "message" variable: ' + typeof(message)); // object

                      if (message == null) {
                          console.log('The "message" variable is null');
                      } else {
                          for (const property of Object.keys(message)) {
                              console.log('The "' + property + '" property value of "message" object is: ' + message[property]);
                          }
                      }

                      console.log('Type of "message.body" variable: ' + typeof(message.body)); // object
                      console.log('Received message: ' + JSON.stringify(message.body));

                      var ArrayOfResult: StockPriceBean[] = JSON.parse(message.body);

                      console.log("ArrayOfResult = "+ArrayOfResult);

                      self.stockPriceBeanArrayObserver.next(ArrayOfResult);

              });

      };



      this.eventBus.enableReconnect(true);

  }

  public closeEventBusClient(): void {

    this.eventBus.close();

    console.log('Connection to the Vert.x event bus has been closed');

}

  }  