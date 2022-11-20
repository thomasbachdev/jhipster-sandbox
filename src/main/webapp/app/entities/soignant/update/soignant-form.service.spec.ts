import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../soignant.test-samples';

import { SoignantFormService } from './soignant-form.service';

describe('Soignant Form Service', () => {
  let service: SoignantFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SoignantFormService);
  });

  describe('Service methods', () => {
    describe('createSoignantFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSoignantFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            deleted: expect.any(Object),
            user: expect.any(Object),
            etablissement: expect.any(Object),
          })
        );
      });

      it('passing ISoignant should create a new form with FormGroup', () => {
        const formGroup = service.createSoignantFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            deleted: expect.any(Object),
            user: expect.any(Object),
            etablissement: expect.any(Object),
          })
        );
      });
    });

    describe('getSoignant', () => {
      it('should return NewSoignant for default Soignant initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSoignantFormGroup(sampleWithNewData);

        const soignant = service.getSoignant(formGroup) as any;

        expect(soignant).toMatchObject(sampleWithNewData);
      });

      it('should return NewSoignant for empty Soignant initial value', () => {
        const formGroup = service.createSoignantFormGroup();

        const soignant = service.getSoignant(formGroup) as any;

        expect(soignant).toMatchObject({});
      });

      it('should return ISoignant', () => {
        const formGroup = service.createSoignantFormGroup(sampleWithRequiredData);

        const soignant = service.getSoignant(formGroup) as any;

        expect(soignant).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISoignant should not enable id FormControl', () => {
        const formGroup = service.createSoignantFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSoignant should disable id FormControl', () => {
        const formGroup = service.createSoignantFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
