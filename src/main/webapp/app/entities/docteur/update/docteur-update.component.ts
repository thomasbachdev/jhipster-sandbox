import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DocteurFormService, DocteurFormGroup } from './docteur-form.service';
import { IDocteur } from '../docteur.model';
import { DocteurService } from '../service/docteur.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-docteur-update',
  templateUrl: './docteur-update.component.html',
})
export class DocteurUpdateComponent implements OnInit {
  isSaving = false;
  docteur: IDocteur | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: DocteurFormGroup = this.docteurFormService.createDocteurFormGroup();

  constructor(
    protected docteurService: DocteurService,
    protected docteurFormService: DocteurFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ docteur }) => {
      this.docteur = docteur;
      if (docteur) {
        this.updateForm(docteur);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const docteur = this.docteurFormService.getDocteur(this.editForm);
    if (docteur.id !== null) {
      this.subscribeToSaveResponse(this.docteurService.update(docteur));
    } else {
      this.subscribeToSaveResponse(this.docteurService.create(docteur));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDocteur>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(docteur: IDocteur): void {
    this.docteur = docteur;
    this.docteurFormService.resetForm(this.editForm, docteur);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, docteur.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.docteur?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
