import dayjs from 'dayjs/esm';
import { IResident } from 'app/entities/resident/resident.model';
import { IDocteur } from 'app/entities/docteur/docteur.model';

export interface IDemande {
  id: number;
  dateEmition?: dayjs.Dayjs | null;
  dateLimite?: dayjs.Dayjs | null;
  description?: string | null;
  deleted?: boolean | null;
  resident?: Pick<IResident, 'id'> | null;
  docteur?: Pick<IDocteur, 'id'> | null;
}

export type NewDemande = Omit<IDemande, 'id'> & { id: null };
