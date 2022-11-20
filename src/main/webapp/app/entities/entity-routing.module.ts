import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'etablissement',
        data: { pageTitle: 'jhipstersandboxApp.etablissement.home.title' },
        loadChildren: () => import('./etablissement/etablissement.module').then(m => m.EtablissementModule),
      },
      {
        path: 'resident',
        data: { pageTitle: 'jhipstersandboxApp.resident.home.title' },
        loadChildren: () => import('./resident/resident.module').then(m => m.ResidentModule),
      },
      {
        path: 'examen',
        data: { pageTitle: 'jhipstersandboxApp.examen.home.title' },
        loadChildren: () => import('./examen/examen.module').then(m => m.ExamenModule),
      },
      {
        path: 'rappel',
        data: { pageTitle: 'jhipstersandboxApp.rappel.home.title' },
        loadChildren: () => import('./rappel/rappel.module').then(m => m.RappelModule),
      },
      {
        path: 'docteur',
        data: { pageTitle: 'jhipstersandboxApp.docteur.home.title' },
        loadChildren: () => import('./docteur/docteur.module').then(m => m.DocteurModule),
      },
      {
        path: 'soignant',
        data: { pageTitle: 'jhipstersandboxApp.soignant.home.title' },
        loadChildren: () => import('./soignant/soignant.module').then(m => m.SoignantModule),
      },
      {
        path: 'demande',
        data: { pageTitle: 'jhipstersandboxApp.demande.home.title' },
        loadChildren: () => import('./demande/demande.module').then(m => m.DemandeModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
