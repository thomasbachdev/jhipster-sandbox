import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IResident, NewResident } from '../resident.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IResident for edit and NewResidentFormGroupInput for create.
 */
type ResidentFormGroupInput = IResident | PartialWithRequiredKeyOf<NewResident>;

type ResidentFormDefaults = Pick<NewResident, 'id' | 'deleted'>;

type ResidentFormGroupContent = {
  id: FormControl<IResident['id'] | NewResident['id']>;
  numero: FormControl<IResident['numero']>;
  nom: FormControl<IResident['nom']>;
  prenom: FormControl<IResident['prenom']>;
  dateNaissance: FormControl<IResident['dateNaissance']>;
  sexe: FormControl<IResident['sexe']>;
  dateArrivee: FormControl<IResident['dateArrivee']>;
  chambre: FormControl<IResident['chambre']>;
  taille: FormControl<IResident['taille']>;
  denutrition: FormControl<IResident['denutrition']>;
  deleted: FormControl<IResident['deleted']>;
  etablissement: FormControl<IResident['etablissement']>;
};

export type ResidentFormGroup = FormGroup<ResidentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ResidentFormService {
  createResidentFormGroup(resident: ResidentFormGroupInput = { id: null }): ResidentFormGroup {
    const residentRawValue = {
      ...this.getFormDefaults(),
      ...resident,
    };
    return new FormGroup<ResidentFormGroupContent>({
      id: new FormControl(
        { value: residentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      numero: new FormControl(residentRawValue.numero, {
        validators: [Validators.required],
      }),
      nom: new FormControl(residentRawValue.nom, {
        validators: [Validators.required, Validators.maxLength(50), Validators.pattern('^[A-Z][a-z]+$')],
      }),
      prenom: new FormControl(residentRawValue.prenom, {
        validators: [Validators.required, Validators.maxLength(50), Validators.pattern('^[A-Z][a-z]+$')],
      }),
      dateNaissance: new FormControl(residentRawValue.dateNaissance, {
        validators: [Validators.required],
      }),
      sexe: new FormControl(residentRawValue.sexe, {
        validators: [Validators.required],
      }),
      dateArrivee: new FormControl(residentRawValue.dateArrivee, {
        validators: [Validators.required],
      }),
      chambre: new FormControl(residentRawValue.chambre, {
        validators: [Validators.required, Validators.maxLength(6)],
      }),
      taille: new FormControl(residentRawValue.taille, {
        validators: [Validators.required],
      }),
      denutrition: new FormControl(residentRawValue.denutrition),
      deleted: new FormControl(residentRawValue.deleted),
      etablissement: new FormControl(residentRawValue.etablissement),
    });
  }

  getResident(form: ResidentFormGroup): IResident | NewResident {
    return form.getRawValue() as IResident | NewResident;
  }

  resetForm(form: ResidentFormGroup, resident: ResidentFormGroupInput): void {
    const residentRawValue = { ...this.getFormDefaults(), ...resident };
    form.reset(
      {
        ...residentRawValue,
        id: { value: residentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ResidentFormDefaults {
    return {
      id: null,
      deleted: false,
    };
  }
}
