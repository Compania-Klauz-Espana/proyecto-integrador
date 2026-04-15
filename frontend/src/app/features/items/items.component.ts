import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { ItemsService } from './items.service';
import { Item, ItemStatus, CreateItemRequest } from './models/item.model';

@Component({
  selector: 'app-items',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './items.component.html',
  styleUrl: './items.component.scss'
})
export class ItemsComponent implements OnInit, OnDestroy {
  items: Item[] = [];
  totalElements = 0;
  page = 0;
  size = 10;
  loading = false;
  error = '';
  success = '';

  showForm = false;
  editingId: number | null = null;
  form: CreateItemRequest = { name: '', description: '', status: 'ACTIVE' };
  statuses: ItemStatus[] = ['ACTIVE', 'INACTIVE', 'ARCHIVED'];

  private destroy$ = new Subject<void>();

  constructor(private svc: ItemsService) {}

  ngOnInit() { this.load(); }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  load() {
    this.loading = true;
    this.error = '';
    this.svc.findAll(this.page, this.size).pipe(takeUntil(this.destroy$)).subscribe({
      next: res => {
        this.items = res.content;
        this.totalElements = res.totalElements;
        this.loading = false;
      },
      error: err => {
        this.error = err.message;
        this.loading = false;
      }
    });
  }

  openCreate() {
    this.editingId = null;
    this.form = { name: '', description: '', status: 'ACTIVE' };
    this.showForm = true;
  }

  openEdit(item: Item) {
    this.editingId = item.id;
    this.form = { name: item.name, description: item.description, status: item.status };
    this.showForm = true;
  }

  save() {
    if (!this.form.name.trim()) return;
    this.loading = true;
    const obs = this.editingId
      ? this.svc.update(this.editingId, { ...this.form, status: this.form.status! })
      : this.svc.create(this.form);

    obs.pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.success = this.editingId ? 'Item actualizado' : 'Item creado';
        this.showForm = false;
        this.load();
        setTimeout(() => this.success = '', 3000);
      },
      error: err => { this.error = err.message; this.loading = false; }
    });
  }

  delete(id: number) {
    if (!confirm('¿Eliminar este item?')) return;
    this.loading = true;
    this.svc.delete(id).pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.success = 'Item eliminado';
        this.load();
        setTimeout(() => this.success = '', 3000);
      },
      error: err => { this.error = err.message; this.loading = false; }
    });
  }

  prevPage() { if (this.page > 0) { this.page--; this.load(); } }
  nextPage() { if ((this.page + 1) * this.size < this.totalElements) { this.page++; this.load(); } }
}
