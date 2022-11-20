import { IUser } from 'app/entities/user/user.model';

export interface IDocteur {
  id: number;
  deleted?: boolean | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewDocteur = Omit<IDocteur, 'id'> & { id: null };
