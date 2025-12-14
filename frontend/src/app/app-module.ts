import { NgModule, APP_INITIALIZER, PLATFORM_ID, inject } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { isPlatformBrowser } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { AppComponent } from './app';

function initializeKeycloak(keycloak: KeycloakService) {
  return () => {
    const platformId = inject(PLATFORM_ID);
    
    // Skip Keycloak initialization during SSR
    if (!isPlatformBrowser(platformId)) {
      return Promise.resolve();
    }
    
    return keycloak.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'demo-realm',
        clientId: 'angular-client'
      },
      initOptions: {
        onLoad: 'login-required',
        checkLoginIframe: false
      }
    });
  };
}

@NgModule({
  declarations: [],
  imports: [BrowserModule, HttpClientModule, KeycloakAngularModule, AppComponent],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }