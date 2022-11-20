import dayjs from 'dayjs/esm';

import { IExamen, NewExamen } from './examen.model';

export const sampleWithRequiredData: IExamen = {
  id: 47763,
  date: dayjs('2022-11-20T14:49'),
};

export const sampleWithPartialData: IExamen = {
  id: 15687,
  date: dayjs('2022-11-19T19:31'),
  albumine: 37755,
  commentaire: 'b Fidji embrace',
};

export const sampleWithFullData: IExamen = {
  id: 12542,
  date: dayjs('2022-11-19T15:51'),
  poids: 48530,
  albumine: 65597,
  imc: 1992,
  epa: 7,
  commentaire: 'a',
  deleted: true,
};

export const sampleWithNewData: NewExamen = {
  date: dayjs('2022-11-20T13:33'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
