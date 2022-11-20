import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { RappelFormService, RappelFormGroup } from './rappel-form.service';
import { IRappel } from '../rappel.model';
import { RappelService } from '../service/rappel.service';
import { IDocteur } from 'app/entities/docteur/docteur.model';
import { DocteurService } from 'app/entities/docteur/service/docteur.service';

@Component({
  selector: 'jhi-rappel-update',
  templateUrl: './rappel-update.component.html',
})
export class RappelUpdateComponent implements OnInit {
  isSaving = false;
  rappel: IRappel | null = null;

  docteursSharedCollection: IDocteur[] = [];

  editForm: RappelFormGroup = this.rappelFormService.createRappelFormGroup();

  constructor(
    protected rappelService: RappelService,
    protected rappelFormService: RappelFormService,
    protected docteurService: DocteurService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDocteur = (o1: IDocteur | null, o2: IDocteur | null): boolean => this.docteurService.compareDocteur(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rappel }) => {
      this.rappel = rappel;
      if (rappel) {
        this.updateForm(rappel);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const rappel = this.rappelFormService.getRappel(this.editForm);
    if (rappel.id !== null) {
      this.subscribeToSaveResponse(this.rappelService.update(rappel));
    } else {
      this.subscribeToSaveResponse(this.rappelService.create(rappel));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRappel>>): void {
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

  protected updateForm(rappel: IRappel): void {
    this.rappel = rappel;
    this.rappelFormService.resetForm(this.editForm, rappel);

    this.docteursSharedCollection = this.docteurService.addDocteurToCollectionIfMissing<IDocteur>(
      this.docteursSharedCollection,
      rappel.docteur
    );
  }

  protected loadRelationshipsOptions(): void {
    this.docteurService
      .query()
      .pipe(map((res: HttpResponse<IDocteur[]>) => res.body ?? []))
      .pipe(map((docteurs: IDocteur[]) => this.docteurService.addDocteurToCollectionIfMissing<IDocteur>(docteurs, this.rappel?.docteur)))
      .subscribe((docteurs: IDocteur[]) => (this.docteursSharedCollection = docteurs));
  }
}
