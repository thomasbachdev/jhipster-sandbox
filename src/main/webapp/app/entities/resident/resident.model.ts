import dayjs from 'dayjs/esm';
import { IEtablissement } from 'app/entities/etablissement/etablissement.model';
import { Sexe } from 'app/entities/enumerations/sexe.model';
import { StadeDenutrition } from 'app/entities/enumerations/stade-denutrition.model';

export interface IResident {
  id: number;
  numero?: number | null;
  nom?: string | null;
  prenom?: string | null;
  dateNaissance?: dayjs.Dayjs | null;
  sexe?: Sexe | null;
  dateArrivee?: dayjs.Dayjs | null;
  chambre?: string | null;
  taille?: number | null;
  denutrition?: StadeDenutrition | null;
  deleted?: boolean | null;
  etablissement?: Pick<IEtablissement, 'id'> | null;
}

export type NewResident = Omit<IResident, 'id'> & { id: null };
