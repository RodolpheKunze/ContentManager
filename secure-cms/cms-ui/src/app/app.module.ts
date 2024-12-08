// src/app/app.module.ts
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { DocumentListComponent } from './components/document-list/document-list.component';
import { DocumentUploadComponent } from './document-upload/document-upload.component';
import { HttpRequestInterceptor } from './interceptors/http.interceptor';
import { CommonModule } from '@angular/common';
import { DocumentService } from './service/document.service';


@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    FormsModule,
    DocumentListComponent,
    DocumentUploadComponent,
    
],
  providers: [DocumentService],
  bootstrap: [AppComponent]
})
export class AppModule { }