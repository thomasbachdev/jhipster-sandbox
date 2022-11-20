import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { RappelComponent } from './list/rappel.component';
import { RappelDetailComponent } from './detail/rappel-detail.component';
import { RappelUpdateComponent } from './update/rappel-update.component';
import { RappelDeleteDialogComponent } from './delete/rappel-delete-dialog.component';
import { RappelRoutingModule } from './route/rappel-routing.module';

@NgModule({
  imports: [SharedModule, RappelRoutingModule],
  declarations: [RappelComponent, RappelDetailComponent, RappelUpdateComponent, RappelDeleteDialogComponent],
})
export class RappelModule {}
