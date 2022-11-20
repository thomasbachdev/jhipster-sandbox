import { IUser } from 'app/entities/user/user.model';
import { IEtablissement } from 'app/entities/etablissement/etablissement.model';

export interface ISoignant {
  id: number;
  deleted?: boolean | null;
  user?: Pick<IUser, 'id'> | null;
  etablissement?: Pick<IEtablissement, 'id'> | null;
}

export type NewSoignant = Omit<ISoignant, 'id'> & { id: null };
