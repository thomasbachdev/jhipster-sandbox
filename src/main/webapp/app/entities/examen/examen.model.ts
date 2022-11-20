import dayjs from 'dayjs/esm';
import { IResident } from 'app/entities/resident/resident.model';
import { ISoignant } from 'app/entities/soignant/soignant.model';

export interface IExamen {
  id: number;
  date?: dayjs.Dayjs | null;
  poids?: number | null;
  albumine?: number | null;
  imc?: number | null;
  epa?: number | null;
  commentaire?: string | null;
  deleted?: boolean | null;
  resident?: Pick<IResident, 'id'> | null;
  soignant?: Pick<ISoignant, 'id'> | null;
}

export type NewExamen = Omit<IExamen, 'id'> & { id: null };
