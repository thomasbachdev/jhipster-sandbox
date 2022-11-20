import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DocteurComponent } from './list/docteur.component';
import { DocteurDetailComponent } from './detail/docteur-detail.component';
import { DocteurUpdateComponent } from './update/docteur-update.component';
import { DocteurDeleteDialogComponent } from './delete/docteur-delete-dialog.component';
import { DocteurRoutingModule } from './route/docteur-routing.module';

@NgModule({
  imports: [SharedModule, DocteurRoutingModule],
  declarations: [DocteurComponent, DocteurDetailComponent, DocteurUpdateComponent, DocteurDeleteDialogComponent],
})
export class DocteurModule {}
