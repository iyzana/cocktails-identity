import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CocktailDto } from '../shared/cocktail-dto';

@Injectable()
export class OrderListResolverService implements Resolve<CocktailDto[]> {
  constructor(private http: HttpClient) {}

  resolve(): Observable<CocktailDto[]> {
    const person = localStorage.getItem('cocktails.identity.id');

    return this.http
      .get<CocktailDto[]>(`/api/orders/${person}`)
      .pipe(catchError(() => of([])));
  }
}
