import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RappelComponent } from '../list/rappel.component';
import { RappelDetailComponent } from '../detail/rappel-detail.component';
import { RappelUpdateComponent } from '../update/rappel-update.component';
import { RappelRoutingResolveService } from './rappel-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const rappelRoute: Routes = [
  {
    path: '',
    component: RappelComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RappelDetailComponent,
    resolve: {
      rappel: RappelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RappelUpdateComponent,
    resolve: {
      rappel: RappelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RappelUpdateComponent,
    resolve: {
      rappel: RappelRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(rappelRoute)],
  exports: [RouterModule],
})
export class RappelRoutingModule {}
