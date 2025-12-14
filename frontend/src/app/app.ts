import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent {
  apiResponse = '';
  errorMessage = '';
  isCalling = false;

  constructor(private http: HttpClient, private keycloak: KeycloakService) {}

  callApi() {
    this.apiResponse = '';
    this.errorMessage = '';
    this.isCalling = true;
    const token = this.keycloak.getKeycloakInstance().token;
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    this.http.get('http://localhost:8081/api/private', { headers, responseType: 'text' })
      .subscribe({
        next: data => {
          this.apiResponse = data;
          this.isCalling = false;
        },
        error: err => {
          this.errorMessage = err.message || 'Erreur lors de l\'appel API';
          this.isCalling = false;
        }
      });
  }

  logout() {
    this.keycloak.logout();
  }
}