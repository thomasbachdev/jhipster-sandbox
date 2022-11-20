import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRappel, NewRappel } from '../rappel.model';

export type PartialUpdateRappel = Partial<IRappel> & Pick<IRappel, 'id'>;

type RestOf<T extends IRappel | NewRappel> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestRappel = RestOf<IRappel>;

export type NewRestRappel = RestOf<NewRappel>;

export type PartialUpdateRestRappel = RestOf<PartialUpdateRappel>;

export type EntityResponseType = HttpResponse<IRappel>;
export type EntityArrayResponseType = HttpResponse<IRappel[]>;

@Injectable({ providedIn: 'root' })
export class RappelService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/rappels');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(rappel: NewRappel): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(rappel);
    return this.http
      .post<RestRappel>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(rappel: IRappel): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(rappel);
    return this.http
      .put<RestRappel>(`${this.resourceUrl}/${this.getRappelIdentifier(rappel)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(rappel: PartialUpdateRappel): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(rappel);
    return this.http
      .patch<RestRappel>(`${this.resourceUrl}/${this.getRappelIdentifier(rappel)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRappel>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRappel[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRappelIdentifier(rappel: Pick<IRappel, 'id'>): number {
    return rappel.id;
  }

  compareRappel(o1: Pick<IRappel, 'id'> | null, o2: Pick<IRappel, 'id'> | null): boolean {
    return o1 && o2 ? this.getRappelIdentifier(o1) === this.getRappelIdentifier(o2) : o1 === o2;
  }

  addRappelToCollectionIfMissing<Type extends Pick<IRappel, 'id'>>(
    rappelCollection: Type[],
    ...rappelsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const rappels: Type[] = rappelsToCheck.filter(isPresent);
    if (rappels.length > 0) {
      const rappelCollectionIdentifiers = rappelCollection.map(rappelItem => this.getRappelIdentifier(rappelItem)!);
      const rappelsToAdd = rappels.filter(rappelItem => {
        const rappelIdentifier = this.getRappelIdentifier(rappelItem);
        if (rappelCollectionIdentifiers.includes(rappelIdentifier)) {
          return false;
        }
        rappelCollectionIdentifiers.push(rappelIdentifier);
        return true;
      });
      return [...rappelsToAdd, ...rappelCollection];
    }
    return rappelCollection;
  }

  protected convertDateFromClient<T extends IRappel | NewRappel | PartialUpdateRappel>(rappel: T): RestOf<T> {
    return {
      ...rappel,
      date: rappel.date?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restRappel: RestRappel): IRappel {
    return {
      ...restRappel,
      date: restRappel.date ? dayjs(restRappel.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestRappel>): HttpResponse<IRappel> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRappel[]>): HttpResponse<IRappel[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
