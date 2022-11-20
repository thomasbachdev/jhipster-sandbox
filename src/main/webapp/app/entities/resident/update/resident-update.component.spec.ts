import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ResidentFormService } from './resident-form.service';
import { ResidentService } from '../service/resident.service';
import { IResident } from '../resident.model';
import { IEtablissement } from 'app/entities/etablissement/etablissement.model';
import { EtablissementService } from 'app/entities/etablissement/service/etablissement.service';

import { ResidentUpdateComponent } from './resident-update.component';

describe('Resident Management Update Component', () => {
  let comp: ResidentUpdateComponent;
  let fixture: ComponentFixture<ResidentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let residentFormService: ResidentFormService;
  let residentService: ResidentService;
  let etablissementService: EtablissementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ResidentUpdateComponent],
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
      .overrideTemplate(ResidentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResidentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    residentFormService = TestBed.inject(ResidentFormService);
    residentService = TestBed.inject(ResidentService);
    etablissementService = TestBed.inject(EtablissementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Etablissement query and add missing value', () => {
      const resident: IResident = { id: 456 };
      const etablissement: IEtablissement = { id: 14263 };
      resident.etablissement = etablissement;

      const etablissementCollection: IEtablissement[] = [{ id: 52818 }];
      jest.spyOn(etablissementService, 'query').mockReturnValue(of(new HttpResponse({ body: etablissementCollection })));
      const additionalEtablissements = [etablissement];
      const expectedCollection: IEtablissement[] = [...additionalEtablissements, ...etablissementCollection];
      jest.spyOn(etablissementService, 'addEtablissementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ resident });
      comp.ngOnInit();

      expect(etablissementService.query).toHaveBeenCalled();
      expect(etablissementService.addEtablissementToCollectionIfMissing).toHaveBeenCalledWith(
        etablissementCollection,
        ...additionalEtablissements.map(expect.objectContaining)
      );
      expect(comp.etablissementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const resident: IResident = { id: 456 };
      const etablissement: IEtablissement = { id: 63788 };
      resident.etablissement = etablissement;

      activatedRoute.data = of({ resident });
      comp.ngOnInit();

      expect(comp.etablissementsSharedCollection).toContain(etablissement);
      expect(comp.resident).toEqual(resident);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResident>>();
      const resident = { id: 123 };
      jest.spyOn(residentFormService, 'getResident').mockReturnValue(resident);
      jest.spyOn(residentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resident });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resident }));
      saveSubject.complete();

      // THEN
      expect(residentFormService.getResident).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(residentService.update).toHaveBeenCalledWith(expect.objectContaining(resident));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResident>>();
      const resident = { id: 123 };
      jest.spyOn(residentFormService, 'getResident').mockReturnValue({ id: null });
      jest.spyOn(residentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resident: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resident }));
      saveSubject.complete();

      // THEN
      expect(residentFormService.getResident).toHaveBeenCalled();
      expect(residentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResident>>();
      const resident = { id: 123 };
      jest.spyOn(residentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resident });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(residentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEtablissement', () => {
      it('Should forward to etablissementService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(etablissementService, 'compareEtablissement');
        comp.compareEtablissement(entity, entity2);
        expect(etablissementService.compareEtablissement).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
