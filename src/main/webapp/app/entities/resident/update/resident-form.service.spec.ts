import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../resident.test-samples';

import { ResidentFormService } from './resident-form.service';

describe('Resident Form Service', () => {
  let service: ResidentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResidentFormService);
  });

  describe('Service methods', () => {
    describe('createResidentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createResidentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numero: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            dateNaissance: expect.any(Object),
            sexe: expect.any(Object),
            dateArrivee: expect.any(Object),
            chambre: expect.any(Object),
            taille: expect.any(Object),
            denutrition: expect.any(Object),
            deleted: expect.any(Object),
            etablissement: expect.any(Object),
          })
        );
      });

      it('passing IResident should create a new form with FormGroup', () => {
        const formGroup = service.createResidentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            numero: expect.any(Object),
            nom: expect.any(Object),
            prenom: expect.any(Object),
            dateNaissance: expect.any(Object),
            sexe: expect.any(Object),
            dateArrivee: expect.any(Object),
            chambre: expect.any(Object),
            taille: expect.any(Object),
            denutrition: expect.any(Object),
            deleted: expect.any(Object),
            etablissement: expect.any(Object),
          })
        );
      });
    });

    describe('getResident', () => {
      it('should return NewResident for default Resident initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createResidentFormGroup(sampleWithNewData);

        const resident = service.getResident(formGroup) as any;

        expect(resident).toMatchObject(sampleWithNewData);
      });

      it('should return NewResident for empty Resident initial value', () => {
        const formGroup = service.createResidentFormGroup();

        const resident = service.getResident(formGroup) as any;

        expect(resident).toMatchObject({});
      });

      it('should return IResident', () => {
        const formGroup = service.createResidentFormGroup(sampleWithRequiredData);

        const resident = service.getResident(formGroup) as any;

        expect(resident).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IResident should not enable id FormControl', () => {
        const formGroup = service.createResidentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewResident should disable id FormControl', () => {
        const formGroup = service.createResidentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
