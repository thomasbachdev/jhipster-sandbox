import dayjs from 'dayjs/esm';

import { Sexe } from 'app/entities/enumerations/sexe.model';
import { StadeDenutrition } from 'app/entities/enumerations/stade-denutrition.model';

import { IResident, NewResident } from './resident.model';

export const sampleWithRequiredData: IResident = {
  id: 20367,
  numero: 16803,
  nom: 'Vwxh',
  prenom: 'Gujky',
  dateNaissance: dayjs('2022-11-20'),
  sexe: Sexe['MASCULIN'],
  dateArrivee: dayjs('2022-11-20'),
  chambre: 'hub',
  taille: 85943,
};

export const sampleWithPartialData: IResident = {
  id: 2719,
  numero: 46367,
  nom: 'Px',
  prenom: 'Jcj',
  dateNaissance: dayjs('2022-11-20'),
  sexe: Sexe['MASCULIN'],
  dateArrivee: dayjs('2022-11-19'),
  chambre: 'CSS',
  taille: 7007,
  deleted: false,
};

export const sampleWithFullData: IResident = {
  id: 79053,
  numero: 34130,
  nom: 'Fquj',
  prenom: 'Rjwf',
  dateNaissance: dayjs('2022-11-19'),
  sexe: Sexe['NON_BINAIRE'],
  dateArrivee: dayjs('2022-11-19'),
  chambre: 'b Boli',
  taille: 55150,
  denutrition: StadeDenutrition['MODEREE'],
  deleted: false,
};

export const sampleWithNewData: NewResident = {
  numero: 43786,
  nom: 'Dtfcm',
  prenom: 'Mebfz',
  dateNaissance: dayjs('2022-11-20'),
  sexe: Sexe['FEMININ'],
  dateArrivee: dayjs('2022-11-19'),
  chambre: 'Metal ',
  taille: 9573,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
