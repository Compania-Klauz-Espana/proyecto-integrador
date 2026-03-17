export type ItemStatus = 'ACTIVE' | 'INACTIVE' | 'PENDING';

export interface Item {
  id: number;
  name: string;
  description?: string;
  status: ItemStatus;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface CreateItemRequest {
  name: string;
  description?: string;
  status?: ItemStatus;
}

export interface UpdateItemRequest {
  name: string;
  description?: string;
  status: ItemStatus;
}
