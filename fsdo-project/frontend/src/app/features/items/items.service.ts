import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  Item, PageResponse,
  CreateItemRequest, UpdateItemRequest
} from './models/item.model';

@Injectable({ providedIn: 'root' })
export class ItemsService {
  private api = `${environment.apiUrl}/items`;

  constructor(private http: HttpClient) {}

  findAll(page = 0, size = 10): Observable<PageResponse<Item>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get<PageResponse<Item>>(this.api, { params });
  }

  findById(id: number): Observable<Item> {
    return this.http.get<Item>(`${this.api}/${id}`);
  }

  create(req: CreateItemRequest): Observable<Item> {
    return this.http.post<Item>(this.api, req);
  }

  update(id: number, req: UpdateItemRequest): Observable<Item> {
    return this.http.put<Item>(`${this.api}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
