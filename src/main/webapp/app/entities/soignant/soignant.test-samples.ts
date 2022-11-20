import { ISoignant, NewSoignant } from './soignant.model';

export const sampleWithRequiredData: ISoignant = {
  id: 37575,
};

export const sampleWithPartialData: ISoignant = {
  id: 51049,
  deleted: true,
};

export const sampleWithFullData: ISoignant = {
  id: 35758,
  deleted: true,
};

export const sampleWithNewData: NewSoignant = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
