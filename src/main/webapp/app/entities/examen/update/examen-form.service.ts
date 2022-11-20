import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IExamen, NewExamen } from '../examen.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IExamen for edit and NewExamenFormGroupInput for create.
 */
type ExamenFormGroupInput = IExamen | PartialWithRequiredKeyOf<NewExamen>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IExamen | NewExamen> = Omit<T, 'date'> & {
  date?: string | null;
};

type ExamenFormRawValue = FormValueOf<IExamen>;

type NewExamenFormRawValue = FormValueOf<NewExamen>;

type ExamenFormDefaults = Pick<NewExamen, 'id' | 'date' | 'deleted'>;

type ExamenFormGroupContent = {
  id: FormControl<ExamenFormRawValue['id'] | NewExamen['id']>;
  date: FormControl<ExamenFormRawValue['date']>;
  poids: FormControl<ExamenFormRawValue['poids']>;
  albumine: FormControl<ExamenFormRawValue['albumine']>;
  imc: FormControl<ExamenFormRawValue['imc']>;
  epa: FormControl<ExamenFormRawValue['epa']>;
  commentaire: FormControl<ExamenFormRawValue['commentaire']>;
  deleted: FormControl<ExamenFormRawValue['deleted']>;
  resident: FormControl<ExamenFormRawValue['resident']>;
  soignant: FormControl<ExamenFormRawValue['soignant']>;
};

export type ExamenFormGroup = FormGroup<ExamenFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ExamenFormService {
  createExamenFormGroup(examen: ExamenFormGroupInput = { id: null }): ExamenFormGroup {
    const examenRawValue = this.convertExamenToExamenRawValue({
      ...this.getFormDefaults(),
      ...examen,
    });
    return new FormGroup<ExamenFormGroupContent>({
      id: new FormControl(
        { value: examenRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      date: new FormControl(examenRawValue.date, {
        validators: [Validators.required],
      }),
      poids: new FormControl(examenRawValue.poids, {
        validators: [Validators.min(0)],
      }),
      albumine: new FormControl(examenRawValue.albumine, {
        validators: [Validators.min(0)],
      }),
      imc: new FormControl(examenRawValue.imc),
      epa: new FormControl(examenRawValue.epa, {
        validators: [Validators.min(0), Validators.max(10)],
      }),
      commentaire: new FormControl(examenRawValue.commentaire, {
        validators: [Validators.maxLength(2000)],
      }),
      deleted: new FormControl(examenRawValue.deleted),
      resident: new FormControl(examenRawValue.resident),
      soignant: new FormControl(examenRawValue.soignant),
    });
  }

  getExamen(form: ExamenFormGroup): IExamen | NewExamen {
    return this.convertExamenRawValueToExamen(form.getRawValue() as ExamenFormRawValue | NewExamenFormRawValue);
  }

  resetForm(form: ExamenFormGroup, examen: ExamenFormGroupInput): void {
    const examenRawValue = this.convertExamenToExamenRawValue({ ...this.getFormDefaults(), ...examen });
    form.reset(
      {
        ...examenRawValue,
        id: { value: examenRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ExamenFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
      deleted: false,
    };
  }

  private convertExamenRawValueToExamen(rawExamen: ExamenFormRawValue | NewExamenFormRawValue): IExamen | NewExamen {
    return {
      ...rawExamen,
      date: dayjs(rawExamen.date, DATE_TIME_FORMAT),
    };
  }

  private convertExamenToExamenRawValue(
    examen: IExamen | (Partial<NewExamen> & ExamenFormDefaults)
  ): ExamenFormRawValue | PartialWithRequiredKeyOf<NewExamenFormRawValue> {
    return {
      ...examen,
      date: examen.date ? examen.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
