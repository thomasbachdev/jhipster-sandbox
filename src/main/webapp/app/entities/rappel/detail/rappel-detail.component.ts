import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRappel } from '../rappel.model';

@Component({
  selector: 'jhi-rappel-detail',
  templateUrl: './rappel-detail.component.html',
})
export class RappelDetailComponent implements OnInit {
  rappel: IRappel | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rappel }) => {
      this.rappel = rappel;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
