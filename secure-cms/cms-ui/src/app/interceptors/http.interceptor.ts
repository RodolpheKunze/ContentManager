import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class HttpRequestInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Add any headers or authentication tokens here if needed
    const modifiedReq = req.clone({
      headers: req.headers.set('Accept', 'application/json'),
    });
    return next.handle(modifiedReq);
  }
}