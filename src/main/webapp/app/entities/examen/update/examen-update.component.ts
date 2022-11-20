import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ExamenFormService, ExamenFormGroup } from './examen-form.service';
import { IExamen } from '../examen.model';
import { ExamenService } from '../service/examen.service';
import { IResident } from 'app/entities/resident/resident.model';
import { ResidentService } from 'app/entities/resident/service/resident.service';
import { ISoignant } from 'app/entities/soignant/soignant.model';
import { SoignantService } from 'app/entities/soignant/service/soignant.service';

@Component({
  selector: 'jhi-examen-update',
  templateUrl: './examen-update.component.html',
})
export class ExamenUpdateComponent implements OnInit {
  isSaving = false;
  examen: IExamen | null = null;

  residentsSharedCollection: IResident[] = [];
  soignantsSharedCollection: ISoignant[] = [];

  editForm: ExamenFormGroup = this.examenFormService.createExamenFormGroup();

  constructor(
    protected examenService: ExamenService,
    protected examenFormService: ExamenFormService,
    protected residentService: ResidentService,
    protected soignantService: SoignantService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareResident = (o1: IResident | null, o2: IResident | null): boolean => this.residentService.compareResident(o1, o2);

  compareSoignant = (o1: ISoignant | null, o2: ISoignant | null): boolean => this.soignantService.compareSoignant(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ examen }) => {
      this.examen = examen;
      if (examen) {
        this.updateForm(examen);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const examen = this.examenFormService.getExamen(this.editForm);
    if (examen.id !== null) {
      this.subscribeToSaveResponse(this.examenService.update(examen));
    } else {
      this.subscribeToSaveResponse(this.examenService.create(examen));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExamen>>): void {
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

  protected updateForm(examen: IExamen): void {
    this.examen = examen;
    this.examenFormService.resetForm(this.editForm, examen);

    this.residentsSharedCollection = this.residentService.addResidentToCollectionIfMissing<IResident>(
      this.residentsSharedCollection,
      examen.resident
    );
    this.soignantsSharedCollection = this.soignantService.addSoignantToCollectionIfMissing<ISoignant>(
      this.soignantsSharedCollection,
      examen.soignant
    );
  }

  protected loadRelationshipsOptions(): void {
    this.residentService
      .query()
      .pipe(map((res: HttpResponse<IResident[]>) => res.body ?? []))
      .pipe(
        map((residents: IResident[]) => this.residentService.addResidentToCollectionIfMissing<IResident>(residents, this.examen?.resident))
      )
      .subscribe((residents: IResident[]) => (this.residentsSharedCollection = residents));

    this.soignantService
      .query()
      .pipe(map((res: HttpResponse<ISoignant[]>) => res.body ?? []))
      .pipe(
        map((soignants: ISoignant[]) => this.soignantService.addSoignantToCollectionIfMissing<ISoignant>(soignants, this.examen?.soignant))
      )
      .subscribe((soignants: ISoignant[]) => (this.soignantsSharedCollection = soignants));
  }
}
