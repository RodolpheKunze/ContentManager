// src/app/app.component.ts
import { Component } from '@angular/core';
import { DocumentListComponent } from "./components/document-list/document-list.component";
import { DocumentUploadComponent } from "./document-upload/document-upload.component";
import { AppModule } from './app.module';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { HttpRequestInterceptor } from './interceptors/http.interceptor';


@Component({
  selector: 'app-root',
  template: `
    <div class="container">
      <h1>CMS Document Management</h1>
      <app-document-upload></app-document-upload>
      <app-document-list></app-document-list>
    </div>
  `,
  standalone: true, 
  imports: [DocumentListComponent, DocumentUploadComponent,AppModule],
  providers: [    {
    provide: HTTP_INTERCEPTORS,
    useClass: HttpRequestInterceptor,
    multi: true,
  }],
})
export class AppComponent {}