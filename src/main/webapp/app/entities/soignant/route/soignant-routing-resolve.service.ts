import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISoignant } from '../soignant.model';
import { SoignantService } from '../service/soignant.service';

@Injectable({ providedIn: 'root' })
export class SoignantRoutingResolveService implements Resolve<ISoignant | null> {
  constructor(protected service: SoignantService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISoignant | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((soignant: HttpResponse<ISoignant>) => {
          if (soignant.body) {
            return of(soignant.body);
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
