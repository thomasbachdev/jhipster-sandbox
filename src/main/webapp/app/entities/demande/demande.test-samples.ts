import dayjs from 'dayjs/esm';

import { IDemande, NewDemande } from './demande.model';

export const sampleWithRequiredData: IDemande = {
  id: 19730,
  dateEmition: dayjs('2022-11-19T19:47'),
  dateLimite: dayjs('2022-11-19'),
  description: 'Hat',
};

export const sampleWithPartialData: IDemande = {
  id: 42571,
  dateEmition: dayjs('2022-11-19T17:30'),
  dateLimite: dayjs('2022-11-20'),
  description: 'Aquitaine',
};

export const sampleWithFullData: IDemande = {
  id: 43417,
  dateEmition: dayjs('2022-11-19T23:13'),
  dateLimite: dayjs('2022-11-19'),
  description: 'vertical Market bus',
  deleted: false,
};

export const sampleWithNewData: NewDemande = {
  dateEmition: dayjs('2022-11-20T05:07'),
  dateLimite: dayjs('2022-11-19'),
  description: 'EXE saoudite',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
