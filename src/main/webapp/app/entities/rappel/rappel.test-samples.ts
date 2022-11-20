import dayjs from 'dayjs/esm';

import { IRappel, NewRappel } from './rappel.model';

export const sampleWithRequiredData: IRappel = {
  id: 58534,
  date: dayjs('2022-11-20T04:12'),
  description: 'next quantify black',
};

export const sampleWithPartialData: IRappel = {
  id: 13255,
  date: dayjs('2022-11-20T14:06'),
  description: 'engage',
  deleted: true,
};

export const sampleWithFullData: IRappel = {
  id: 93551,
  date: dayjs('2022-11-20T10:17'),
  description: 'JSON b',
  deleted: false,
};

export const sampleWithNewData: NewRappel = {
  date: dayjs('2022-11-20T09:13'),
  description: 'optical',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
