import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IDocteur, NewDocteur } from '../docteur.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDocteur for edit and NewDocteurFormGroupInput for create.
 */
type DocteurFormGroupInput = IDocteur | PartialWithRequiredKeyOf<NewDocteur>;

type DocteurFormDefaults = Pick<NewDocteur, 'id' | 'deleted'>;

type DocteurFormGroupContent = {
  id: FormControl<IDocteur['id'] | NewDocteur['id']>;
  deleted: FormControl<IDocteur['deleted']>;
  user: FormControl<IDocteur['user']>;
};

export type DocteurFormGroup = FormGroup<DocteurFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DocteurFormService {
  createDocteurFormGroup(docteur: DocteurFormGroupInput = { id: null }): DocteurFormGroup {
    const docteurRawValue = {
      ...this.getFormDefaults(),
      ...docteur,
    };
    return new FormGroup<DocteurFormGroupContent>({
      id: new FormControl(
        { value: docteurRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      deleted: new FormControl(docteurRawValue.deleted),
      user: new FormControl(docteurRawValue.user),
    });
  }

  getDocteur(form: DocteurFormGroup): IDocteur | NewDocteur {
    return form.getRawValue() as IDocteur | NewDocteur;
  }

  resetForm(form: DocteurFormGroup, docteur: DocteurFormGroupInput): void {
    const docteurRawValue = { ...this.getFormDefaults(), ...docteur };
    form.reset(
      {
        ...docteurRawValue,
        id: { value: docteurRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DocteurFormDefaults {
    return {
      id: null,
      deleted: false,
    };
  }
}
