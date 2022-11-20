import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDocteur } from '../docteur.model';

@Component({
  selector: 'jhi-docteur-detail',
  templateUrl: './docteur-detail.component.html',
})
export class DocteurDetailComponent implements OnInit {
  docteur: IDocteur | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ docteur }) => {
      this.docteur = docteur;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
