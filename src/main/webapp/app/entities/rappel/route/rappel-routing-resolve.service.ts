import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRappel } from '../rappel.model';
import { RappelService } from '../service/rappel.service';

@Injectable({ providedIn: 'root' })
export class RappelRoutingResolveService implements Resolve<IRappel | null> {
  constructor(protected service: RappelService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IRappel | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((rappel: HttpResponse<IRappel>) => {
          if (rappel.body) {
            return of(rappel.body);
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
