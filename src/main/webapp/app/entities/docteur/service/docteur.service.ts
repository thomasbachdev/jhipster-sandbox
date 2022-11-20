import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDocteur, NewDocteur } from '../docteur.model';

export type PartialUpdateDocteur = Partial<IDocteur> & Pick<IDocteur, 'id'>;

export type EntityResponseType = HttpResponse<IDocteur>;
export type EntityArrayResponseType = HttpResponse<IDocteur[]>;

@Injectable({ providedIn: 'root' })
export class DocteurService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/docteurs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(docteur: NewDocteur): Observable<EntityResponseType> {
    return this.http.post<IDocteur>(this.resourceUrl, docteur, { observe: 'response' });
  }

  update(docteur: IDocteur): Observable<EntityResponseType> {
    return this.http.put<IDocteur>(`${this.resourceUrl}/${this.getDocteurIdentifier(docteur)}`, docteur, { observe: 'response' });
  }

  partialUpdate(docteur: PartialUpdateDocteur): Observable<EntityResponseType> {
    return this.http.patch<IDocteur>(`${this.resourceUrl}/${this.getDocteurIdentifier(docteur)}`, docteur, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDocteur>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDocteur[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDocteurIdentifier(docteur: Pick<IDocteur, 'id'>): number {
    return docteur.id;
  }

  compareDocteur(o1: Pick<IDocteur, 'id'> | null, o2: Pick<IDocteur, 'id'> | null): boolean {
    return o1 && o2 ? this.getDocteurIdentifier(o1) === this.getDocteurIdentifier(o2) : o1 === o2;
  }

  addDocteurToCollectionIfMissing<Type extends Pick<IDocteur, 'id'>>(
    docteurCollection: Type[],
    ...docteursToCheck: (Type | null | undefined)[]
  ): Type[] {
    const docteurs: Type[] = docteursToCheck.filter(isPresent);
    if (docteurs.length > 0) {
      const docteurCollectionIdentifiers = docteurCollection.map(docteurItem => this.getDocteurIdentifier(docteurItem)!);
      const docteursToAdd = docteurs.filter(docteurItem => {
        const docteurIdentifier = this.getDocteurIdentifier(docteurItem);
        if (docteurCollectionIdentifiers.includes(docteurIdentifier)) {
          return false;
        }
        docteurCollectionIdentifiers.push(docteurIdentifier);
        return true;
      });
      return [...docteursToAdd, ...docteurCollection];
    }
    return docteurCollection;
  }
}
