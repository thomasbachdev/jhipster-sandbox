import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IResident, NewResident } from '../resident.model';

export type PartialUpdateResident = Partial<IResident> & Pick<IResident, 'id'>;

type RestOf<T extends IResident | NewResident> = Omit<T, 'dateNaissance' | 'dateArrivee'> & {
  dateNaissance?: string | null;
  dateArrivee?: string | null;
};

export type RestResident = RestOf<IResident>;

export type NewRestResident = RestOf<NewResident>;

export type PartialUpdateRestResident = RestOf<PartialUpdateResident>;

export type EntityResponseType = HttpResponse<IResident>;
export type EntityArrayResponseType = HttpResponse<IResident[]>;

@Injectable({ providedIn: 'root' })
export class ResidentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/residents');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(resident: NewResident): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resident);
    return this.http
      .post<RestResident>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(resident: IResident): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resident);
    return this.http
      .put<RestResident>(`${this.resourceUrl}/${this.getResidentIdentifier(resident)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(resident: PartialUpdateResident): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(resident);
    return this.http
      .patch<RestResident>(`${this.resourceUrl}/${this.getResidentIdentifier(resident)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestResident>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestResident[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getResidentIdentifier(resident: Pick<IResident, 'id'>): number {
    return resident.id;
  }

  compareResident(o1: Pick<IResident, 'id'> | null, o2: Pick<IResident, 'id'> | null): boolean {
    return o1 && o2 ? this.getResidentIdentifier(o1) === this.getResidentIdentifier(o2) : o1 === o2;
  }

  addResidentToCollectionIfMissing<Type extends Pick<IResident, 'id'>>(
    residentCollection: Type[],
    ...residentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const residents: Type[] = residentsToCheck.filter(isPresent);
    if (residents.length > 0) {
      const residentCollectionIdentifiers = residentCollection.map(residentItem => this.getResidentIdentifier(residentItem)!);
      const residentsToAdd = residents.filter(residentItem => {
        const residentIdentifier = this.getResidentIdentifier(residentItem);
        if (residentCollectionIdentifiers.includes(residentIdentifier)) {
          return false;
        }
        residentCollectionIdentifiers.push(residentIdentifier);
        return true;
      });
      return [...residentsToAdd, ...residentCollection];
    }
    return residentCollection;
  }

  protected convertDateFromClient<T extends IResident | NewResident | PartialUpdateResident>(resident: T): RestOf<T> {
    return {
      ...resident,
      dateNaissance: resident.dateNaissance?.format(DATE_FORMAT) ?? null,
      dateArrivee: resident.dateArrivee?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restResident: RestResident): IResident {
    return {
      ...restResident,
      dateNaissance: restResident.dateNaissance ? dayjs(restResident.dateNaissance) : undefined,
      dateArrivee: restResident.dateArrivee ? dayjs(restResident.dateArrivee) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestResident>): HttpResponse<IResident> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestResident[]>): HttpResponse<IResident[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
