import { IEtablissement, NewEtablissement } from './etablissement.model';

export const sampleWithRequiredData: IEtablissement = {
  id: 16156,
  nom: 'Mmpaa',
};

export const sampleWithPartialData: IEtablissement = {
  id: 27534,
  nom: 'Jel',
  deleted: true,
};

export const sampleWithFullData: IEtablissement = {
  id: 19437,
  nom: 'Fe',
  deleted: false,
};

export const sampleWithNewData: NewEtablissement = {
  nom: 'Pmwfrqj',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
