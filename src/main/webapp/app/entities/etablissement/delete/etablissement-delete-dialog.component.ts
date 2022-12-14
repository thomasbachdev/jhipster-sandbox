import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEtablissement } from '../etablissement.model';
import { EtablissementService } from '../service/etablissement.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './etablissement-delete-dialog.component.html',
})
export class EtablissementDeleteDialogComponent {
  etablissement?: IEtablissement;

  constructor(protected etablissementService: EtablissementService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.etablissementService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
