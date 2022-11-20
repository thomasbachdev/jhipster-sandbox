import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRappel, NewRappel } from '../rappel.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRappel for edit and NewRappelFormGroupInput for create.
 */
type RappelFormGroupInput = IRappel | PartialWithRequiredKeyOf<NewRappel>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRappel | NewRappel> = Omit<T, 'date'> & {
  date?: string | null;
};

type RappelFormRawValue = FormValueOf<IRappel>;

type NewRappelFormRawValue = FormValueOf<NewRappel>;

type RappelFormDefaults = Pick<NewRappel, 'id' | 'date' | 'deleted'>;

type RappelFormGroupContent = {
  id: FormControl<RappelFormRawValue['id'] | NewRappel['id']>;
  date: FormControl<RappelFormRawValue['date']>;
  description: FormControl<RappelFormRawValue['description']>;
  deleted: FormControl<RappelFormRawValue['deleted']>;
  docteur: FormControl<RappelFormRawValue['docteur']>;
};

export type RappelFormGroup = FormGroup<RappelFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RappelFormService {
  createRappelFormGroup(rappel: RappelFormGroupInput = { id: null }): RappelFormGroup {
    const rappelRawValue = this.convertRappelToRappelRawValue({
      ...this.getFormDefaults(),
      ...rappel,
    });
    return new FormGroup<RappelFormGroupContent>({
      id: new FormControl(
        { value: rappelRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      date: new FormControl(rappelRawValue.date, {
        validators: [Validators.required],
      }),
      description: new FormControl(rappelRawValue.description, {
        validators: [Validators.required, Validators.maxLength(2000)],
      }),
      deleted: new FormControl(rappelRawValue.deleted),
      docteur: new FormControl(rappelRawValue.docteur),
    });
  }

  getRappel(form: RappelFormGroup): IRappel | NewRappel {
    return this.convertRappelRawValueToRappel(form.getRawValue() as RappelFormRawValue | NewRappelFormRawValue);
  }

  resetForm(form: RappelFormGroup, rappel: RappelFormGroupInput): void {
    const rappelRawValue = this.convertRappelToRappelRawValue({ ...this.getFormDefaults(), ...rappel });
    form.reset(
      {
        ...rappelRawValue,
        id: { value: rappelRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RappelFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      deleted: false,
    };
  }

  private convertRappelRawValueToRappel(rawRappel: RappelFormRawValue | NewRappelFormRawValue): IRappel | NewRappel {
    return {
      ...rawRappel,
      date: dayjs(rawRappel.date, DATE_TIME_FORMAT),
    };
  }

  private convertRappelToRappelRawValue(
    rappel: IRappel | (Partial<NewRappel> & RappelFormDefaults)
  ): RappelFormRawValue | PartialWithRequiredKeyOf<NewRappelFormRawValue> {
    return {
      ...rappel,
      date: rappel.date ? rappel.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
