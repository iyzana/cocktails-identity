import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { pluck } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { CocktailDto } from '../shared/cocktail-dto';
import { cocktailNames } from '../shared/cocktail-names';

@Component({
  selector: 'app-cocktail-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss'],
})
export class OrderListComponent implements OnInit {
  orders$: Observable<CocktailDto[]>;
  cocktailNames = cocktailNames;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.orders$ = this.route.data.pipe(pluck('orders'));
  }
}
