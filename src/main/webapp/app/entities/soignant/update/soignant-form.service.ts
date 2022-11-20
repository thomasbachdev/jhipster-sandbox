import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISoignant, NewSoignant } from '../soignant.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISoignant for edit and NewSoignantFormGroupInput for create.
 */
type SoignantFormGroupInput = ISoignant | PartialWithRequiredKeyOf<NewSoignant>;

type SoignantFormDefaults = Pick<NewSoignant, 'id' | 'deleted'>;

type SoignantFormGroupContent = {
  id: FormControl<ISoignant['id'] | NewSoignant['id']>;
  deleted: FormControl<ISoignant['deleted']>;
  user: FormControl<ISoignant['user']>;
  etablissement: FormControl<ISoignant['etablissement']>;
};

export type SoignantFormGroup = FormGroup<SoignantFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SoignantFormService {
  createSoignantFormGroup(soignant: SoignantFormGroupInput = { id: null }): SoignantFormGroup {
    const soignantRawValue = {
      ...this.getFormDefaults(),
      ...soignant,
    };
    return new FormGroup<SoignantFormGroupContent>({
      id: new FormControl(
        { value: soignantRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      deleted: new FormControl(soignantRawValue.deleted),
      user: new FormControl(soignantRawValue.user),
      etablissement: new FormControl(soignantRawValue.etablissement),
    });
  }

  getSoignant(form: SoignantFormGroup): ISoignant | NewSoignant {
    return form.getRawValue() as ISoignant | NewSoignant;
  }

  resetForm(form: SoignantFormGroup, soignant: SoignantFormGroupInput): void {
    const soignantRawValue = { ...this.getFormDefaults(), ...soignant };
    form.reset(
      {
        ...soignantRawValue,
        id: { value: soignantRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SoignantFormDefaults {
    return {
      id: null,
      deleted: false,
    };
  }
}
