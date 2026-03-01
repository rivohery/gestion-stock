import { Injectable, inject } from '@angular/core';
import { HandleErrorService } from '../../../shared/services/handle-error.service';
import { environment } from '../../../../environments/environment.dev';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Category } from '../models/product.model';
import { Observable, catchError, take } from 'rxjs';
import { GlobalResponse } from '../../../shared/models/shared.model';

@Injectable({
  providedIn: 'root',
})
export class CategoryService extends HandleErrorService {
  categoryUrl: string = `${environment.server_url}/admin`;
  http = inject(HttpClient);

  create(request: Category): Observable<Category> {
    return this.http
      .post<Category>(`${this.categoryUrl}/categories`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  findAll(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.categoryUrl}/categories`).pipe(
      take(1),
      catchError((err) => this.handleError(err))
    );
  }

  findById(categoryId: number): Observable<Category> {
    return this.http
      .get<Category>(`${this.categoryUrl}/category/${categoryId}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  deleteById(categoryId: number): Observable<GlobalResponse> {
    return this.http
      .delete<GlobalResponse>(`${this.categoryUrl}/categories/${categoryId}`)
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }

  update(request: Category): Observable<Category> {
    return this.http
      .put<Category>(`${this.categoryUrl}/categories/${request?.id}`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        take(1),
        catchError((err) => this.handleError(err))
      );
  }
}
