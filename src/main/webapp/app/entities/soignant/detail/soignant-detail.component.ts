import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISoignant } from '../soignant.model';

@Component({
  selector: 'jhi-soignant-detail',
  templateUrl: './soignant-detail.component.html',
})
export class SoignantDetailComponent implements OnInit {
  soignant: ISoignant | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ soignant }) => {
      this.soignant = soignant;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
