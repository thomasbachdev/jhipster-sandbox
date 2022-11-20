import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { SoignantFormService, SoignantFormGroup } from './soignant-form.service';
import { ISoignant } from '../soignant.model';
import { SoignantService } from '../service/soignant.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IEtablissement } from 'app/entities/etablissement/etablissement.model';
import { EtablissementService } from 'app/entities/etablissement/service/etablissement.service';

@Component({
  selector: 'jhi-soignant-update',
  templateUrl: './soignant-update.component.html',
})
export class SoignantUpdateComponent implements OnInit {
  isSaving = false;
  soignant: ISoignant | null = null;

  usersSharedCollection: IUser[] = [];
  etablissementsSharedCollection: IEtablissement[] = [];

  editForm: SoignantFormGroup = this.soignantFormService.createSoignantFormGroup();

  constructor(
    protected soignantService: SoignantService,
    protected soignantFormService: SoignantFormService,
    protected userService: UserService,
    protected etablissementService: EtablissementService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareEtablissement = (o1: IEtablissement | null, o2: IEtablissement | null): boolean =>
    this.etablissementService.compareEtablissement(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ soignant }) => {
      this.soignant = soignant;
      if (soignant) {
        this.updateForm(soignant);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const soignant = this.soignantFormService.getSoignant(this.editForm);
    if (soignant.id !== null) {
      this.subscribeToSaveResponse(this.soignantService.update(soignant));
    } else {
      this.subscribeToSaveResponse(this.soignantService.create(soignant));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISoignant>>): void {
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

  protected updateForm(soignant: ISoignant): void {
    this.soignant = soignant;
    this.soignantFormService.resetForm(this.editForm, soignant);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, soignant.user);
    this.etablissementsSharedCollection = this.etablissementService.addEtablissementToCollectionIfMissing<IEtablissement>(
      this.etablissementsSharedCollection,
      soignant.etablissement
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.soignant?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.etablissementService
      .query()
      .pipe(map((res: HttpResponse<IEtablissement[]>) => res.body ?? []))
      .pipe(
        map((etablissements: IEtablissement[]) =>
          this.etablissementService.addEtablissementToCollectionIfMissing<IEtablissement>(etablissements, this.soignant?.etablissement)
        )
      )
      .subscribe((etablissements: IEtablissement[]) => (this.etablissementsSharedCollection = etablissements));
  }
}
