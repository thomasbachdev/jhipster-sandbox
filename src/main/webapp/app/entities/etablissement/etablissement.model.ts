export interface IEtablissement {
  id: number;
  nom?: string | null;
  deleted?: boolean | null;
}

export type NewEtablissement = Omit<IEtablissement, 'id'> & { id: null };
