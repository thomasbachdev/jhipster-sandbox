import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IExamen } from '../examen.model';
import { ExamenService } from '../service/examen.service';

@Injectable({ providedIn: 'root' })
export class ExamenRoutingResolveService implements Resolve<IExamen | null> {
  constructor(protected service: ExamenService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IExamen | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((examen: HttpResponse<IExamen>) => {
          if (examen.body) {
            return of(examen.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
