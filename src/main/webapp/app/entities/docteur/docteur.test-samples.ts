import { IDocteur, NewDocteur } from './docteur.model';

export const sampleWithRequiredData: IDocteur = {
  id: 22339,
};

export const sampleWithPartialData: IDocteur = {
  id: 50693,
  deleted: true,
};

export const sampleWithFullData: IDocteur = {
  id: 9123,
  deleted: false,
};

export const sampleWithNewData: NewDocteur = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
