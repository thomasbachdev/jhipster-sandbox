import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../rappel.test-samples';

import { RappelFormService } from './rappel-form.service';

describe('Rappel Form Service', () => {
  let service: RappelFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RappelFormService);
  });

  describe('Service methods', () => {
    describe('createRappelFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRappelFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            description: expect.any(Object),
            deleted: expect.any(Object),
            docteur: expect.any(Object),
          })
        );
      });

      it('passing IRappel should create a new form with FormGroup', () => {
        const formGroup = service.createRappelFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            description: expect.any(Object),
            deleted: expect.any(Object),
            docteur: expect.any(Object),
          })
        );
      });
    });

    describe('getRappel', () => {
      it('should return NewRappel for default Rappel initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRappelFormGroup(sampleWithNewData);

        const rappel = service.getRappel(formGroup) as any;

        expect(rappel).toMatchObject(sampleWithNewData);
      });

      it('should return NewRappel for empty Rappel initial value', () => {
        const formGroup = service.createRappelFormGroup();

        const rappel = service.getRappel(formGroup) as any;

        expect(rappel).toMatchObject({});
      });

      it('should return IRappel', () => {
        const formGroup = service.createRappelFormGroup(sampleWithRequiredData);

        const rappel = service.getRappel(formGroup) as any;

        expect(rappel).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRappel should not enable id FormControl', () => {
        const formGroup = service.createRappelFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRappel should disable id FormControl', () => {
        const formGroup = service.createRappelFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
