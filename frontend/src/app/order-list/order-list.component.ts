import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { pluck } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { CocktailDto } from '../shared/cocktail-dto';

@Component({
  selector: 'app-cocktail-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss'],
})
export class OrderListComponent implements OnInit {
  orders$: Observable<CocktailDto[]>;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.orders$ = this.route.data.pipe(pluck('orders'));
  }
}
