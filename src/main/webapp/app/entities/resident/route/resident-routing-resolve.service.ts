import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IResident } from '../resident.model';
import { ResidentService } from '../service/resident.service';

@Injectable({ providedIn: 'root' })
export class ResidentRoutingResolveService implements Resolve<IResident | null> {
  constructor(protected service: ResidentService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IResident | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((resident: HttpResponse<IResident>) => {
          if (resident.body) {
            return of(resident.body);
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
