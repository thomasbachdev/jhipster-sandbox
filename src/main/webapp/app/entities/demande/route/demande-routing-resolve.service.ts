import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDemande } from '../demande.model';
import { DemandeService } from '../service/demande.service';

@Injectable({ providedIn: 'root' })
export class DemandeRoutingResolveService implements Resolve<IDemande | null> {
  constructor(protected service: DemandeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDemande | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((demande: HttpResponse<IDemande>) => {
          if (demande.body) {
            return of(demande.body);
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
