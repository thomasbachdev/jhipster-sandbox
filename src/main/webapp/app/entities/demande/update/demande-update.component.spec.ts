import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DemandeFormService } from './demande-form.service';
import { DemandeService } from '../service/demande.service';
import { IDemande } from '../demande.model';
import { IResident } from 'app/entities/resident/resident.model';
import { ResidentService } from 'app/entities/resident/service/resident.service';
import { IDocteur } from 'app/entities/docteur/docteur.model';
import { DocteurService } from 'app/entities/docteur/service/docteur.service';

import { DemandeUpdateComponent } from './demande-update.component';

describe('Demande Management Update Component', () => {
  let comp: DemandeUpdateComponent;
  let fixture: ComponentFixture<DemandeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let demandeFormService: DemandeFormService;
  let demandeService: DemandeService;
  let residentService: ResidentService;
  let docteurService: DocteurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DemandeUpdateComponent],
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
      .overrideTemplate(DemandeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DemandeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    demandeFormService = TestBed.inject(DemandeFormService);
    demandeService = TestBed.inject(DemandeService);
    residentService = TestBed.inject(ResidentService);
    docteurService = TestBed.inject(DocteurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Resident query and add missing value', () => {
      const demande: IDemande = { id: 456 };
      const resident: IResident = { id: 23296 };
      demande.resident = resident;

      const residentCollection: IResident[] = [{ id: 5657 }];
      jest.spyOn(residentService, 'query').mockReturnValue(of(new HttpResponse({ body: residentCollection })));
      const additionalResidents = [resident];
      const expectedCollection: IResident[] = [...additionalResidents, ...residentCollection];
      jest.spyOn(residentService, 'addResidentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ demande });
      comp.ngOnInit();

      expect(residentService.query).toHaveBeenCalled();
      expect(residentService.addResidentToCollectionIfMissing).toHaveBeenCalledWith(
        residentCollection,
        ...additionalResidents.map(expect.objectContaining)
      );
      expect(comp.residentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Docteur query and add missing value', () => {
      const demande: IDemande = { id: 456 };
      const docteur: IDocteur = { id: 77211 };
      demande.docteur = docteur;

      const docteurCollection: IDocteur[] = [{ id: 29689 }];
      jest.spyOn(docteurService, 'query').mockReturnValue(of(new HttpResponse({ body: docteurCollection })));
      const additionalDocteurs = [docteur];
      const expectedCollection: IDocteur[] = [...additionalDocteurs, ...docteurCollection];
      jest.spyOn(docteurService, 'addDocteurToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ demande });
      comp.ngOnInit();

      expect(docteurService.query).toHaveBeenCalled();
      expect(docteurService.addDocteurToCollectionIfMissing).toHaveBeenCalledWith(
        docteurCollection,
        ...additionalDocteurs.map(expect.objectContaining)
      );
      expect(comp.docteursSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const demande: IDemande = { id: 456 };
      const resident: IResident = { id: 90839 };
      demande.resident = resident;
      const docteur: IDocteur = { id: 4292 };
      demande.docteur = docteur;

      activatedRoute.data = of({ demande });
      comp.ngOnInit();

      expect(comp.residentsSharedCollection).toContain(resident);
      expect(comp.docteursSharedCollection).toContain(docteur);
      expect(comp.demande).toEqual(demande);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDemande>>();
      const demande = { id: 123 };
      jest.spyOn(demandeFormService, 'getDemande').mockReturnValue(demande);
      jest.spyOn(demandeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demande });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: demande }));
      saveSubject.complete();

      // THEN
      expect(demandeFormService.getDemande).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(demandeService.update).toHaveBeenCalledWith(expect.objectContaining(demande));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDemande>>();
      const demande = { id: 123 };
      jest.spyOn(demandeFormService, 'getDemande').mockReturnValue({ id: null });
      jest.spyOn(demandeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demande: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: demande }));
      saveSubject.complete();

      // THEN
      expect(demandeFormService.getDemande).toHaveBeenCalled();
      expect(demandeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDemande>>();
      const demande = { id: 123 };
      jest.spyOn(demandeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demande });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(demandeService.update).toHaveBeenCalled();
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

    describe('compareDocteur', () => {
      it('Should forward to docteurService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(docteurService, 'compareDocteur');
        comp.compareDocteur(entity, entity2);
        expect(docteurService.compareDocteur).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
