import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SoignantComponent } from './list/soignant.component';
import { SoignantDetailComponent } from './detail/soignant-detail.component';
import { SoignantUpdateComponent } from './update/soignant-update.component';
import { SoignantDeleteDialogComponent } from './delete/soignant-delete-dialog.component';
import { SoignantRoutingModule } from './route/soignant-routing.module';

@NgModule({
  imports: [SharedModule, SoignantRoutingModule],
  declarations: [SoignantComponent, SoignantDetailComponent, SoignantUpdateComponent, SoignantDeleteDialogComponent],
})
export class SoignantModule {}
