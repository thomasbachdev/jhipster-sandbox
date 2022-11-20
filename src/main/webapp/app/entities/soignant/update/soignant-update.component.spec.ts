import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SoignantFormService } from './soignant-form.service';
import { SoignantService } from '../service/soignant.service';
import { ISoignant } from '../soignant.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IEtablissement } from 'app/entities/etablissement/etablissement.model';
import { EtablissementService } from 'app/entities/etablissement/service/etablissement.service';

import { SoignantUpdateComponent } from './soignant-update.component';

describe('Soignant Management Update Component', () => {
  let comp: SoignantUpdateComponent;
  let fixture: ComponentFixture<SoignantUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let soignantFormService: SoignantFormService;
  let soignantService: SoignantService;
  let userService: UserService;
  let etablissementService: EtablissementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SoignantUpdateComponent],
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
      .overrideTemplate(SoignantUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SoignantUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    soignantFormService = TestBed.inject(SoignantFormService);
    soignantService = TestBed.inject(SoignantService);
    userService = TestBed.inject(UserService);
    etablissementService = TestBed.inject(EtablissementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const soignant: ISoignant = { id: 456 };
      const user: IUser = { id: 33602 };
      soignant.user = user;

      const userCollection: IUser[] = [{ id: 63817 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ soignant });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Etablissement query and add missing value', () => {
      const soignant: ISoignant = { id: 456 };
      const etablissement: IEtablissement = { id: 4927 };
      soignant.etablissement = etablissement;

      const etablissementCollection: IEtablissement[] = [{ id: 4662 }];
      jest.spyOn(etablissementService, 'query').mockReturnValue(of(new HttpResponse({ body: etablissementCollection })));
      const additionalEtablissements = [etablissement];
      const expectedCollection: IEtablissement[] = [...additionalEtablissements, ...etablissementCollection];
      jest.spyOn(etablissementService, 'addEtablissementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ soignant });
      comp.ngOnInit();

      expect(etablissementService.query).toHaveBeenCalled();
      expect(etablissementService.addEtablissementToCollectionIfMissing).toHaveBeenCalledWith(
        etablissementCollection,
        ...additionalEtablissements.map(expect.objectContaining)
      );
      expect(comp.etablissementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const soignant: ISoignant = { id: 456 };
      const user: IUser = { id: 43612 };
      soignant.user = user;
      const etablissement: IEtablissement = { id: 16338 };
      soignant.etablissement = etablissement;

      activatedRoute.data = of({ soignant });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.etablissementsSharedCollection).toContain(etablissement);
      expect(comp.soignant).toEqual(soignant);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISoignant>>();
      const soignant = { id: 123 };
      jest.spyOn(soignantFormService, 'getSoignant').mockReturnValue(soignant);
      jest.spyOn(soignantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ soignant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: soignant }));
      saveSubject.complete();

      // THEN
      expect(soignantFormService.getSoignant).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(soignantService.update).toHaveBeenCalledWith(expect.objectContaining(soignant));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISoignant>>();
      const soignant = { id: 123 };
      jest.spyOn(soignantFormService, 'getSoignant').mockReturnValue({ id: null });
      jest.spyOn(soignantService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ soignant: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: soignant }));
      saveSubject.complete();

      // THEN
      expect(soignantFormService.getSoignant).toHaveBeenCalled();
      expect(soignantService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISoignant>>();
      const soignant = { id: 123 };
      jest.spyOn(soignantService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ soignant });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(soignantService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
