import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEtablissement } from '../etablissement.model';
import { EtablissementService } from '../service/etablissement.service';

@Injectable({ providedIn: 'root' })
export class EtablissementRoutingResolveService implements Resolve<IEtablissement | null> {
  constructor(protected service: EtablissementService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEtablissement | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((etablissement: HttpResponse<IEtablissement>) => {
          if (etablissement.body) {
            return of(etablissement.body);
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
