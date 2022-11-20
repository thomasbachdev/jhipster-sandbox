import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IExamen, NewExamen } from '../examen.model';

export type PartialUpdateExamen = Partial<IExamen> & Pick<IExamen, 'id'>;

type RestOf<T extends IExamen | NewExamen> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestExamen = RestOf<IExamen>;

export type NewRestExamen = RestOf<NewExamen>;

export type PartialUpdateRestExamen = RestOf<PartialUpdateExamen>;

export type EntityResponseType = HttpResponse<IExamen>;
export type EntityArrayResponseType = HttpResponse<IExamen[]>;

@Injectable({ providedIn: 'root' })
export class ExamenService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/examen');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(examen: NewExamen): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(examen);
    return this.http
      .post<RestExamen>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(examen: IExamen): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(examen);
    return this.http
      .put<RestExamen>(`${this.resourceUrl}/${this.getExamenIdentifier(examen)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(examen: PartialUpdateExamen): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(examen);
    return this.http
      .patch<RestExamen>(`${this.resourceUrl}/${this.getExamenIdentifier(examen)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestExamen>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestExamen[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getExamenIdentifier(examen: Pick<IExamen, 'id'>): number {
    return examen.id;
  }

  compareExamen(o1: Pick<IExamen, 'id'> | null, o2: Pick<IExamen, 'id'> | null): boolean {
    return o1 && o2 ? this.getExamenIdentifier(o1) === this.getExamenIdentifier(o2) : o1 === o2;
  }

  addExamenToCollectionIfMissing<Type extends Pick<IExamen, 'id'>>(
    examenCollection: Type[],
    ...examenToCheck: (Type | null | undefined)[]
  ): Type[] {
    const examen: Type[] = examenToCheck.filter(isPresent);
    if (examen.length > 0) {
      const examenCollectionIdentifiers = examenCollection.map(examenItem => this.getExamenIdentifier(examenItem)!);
      const examenToAdd = examen.filter(examenItem => {
        const examenIdentifier = this.getExamenIdentifier(examenItem);
        if (examenCollectionIdentifiers.includes(examenIdentifier)) {
          return false;
        }
        examenCollectionIdentifiers.push(examenIdentifier);
        return true;
      });
      return [...examenToAdd, ...examenCollection];
    }
    return examenCollection;
  }

  protected convertDateFromClient<T extends IExamen | NewExamen | PartialUpdateExamen>(examen: T): RestOf<T> {
    return {
      ...examen,
      date: examen.date?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restExamen: RestExamen): IExamen {
    return {
      ...restExamen,
      date: restExamen.date ? dayjs(restExamen.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestExamen>): HttpResponse<IExamen> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestExamen[]>): HttpResponse<IExamen[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
