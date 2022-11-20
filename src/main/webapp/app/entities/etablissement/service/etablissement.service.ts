import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEtablissement, NewEtablissement } from '../etablissement.model';

export type PartialUpdateEtablissement = Partial<IEtablissement> & Pick<IEtablissement, 'id'>;

export type EntityResponseType = HttpResponse<IEtablissement>;
export type EntityArrayResponseType = HttpResponse<IEtablissement[]>;

@Injectable({ providedIn: 'root' })
export class EtablissementService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/etablissements');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(etablissement: NewEtablissement): Observable<EntityResponseType> {
    return this.http.post<IEtablissement>(this.resourceUrl, etablissement, { observe: 'response' });
  }

  update(etablissement: IEtablissement): Observable<EntityResponseType> {
    return this.http.put<IEtablissement>(`${this.resourceUrl}/${this.getEtablissementIdentifier(etablissement)}`, etablissement, {
      observe: 'response',
    });
  }

  partialUpdate(etablissement: PartialUpdateEtablissement): Observable<EntityResponseType> {
    return this.http.patch<IEtablissement>(`${this.resourceUrl}/${this.getEtablissementIdentifier(etablissement)}`, etablissement, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEtablissement>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEtablissement[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEtablissementIdentifier(etablissement: Pick<IEtablissement, 'id'>): number {
    return etablissement.id;
  }

  compareEtablissement(o1: Pick<IEtablissement, 'id'> | null, o2: Pick<IEtablissement, 'id'> | null): boolean {
    return o1 && o2 ? this.getEtablissementIdentifier(o1) === this.getEtablissementIdentifier(o2) : o1 === o2;
  }

  addEtablissementToCollectionIfMissing<Type extends Pick<IEtablissement, 'id'>>(
    etablissementCollection: Type[],
    ...etablissementsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const etablissements: Type[] = etablissementsToCheck.filter(isPresent);
    if (etablissements.length > 0) {
      const etablissementCollectionIdentifiers = etablissementCollection.map(
        etablissementItem => this.getEtablissementIdentifier(etablissementItem)!
      );
      const etablissementsToAdd = etablissements.filter(etablissementItem => {
        const etablissementIdentifier = this.getEtablissementIdentifier(etablissementItem);
        if (etablissementCollectionIdentifiers.includes(etablissementIdentifier)) {
          return false;
        }
        etablissementCollectionIdentifiers.push(etablissementIdentifier);
        return true;
      });
      return [...etablissementsToAdd, ...etablissementCollection];
    }
    return etablissementCollection;
  }
}
