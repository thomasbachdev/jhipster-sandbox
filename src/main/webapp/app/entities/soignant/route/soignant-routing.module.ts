import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SoignantComponent } from '../list/soignant.component';
import { SoignantDetailComponent } from '../detail/soignant-detail.component';
import { SoignantUpdateComponent } from '../update/soignant-update.component';
import { SoignantRoutingResolveService } from './soignant-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const soignantRoute: Routes = [
  {
    path: '',
    component: SoignantComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SoignantDetailComponent,
    resolve: {
      soignant: SoignantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SoignantUpdateComponent,
    resolve: {
      soignant: SoignantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SoignantUpdateComponent,
    resolve: {
      soignant: SoignantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(soignantRoute)],
  exports: [RouterModule],
})
export class SoignantRoutingModule {}
