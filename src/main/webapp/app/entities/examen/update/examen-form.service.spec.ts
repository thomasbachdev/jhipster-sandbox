import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../examen.test-samples';

import { ExamenFormService } from './examen-form.service';

describe('Examen Form Service', () => {
  let service: ExamenFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExamenFormService);
  });

  describe('Service methods', () => {
    describe('createExamenFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createExamenFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            poids: expect.any(Object),
            albumine: expect.any(Object),
            imc: expect.any(Object),
            epa: expect.any(Object),
            commentaire: expect.any(Object),
            deleted: expect.any(Object),
            resident: expect.any(Object),
            soignant: expect.any(Object),
          })
        );
      });

      it('passing IExamen should create a new form with FormGroup', () => {
        const formGroup = service.createExamenFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            poids: expect.any(Object),
            albumine: expect.any(Object),
            imc: expect.any(Object),
            epa: expect.any(Object),
            commentaire: expect.any(Object),
            deleted: expect.any(Object),
            resident: expect.any(Object),
            soignant: expect.any(Object),
          })
        );
      });
    });

    describe('getExamen', () => {
      it('should return NewExamen for default Examen initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createExamenFormGroup(sampleWithNewData);

        const examen = service.getExamen(formGroup) as any;

        expect(examen).toMatchObject(sampleWithNewData);
      });

      it('should return NewExamen for empty Examen initial value', () => {
        const formGroup = service.createExamenFormGroup();

        const examen = service.getExamen(formGroup) as any;

        expect(examen).toMatchObject({});
      });

      it('should return IExamen', () => {
        const formGroup = service.createExamenFormGroup(sampleWithRequiredData);

        const examen = service.getExamen(formGroup) as any;

        expect(examen).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IExamen should not enable id FormControl', () => {
        const formGroup = service.createExamenFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewExamen should disable id FormControl', () => {
        const formGroup = service.createExamenFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
