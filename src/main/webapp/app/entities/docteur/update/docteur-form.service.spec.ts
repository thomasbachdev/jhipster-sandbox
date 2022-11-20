import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../docteur.test-samples';

import { DocteurFormService } from './docteur-form.service';

describe('Docteur Form Service', () => {
  let service: DocteurFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DocteurFormService);
  });

  describe('Service methods', () => {
    describe('createDocteurFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDocteurFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            deleted: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing IDocteur should create a new form with FormGroup', () => {
        const formGroup = service.createDocteurFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            deleted: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getDocteur', () => {
      it('should return NewDocteur for default Docteur initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDocteurFormGroup(sampleWithNewData);

        const docteur = service.getDocteur(formGroup) as any;

        expect(docteur).toMatchObject(sampleWithNewData);
      });

      it('should return NewDocteur for empty Docteur initial value', () => {
        const formGroup = service.createDocteurFormGroup();

        const docteur = service.getDocteur(formGroup) as any;

        expect(docteur).toMatchObject({});
      });

      it('should return IDocteur', () => {
        const formGroup = service.createDocteurFormGroup(sampleWithRequiredData);

        const docteur = service.getDocteur(formGroup) as any;

        expect(docteur).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDocteur should not enable id FormControl', () => {
        const formGroup = service.createDocteurFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDocteur should disable id FormControl', () => {
        const formGroup = service.createDocteurFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
