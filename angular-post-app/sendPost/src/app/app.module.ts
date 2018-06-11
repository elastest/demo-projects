import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { MainComponent } from './main/main.component';
import { routedComponents, AppRoutingModule, appRoutes } from './app-routing.module';

@NgModule({
  declarations: [AppComponent, MainComponent, routedComponents],
  imports: [BrowserModule, appRoutes, AppRoutingModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
