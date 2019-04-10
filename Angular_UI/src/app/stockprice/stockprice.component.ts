import { Component, OnInit , ViewChild, OnDestroy} from '@angular/core';
import { MatTableDataSource, MatSort, MatPaginator } from '@angular/material';
import {StockPriceService} from './stockprice.service';
import { Observable } from 'rxjs';
import * as io from 'socket.io-client';
import { StockPriceBean } from './StockPriceBean';

@Component({
  selector: 'app-stockprice',
  templateUrl: './stockprice.component.html',
  styleUrls: ['./stockprice.component.css']
})
export class StockpriceComponent implements OnInit, OnDestroy {

  @ViewChild(MatSort) sort: MatSort;

  displayedColumns: string[] = ['company','datetime','price'];
  dataSource = new MatTableDataSource();
  private eb;
  private url = 'http://localhost:8080/eventbus';
  connection;

  constructor(private stockPriceService: StockPriceService) { }

  ngOnInit() {
  
   //Websocket connection
    console.log("in init");
    this.stockPriceService.setUpEventBusClient();

    this.stockPriceService.stockPriceBeanArrayObservable.subscribe(
             stockData => {
              console.log("data received = "+stockData);
              this.dataSource.data = stockData;
              this.dataSource.sort = this.sort;
             }
      );

      console.log("in init completed");
  }

  //request response on button click
  getStockPrices(){
    console.log("calling service");
    this.stockPriceService.getStockPrice().subscribe(
      stockData => {
                     console.log('received stock data');
                     this.dataSource.data = stockData;
                     this.dataSource.sort = this.sort;
                    },
      error => {console.log("error = "+error)}
    );    

  }

  
  ngOnDestroy(): void {

    this.stockPriceService.closeEventBusClient();

}


}
