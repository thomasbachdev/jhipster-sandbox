import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RappelFormService } from './rappel-form.service';
import { RappelService } from '../service/rappel.service';
import { IRappel } from '../rappel.model';
import { IDocteur } from 'app/entities/docteur/docteur.model';
import { DocteurService } from 'app/entities/docteur/service/docteur.service';

import { RappelUpdateComponent } from './rappel-update.component';

describe('Rappel Management Update Component', () => {
  let comp: RappelUpdateComponent;
  let fixture: ComponentFixture<RappelUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let rappelFormService: RappelFormService;
  let rappelService: RappelService;
  let docteurService: DocteurService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RappelUpdateComponent],
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
      .overrideTemplate(RappelUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RappelUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    rappelFormService = TestBed.inject(RappelFormService);
    rappelService = TestBed.inject(RappelService);
    docteurService = TestBed.inject(DocteurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Docteur query and add missing value', () => {
      const rappel: IRappel = { id: 456 };
      const docteur: IDocteur = { id: 82656 };
      rappel.docteur = docteur;

      const docteurCollection: IDocteur[] = [{ id: 58256 }];
      jest.spyOn(docteurService, 'query').mockReturnValue(of(new HttpResponse({ body: docteurCollection })));
      const additionalDocteurs = [docteur];
      const expectedCollection: IDocteur[] = [...additionalDocteurs, ...docteurCollection];
      jest.spyOn(docteurService, 'addDocteurToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rappel });
      comp.ngOnInit();

      expect(docteurService.query).toHaveBeenCalled();
      expect(docteurService.addDocteurToCollectionIfMissing).toHaveBeenCalledWith(
        docteurCollection,
        ...additionalDocteurs.map(expect.objectContaining)
      );
      expect(comp.docteursSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const rappel: IRappel = { id: 456 };
      const docteur: IDocteur = { id: 58093 };
      rappel.docteur = docteur;

      activatedRoute.data = of({ rappel });
      comp.ngOnInit();

      expect(comp.docteursSharedCollection).toContain(docteur);
      expect(comp.rappel).toEqual(rappel);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRappel>>();
      const rappel = { id: 123 };
      jest.spyOn(rappelFormService, 'getRappel').mockReturnValue(rappel);
      jest.spyOn(rappelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rappel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: rappel }));
      saveSubject.complete();

      // THEN
      expect(rappelFormService.getRappel).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(rappelService.update).toHaveBeenCalledWith(expect.objectContaining(rappel));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRappel>>();
      const rappel = { id: 123 };
      jest.spyOn(rappelFormService, 'getRappel').mockReturnValue({ id: null });
      jest.spyOn(rappelService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rappel: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: rappel }));
      saveSubject.complete();

      // THEN
      expect(rappelFormService.getRappel).toHaveBeenCalled();
      expect(rappelService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRappel>>();
      const rappel = { id: 123 };
      jest.spyOn(rappelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rappel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(rappelService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
