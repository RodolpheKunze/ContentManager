import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpEventType, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { DocumentMetadata, DocumentUploadRequest } from '../models/document.model';

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private apiUrl = 'http://localhost:8080/api/documents';

  constructor(private http: HttpClient) { }

  uploadDocument(file: File, metadata: any): Observable<DocumentMetadata> {
    const formData = new FormData();
    
    // Add the file
    formData.append('file', file);
    
    // Add metadata as a JSON string
    const metadataJson = JSON.stringify(metadata);
    formData.append('metadata', metadataJson);

    return this.http.post<DocumentMetadata>(this.apiUrl, formData).pipe(
        catchError(this.handleError)
    );
}

  getDocumentById(id?: string): Observable<DocumentMetadata[]> {
    const url = id ? `${this.apiUrl}/${id}` : `${this.apiUrl}`;
    return this.http.get<DocumentMetadata[]>(url).pipe(
      catchError(this.handleError)
    );
  }

  getDocuments(query?: string, page: number = 0, size: number = 10): Observable<any> {
    let url = `${this.apiUrl}/search?page=${page}&size=${size}`;
    if (query) {
      url += `&query=${encodeURIComponent(query)}`;
    }
    return this.http.get(url);
  }

  downloadDocument(storageKey: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${storageKey}/download`, {
      responseType: 'blob'
    }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: any) {
    let errorMessage = 'An unknown error occurred!';
    console.log("error status:", error.status)
    if (error.status === 413) {
      errorMessage = 'File is too large. Maximum size allowed is 10MB.';
    } else if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      errorMessage = error.error?.message || errorMessage;
    }

    console.error('Error:', error);
    return throwError(() => ({ status: error.status, message: errorMessage }));
  }
  
}