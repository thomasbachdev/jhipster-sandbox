import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDemande, NewDemande } from '../demande.model';

export type PartialUpdateDemande = Partial<IDemande> & Pick<IDemande, 'id'>;

type RestOf<T extends IDemande | NewDemande> = Omit<T, 'dateEmition' | 'dateLimite'> & {
  dateEmition?: string | null;
  dateLimite?: string | null;
};

export type RestDemande = RestOf<IDemande>;

export type NewRestDemande = RestOf<NewDemande>;

export type PartialUpdateRestDemande = RestOf<PartialUpdateDemande>;

export type EntityResponseType = HttpResponse<IDemande>;
export type EntityArrayResponseType = HttpResponse<IDemande[]>;

@Injectable({ providedIn: 'root' })
export class DemandeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/demandes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(demande: NewDemande): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(demande);
    return this.http
      .post<RestDemande>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(demande: IDemande): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(demande);
    return this.http
      .put<RestDemande>(`${this.resourceUrl}/${this.getDemandeIdentifier(demande)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(demande: PartialUpdateDemande): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(demande);
    return this.http
      .patch<RestDemande>(`${this.resourceUrl}/${this.getDemandeIdentifier(demande)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDemande>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDemande[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDemandeIdentifier(demande: Pick<IDemande, 'id'>): number {
    return demande.id;
  }

  compareDemande(o1: Pick<IDemande, 'id'> | null, o2: Pick<IDemande, 'id'> | null): boolean {
    return o1 && o2 ? this.getDemandeIdentifier(o1) === this.getDemandeIdentifier(o2) : o1 === o2;
  }

  addDemandeToCollectionIfMissing<Type extends Pick<IDemande, 'id'>>(
    demandeCollection: Type[],
    ...demandesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const demandes: Type[] = demandesToCheck.filter(isPresent);
    if (demandes.length > 0) {
      const demandeCollectionIdentifiers = demandeCollection.map(demandeItem => this.getDemandeIdentifier(demandeItem)!);
      const demandesToAdd = demandes.filter(demandeItem => {
        const demandeIdentifier = this.getDemandeIdentifier(demandeItem);
        if (demandeCollectionIdentifiers.includes(demandeIdentifier)) {
          return false;
        }
        demandeCollectionIdentifiers.push(demandeIdentifier);
        return true;
      });
      return [...demandesToAdd, ...demandeCollection];
    }
    return demandeCollection;
  }

  protected convertDateFromClient<T extends IDemande | NewDemande | PartialUpdateDemande>(demande: T): RestOf<T> {
    return {
      ...demande,
      dateEmition: demande.dateEmition?.toJSON() ?? null,
      dateLimite: demande.dateLimite?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restDemande: RestDemande): IDemande {
    return {
      ...restDemande,
      dateEmition: restDemande.dateEmition ? dayjs(restDemande.dateEmition) : undefined,
      dateLimite: restDemande.dateLimite ? dayjs(restDemande.dateLimite) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDemande>): HttpResponse<IDemande> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDemande[]>): HttpResponse<IDemande[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
