import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ResidentFormService, ResidentFormGroup } from './resident-form.service';
import { IResident } from '../resident.model';
import { ResidentService } from '../service/resident.service';
import { IEtablissement } from 'app/entities/etablissement/etablissement.model';
import { EtablissementService } from 'app/entities/etablissement/service/etablissement.service';
import { Sexe } from 'app/entities/enumerations/sexe.model';
import { StadeDenutrition } from 'app/entities/enumerations/stade-denutrition.model';

@Component({
  selector: 'jhi-resident-update',
  templateUrl: './resident-update.component.html',
})
export class ResidentUpdateComponent implements OnInit {
  isSaving = false;
  resident: IResident | null = null;
  sexeValues = Object.keys(Sexe);
  stadeDenutritionValues = Object.keys(StadeDenutrition);

  etablissementsSharedCollection: IEtablissement[] = [];

  editForm: ResidentFormGroup = this.residentFormService.createResidentFormGroup();

  constructor(
    protected residentService: ResidentService,
    protected residentFormService: ResidentFormService,
    protected etablissementService: EtablissementService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareEtablissement = (o1: IEtablissement | null, o2: IEtablissement | null): boolean =>
    this.etablissementService.compareEtablissement(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resident }) => {
      this.resident = resident;
      if (resident) {
        this.updateForm(resident);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const resident = this.residentFormService.getResident(this.editForm);
    if (resident.id !== null) {
      this.subscribeToSaveResponse(this.residentService.update(resident));
    } else {
      this.subscribeToSaveResponse(this.residentService.create(resident));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResident>>): void {
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

  protected updateForm(resident: IResident): void {
    this.resident = resident;
    this.residentFormService.resetForm(this.editForm, resident);

    this.etablissementsSharedCollection = this.etablissementService.addEtablissementToCollectionIfMissing<IEtablissement>(
      this.etablissementsSharedCollection,
      resident.etablissement
    );
  }

  protected loadRelationshipsOptions(): void {
    this.etablissementService
      .query()
      .pipe(map((res: HttpResponse<IEtablissement[]>) => res.body ?? []))
      .pipe(
        map((etablissements: IEtablissement[]) =>
          this.etablissementService.addEtablissementToCollectionIfMissing<IEtablissement>(etablissements, this.resident?.etablissement)
        )
      )
      .subscribe((etablissements: IEtablissement[]) => (this.etablissementsSharedCollection = etablissements));
  }
}
