import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDemande, NewDemande } from '../demande.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDemande for edit and NewDemandeFormGroupInput for create.
 */
type DemandeFormGroupInput = IDemande | PartialWithRequiredKeyOf<NewDemande>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDemande | NewDemande> = Omit<T, 'dateEmition'> & {
  dateEmition?: string | null;
};

type DemandeFormRawValue = FormValueOf<IDemande>;

type NewDemandeFormRawValue = FormValueOf<NewDemande>;

type DemandeFormDefaults = Pick<NewDemande, 'id' | 'dateEmition' | 'deleted'>;

type DemandeFormGroupContent = {
  id: FormControl<DemandeFormRawValue['id'] | NewDemande['id']>;
  dateEmition: FormControl<DemandeFormRawValue['dateEmition']>;
  dateLimite: FormControl<DemandeFormRawValue['dateLimite']>;
  description: FormControl<DemandeFormRawValue['description']>;
  deleted: FormControl<DemandeFormRawValue['deleted']>;
  resident: FormControl<DemandeFormRawValue['resident']>;
  docteur: FormControl<DemandeFormRawValue['docteur']>;
};

export type DemandeFormGroup = FormGroup<DemandeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DemandeFormService {
  createDemandeFormGroup(demande: DemandeFormGroupInput = { id: null }): DemandeFormGroup {
    const demandeRawValue = this.convertDemandeToDemandeRawValue({
      ...this.getFormDefaults(),
      ...demande,
    });
    return new FormGroup<DemandeFormGroupContent>({
      id: new FormControl(
        { value: demandeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      dateEmition: new FormControl(demandeRawValue.dateEmition, {
        validators: [Validators.required],
      }),
      dateLimite: new FormControl(demandeRawValue.dateLimite, {
        validators: [Validators.required],
      }),
      description: new FormControl(demandeRawValue.description, {
        validators: [Validators.required],
      }),
      deleted: new FormControl(demandeRawValue.deleted),
      resident: new FormControl(demandeRawValue.resident),
      docteur: new FormControl(demandeRawValue.docteur),
    });
  }

  getDemande(form: DemandeFormGroup): IDemande | NewDemande {
    return this.convertDemandeRawValueToDemande(form.getRawValue() as DemandeFormRawValue | NewDemandeFormRawValue);
  }

  resetForm(form: DemandeFormGroup, demande: DemandeFormGroupInput): void {
    const demandeRawValue = this.convertDemandeToDemandeRawValue({ ...this.getFormDefaults(), ...demande });
    form.reset(
      {
        ...demandeRawValue,
        id: { value: demandeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DemandeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateEmition: currentTime,
      deleted: false,
    };
  }

  private convertDemandeRawValueToDemande(rawDemande: DemandeFormRawValue | NewDemandeFormRawValue): IDemande | NewDemande {
    return {
      ...rawDemande,
      dateEmition: dayjs(rawDemande.dateEmition, DATE_TIME_FORMAT),
    };
  }

  private convertDemandeToDemandeRawValue(
    demande: IDemande | (Partial<NewDemande> & DemandeFormDefaults)
  ): DemandeFormRawValue | PartialWithRequiredKeyOf<NewDemandeFormRawValue> {
    return {
      ...demande,
      dateEmition: demande.dateEmition ? demande.dateEmition.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
