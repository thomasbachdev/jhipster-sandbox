import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ExamenFormService } from './examen-form.service';
import { ExamenService } from '../service/examen.service';
import { IExamen } from '../examen.model';
import { IResident } from 'app/entities/resident/resident.model';
import { ResidentService } from 'app/entities/resident/service/resident.service';
import { ISoignant } from 'app/entities/soignant/soignant.model';
import { SoignantService } from 'app/entities/soignant/service/soignant.service';

import { ExamenUpdateComponent } from './examen-update.component';

describe('Examen Management Update Component', () => {
  let comp: ExamenUpdateComponent;
  let fixture: ComponentFixture<ExamenUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let examenFormService: ExamenFormService;
  let examenService: ExamenService;
  let residentService: ResidentService;
  let soignantService: SoignantService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ExamenUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ExamenUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExamenUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    examenFormService = TestBed.inject(ExamenFormService);
    examenService = TestBed.inject(ExamenService);
    residentService = TestBed.inject(ResidentService);
    soignantService = TestBed.inject(SoignantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Resident query and add missing value', () => {
      const examen: IExamen = { id: 456 };
      const resident: IResident = { id: 26276 };
      examen.resident = resident;

      const residentCollection: IResident[] = [{ id: 78620 }];
      jest.spyOn(residentService, 'query').mockReturnValue(of(new HttpResponse({ body: residentCollection })));
      const additionalResidents = [resident];
      const expectedCollection: IResident[] = [...additionalResidents, ...residentCollection];
      jest.spyOn(residentService, 'addResidentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ examen });
      comp.ngOnInit();

      expect(residentService.query).toHaveBeenCalled();
      expect(residentService.addResidentToCollectionIfMissing).toHaveBeenCalledWith(
        residentCollection,
        ...additionalResidents.map(expect.objectContaining)
      );
      expect(comp.residentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Soignant query and add missing value', () => {
      const examen: IExamen = { id: 456 };
      const soignant: ISoignant = { id: 51738 };
      examen.soignant = soignant;

      const soignantCollection: ISoignant[] = [{ id: 8670 }];
      jest.spyOn(soignantService, 'query').mockReturnValue(of(new HttpResponse({ body: soignantCollection })));
      const additionalSoignants = [soignant];
      const expectedCollection: ISoignant[] = [...additionalSoignants, ...soignantCollection];
      jest.spyOn(soignantService, 'addSoignantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ examen });
      comp.ngOnInit();

      expect(soignantService.query).toHaveBeenCalled();
      expect(soignantService.addSoignantToCollectionIfMissing).toHaveBeenCalledWith(
        soignantCollection,
        ...additionalSoignants.map(expect.objectContaining)
      );
      expect(comp.soignantsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const examen: IExamen = { id: 456 };
      const resident: IResident = { id: 75730 };
      examen.resident = resident;
      const soignant: ISoignant = { id: 1372 };
      examen.soignant = soignant;

      activatedRoute.data = of({ examen });
      comp.ngOnInit();

      expect(comp.residentsSharedCollection).toContain(resident);
      expect(comp.soignantsSharedCollection).toContain(soignant);
      expect(comp.examen).toEqual(examen);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExamen>>();
      const examen = { id: 123 };
      jest.spyOn(examenFormService, 'getExamen').mockReturnValue(examen);
      jest.spyOn(examenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ examen });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: examen }));
      saveSubject.complete();

      // THEN
      expect(examenFormService.getExamen).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(examenService.update).toHaveBeenCalledWith(expect.objectContaining(examen));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExamen>>();
      const examen = { id: 123 };
      jest.spyOn(examenFormService, 'getExamen').mockReturnValue({ id: null });
      jest.spyOn(examenService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ examen: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: examen }));
      saveSubject.complete();

      // THEN
      expect(examenFormService.getExamen).toHaveBeenCalled();
      expect(examenService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExamen>>();
      const examen = { id: 123 };
      jest.spyOn(examenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ examen });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(examenService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareResident', () => {
      it('Should forward to residentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(residentService, 'compareResident');
        comp.compareResident(entity, entity2);
        expect(residentService.compareResident).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSoignant', () => {
      it('Should forward to soignantService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(soignantService, 'compareSoignant');
        comp.compareSoignant(entity, entity2);
        expect(soignantService.compareSoignant).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
