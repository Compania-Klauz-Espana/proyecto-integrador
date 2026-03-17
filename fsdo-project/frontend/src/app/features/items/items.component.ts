import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ItemsService } from './items.service';
import { Item, ItemStatus, CreateItemRequest } from './models/item.model';

@Component({
  selector: 'app-items',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './items.component.html',
  styleUrl: './items.component.scss'
})
export class ItemsComponent implements OnInit {
  items: Item[] = [];
  totalElements = 0;
  page = 0;
  size = 10;
  loading = false;
  error = '';
  success = '';

  showForm = false;
  editingId: number | null = null;
  form: CreateItemRequest = { name: '', description: '', status: 'PENDING' };
  statuses: ItemStatus[] = ['ACTIVE', 'INACTIVE', 'PENDING'];

  constructor(private svc: ItemsService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.error = '';
    this.svc.findAll(this.page, this.size).subscribe({
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
    this.form = { name: '', description: '', status: 'PENDING' };
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

    obs.subscribe({
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
    this.svc.delete(id).subscribe({
      next: () => { this.success = 'Item eliminado'; this.load(); setTimeout(() => this.success = '', 3000); },
      error: err => this.error = err.message
    });
  }

  prevPage() { if (this.page > 0) { this.page--; this.load(); } }
  nextPage() { if ((this.page + 1) * this.size < this.totalElements) { this.page++; this.load(); } }
}
