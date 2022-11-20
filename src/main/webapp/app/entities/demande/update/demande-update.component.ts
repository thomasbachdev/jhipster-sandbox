import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DemandeFormService, DemandeFormGroup } from './demande-form.service';
import { IDemande } from '../demande.model';
import { DemandeService } from '../service/demande.service';
import { IResident } from 'app/entities/resident/resident.model';
import { ResidentService } from 'app/entities/resident/service/resident.service';
import { IDocteur } from 'app/entities/docteur/docteur.model';
import { DocteurService } from 'app/entities/docteur/service/docteur.service';

@Component({
  selector: 'jhi-demande-update',
  templateUrl: './demande-update.component.html',
})
export class DemandeUpdateComponent implements OnInit {
  isSaving = false;
  demande: IDemande | null = null;

  residentsSharedCollection: IResident[] = [];
  docteursSharedCollection: IDocteur[] = [];

  editForm: DemandeFormGroup = this.demandeFormService.createDemandeFormGroup();

  constructor(
    protected demandeService: DemandeService,
    protected demandeFormService: DemandeFormService,
    protected residentService: ResidentService,
    protected docteurService: DocteurService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareResident = (o1: IResident | null, o2: IResident | null): boolean => this.residentService.compareResident(o1, o2);

  compareDocteur = (o1: IDocteur | null, o2: IDocteur | null): boolean => this.docteurService.compareDocteur(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ demande }) => {
      this.demande = demande;
      if (demande) {
        this.updateForm(demande);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const demande = this.demandeFormService.getDemande(this.editForm);
    if (demande.id !== null) {
      this.subscribeToSaveResponse(this.demandeService.update(demande));
    } else {
      this.subscribeToSaveResponse(this.demandeService.create(demande));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDemande>>): void {
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

  protected updateForm(demande: IDemande): void {
    this.demande = demande;
    this.demandeFormService.resetForm(this.editForm, demande);

    this.residentsSharedCollection = this.residentService.addResidentToCollectionIfMissing<IResident>(
      this.residentsSharedCollection,
      demande.resident
    );
    this.docteursSharedCollection = this.docteurService.addDocteurToCollectionIfMissing<IDocteur>(
      this.docteursSharedCollection,
      demande.docteur
    );
  }

  protected loadRelationshipsOptions(): void {
    this.residentService
      .query()
      .pipe(map((res: HttpResponse<IResident[]>) => res.body ?? []))
      .pipe(
        map((residents: IResident[]) => this.residentService.addResidentToCollectionIfMissing<IResident>(residents, this.demande?.resident))
      )
      .subscribe((residents: IResident[]) => (this.residentsSharedCollection = residents));

    this.docteurService
      .query()
      .pipe(map((res: HttpResponse<IDocteur[]>) => res.body ?? []))
      .pipe(map((docteurs: IDocteur[]) => this.docteurService.addDocteurToCollectionIfMissing<IDocteur>(docteurs, this.demande?.docteur)))
      .subscribe((docteurs: IDocteur[]) => (this.docteursSharedCollection = docteurs));
  }
}
