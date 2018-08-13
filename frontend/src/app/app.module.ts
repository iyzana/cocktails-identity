import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';
import { IdInputComponent } from './id-input/id-input.component';
import { FormsModule } from '@angular/forms';
import { OrderListComponent } from './order-list/order-list.component';
import { OrderListResolverService } from './order-list/order-list-resolver.service';
import { HttpClientModule } from '@angular/common/http';
import { OrderGuard } from './shared/order-guard.service';

const routes = [
  {
    path: 'orders',
    component: OrderListComponent,
    canActivate: [OrderGuard],
    resolve: { orders: OrderListResolverService },
  },
  { path: '', component: IdInputComponent, pathMath: 'full' },
];

@NgModule({
  declarations: [AppComponent, IdInputComponent, OrderListComponent],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    FontAwesomeModule,
    FormsModule,
    HttpClientModule,
  ],
  providers: [OrderListResolverService, OrderGuard],
  bootstrap: [AppComponent],
})
export class AppModule {}
