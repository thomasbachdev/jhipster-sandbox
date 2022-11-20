import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DocteurComponent } from '../list/docteur.component';
import { DocteurDetailComponent } from '../detail/docteur-detail.component';
import { DocteurUpdateComponent } from '../update/docteur-update.component';
import { DocteurRoutingResolveService } from './docteur-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const docteurRoute: Routes = [
  {
    path: '',
    component: DocteurComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DocteurDetailComponent,
    resolve: {
      docteur: DocteurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DocteurUpdateComponent,
    resolve: {
      docteur: DocteurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DocteurUpdateComponent,
    resolve: {
      docteur: DocteurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(docteurRoute)],
  exports: [RouterModule],
})
export class DocteurRoutingModule {}
