import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, finalize, throwError } from 'rxjs';
import { LoadingService } from '../services/loading.service';

export const httpInterceptor: HttpInterceptorFn = (req, next) => {
  const loading = inject(LoadingService);
  loading.show();

  const token = localStorage.getItem('token');
  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      const msg =
        err.status === 401 ? 'No autorizado' :
        err.status === 403 ? 'Sin permisos' :
        err.status === 404 ? 'Recurso no encontrado' :
        err.status >= 500  ? 'Error del servidor' :
        'Error inesperado';
      console.error(`HTTP ${err.status}: ${msg}`);
      return throwError(() => ({ status: err.status, message: msg }));
    }),
    finalize(() => loading.hide())
  );
};
