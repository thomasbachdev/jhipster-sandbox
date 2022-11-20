import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DocteurFormService } from './docteur-form.service';
import { DocteurService } from '../service/docteur.service';
import { IDocteur } from '../docteur.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { DocteurUpdateComponent } from './docteur-update.component';

describe('Docteur Management Update Component', () => {
  let comp: DocteurUpdateComponent;
  let fixture: ComponentFixture<DocteurUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let docteurFormService: DocteurFormService;
  let docteurService: DocteurService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DocteurUpdateComponent],
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
      .overrideTemplate(DocteurUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DocteurUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    docteurFormService = TestBed.inject(DocteurFormService);
    docteurService = TestBed.inject(DocteurService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const docteur: IDocteur = { id: 456 };
      const user: IUser = { id: 72229 };
      docteur.user = user;

      const userCollection: IUser[] = [{ id: 88453 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ docteur });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const docteur: IDocteur = { id: 456 };
      const user: IUser = { id: 29441 };
      docteur.user = user;

      activatedRoute.data = of({ docteur });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.docteur).toEqual(docteur);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocteur>>();
      const docteur = { id: 123 };
      jest.spyOn(docteurFormService, 'getDocteur').mockReturnValue(docteur);
      jest.spyOn(docteurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ docteur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: docteur }));
      saveSubject.complete();

      // THEN
      expect(docteurFormService.getDocteur).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(docteurService.update).toHaveBeenCalledWith(expect.objectContaining(docteur));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocteur>>();
      const docteur = { id: 123 };
      jest.spyOn(docteurFormService, 'getDocteur').mockReturnValue({ id: null });
      jest.spyOn(docteurService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ docteur: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: docteur }));
      saveSubject.complete();

      // THEN
      expect(docteurFormService.getDocteur).toHaveBeenCalled();
      expect(docteurService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocteur>>();
      const docteur = { id: 123 };
      jest.spyOn(docteurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ docteur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(docteurService.update).toHaveBeenCalled();
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
  });
});
