import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISoignant, NewSoignant } from '../soignant.model';

export type PartialUpdateSoignant = Partial<ISoignant> & Pick<ISoignant, 'id'>;

export type EntityResponseType = HttpResponse<ISoignant>;
export type EntityArrayResponseType = HttpResponse<ISoignant[]>;

@Injectable({ providedIn: 'root' })
export class SoignantService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/soignants');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(soignant: NewSoignant): Observable<EntityResponseType> {
    return this.http.post<ISoignant>(this.resourceUrl, soignant, { observe: 'response' });
  }

  update(soignant: ISoignant): Observable<EntityResponseType> {
    return this.http.put<ISoignant>(`${this.resourceUrl}/${this.getSoignantIdentifier(soignant)}`, soignant, { observe: 'response' });
  }

  partialUpdate(soignant: PartialUpdateSoignant): Observable<EntityResponseType> {
    return this.http.patch<ISoignant>(`${this.resourceUrl}/${this.getSoignantIdentifier(soignant)}`, soignant, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISoignant>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISoignant[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSoignantIdentifier(soignant: Pick<ISoignant, 'id'>): number {
    return soignant.id;
  }

  compareSoignant(o1: Pick<ISoignant, 'id'> | null, o2: Pick<ISoignant, 'id'> | null): boolean {
    return o1 && o2 ? this.getSoignantIdentifier(o1) === this.getSoignantIdentifier(o2) : o1 === o2;
  }

  addSoignantToCollectionIfMissing<Type extends Pick<ISoignant, 'id'>>(
    soignantCollection: Type[],
    ...soignantsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const soignants: Type[] = soignantsToCheck.filter(isPresent);
    if (soignants.length > 0) {
      const soignantCollectionIdentifiers = soignantCollection.map(soignantItem => this.getSoignantIdentifier(soignantItem)!);
      const soignantsToAdd = soignants.filter(soignantItem => {
        const soignantIdentifier = this.getSoignantIdentifier(soignantItem);
        if (soignantCollectionIdentifiers.includes(soignantIdentifier)) {
          return false;
        }
        soignantCollectionIdentifiers.push(soignantIdentifier);
        return true;
      });
      return [...soignantsToAdd, ...soignantCollection];
    }
    return soignantCollection;
  }
}
