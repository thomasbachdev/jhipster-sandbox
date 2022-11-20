import dayjs from 'dayjs/esm';
import { IDocteur } from 'app/entities/docteur/docteur.model';

export interface IRappel {
  id: number;
  date?: dayjs.Dayjs | null;
  description?: string | null;
  deleted?: boolean | null;
  docteur?: Pick<IDocteur, 'id'> | null;
}

export type NewRappel = Omit<IRappel, 'id'> & { id: null };
